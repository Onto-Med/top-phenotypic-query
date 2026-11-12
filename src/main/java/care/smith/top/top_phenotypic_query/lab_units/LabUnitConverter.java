package care.smith.top.top_phenotypic_query.lab_units;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;

/**
 * Converts medical laboratory values between UCUM units using BigDecimal precision. Mirrors the
 * behavior of etlutils::convertLabUnits from the MII INTERPOLAR project.
 */
public class LabUnitConverter {

  private static final Logger LOGGER = Logger.getLogger(LabUnitConverter.class.getName());
  private final UcumService ucumService;
  private final Map<String, ParsedUnit> unitCache = new ConcurrentHashMap<>();

  // Metric prefixes matching LAB_UNIT_PREFIX_FACTORS in R
  private static final Map<String, BigDecimal> PREFIX_FACTORS =
      Map.ofEntries(
          Map.entry("", BigDecimal.ONE),
          Map.entry("Y", new BigDecimal("1E24")),
          Map.entry("Z", new BigDecimal("1E21")),
          Map.entry("E", new BigDecimal("1E18")),
          Map.entry("P", new BigDecimal("1E15")),
          Map.entry("T", new BigDecimal("1E12")),
          Map.entry("G", new BigDecimal("1E9")),
          Map.entry("M", new BigDecimal("1E6")),
          Map.entry("k", new BigDecimal("1E3")),
          Map.entry("h", new BigDecimal("1E2")),
          Map.entry("da", new BigDecimal("1E1")),
          Map.entry("d", new BigDecimal("1E-1")),
          Map.entry("c", new BigDecimal("1E-2")),
          Map.entry("m", new BigDecimal("1E-3")),
          Map.entry("u", new BigDecimal("1E-6")),
          Map.entry("n", new BigDecimal("1E-9")),
          Map.entry("p", new BigDecimal("1E-12")),
          Map.entry("f", new BigDecimal("1E-15")),
          Map.entry("a", new BigDecimal("1E-18")),
          Map.entry("z", new BigDecimal("1E-21")),
          Map.entry("y", new BigDecimal("1E-24")));

  private static final String PREFIX_REGEX = "(da|[YZEPTGMkhdcmunpfazy]?)";

  private static final Pattern IU_QUOTIENT_PATTERN =
      Pattern.compile("^" + PREFIX_REGEX + "(U|IU|\\[IU\\]|\\[iU\\])/" + PREFIX_REGEX + "[Ll]$");

  private static final Pattern LOWERCASE_LITER_PATTERN =
      Pattern.compile("(^|/|\\.|\\()" + PREFIX_REGEX + "(l)(?=$|/|\\.|\\)|\\d|\\^|\\*)");

  private enum UnitType {
    UCUM,
    SIMPLE_IU_QUOTIENT
  }

  private static class ParsedUnit {
    final UnitType type;
    final String ucumCode; // Used when type == UCUM
    final BigDecimal iuFactor; // Used when type == SIMPLE_IU_QUOTIENT (normalized to IU/L)

    ParsedUnit(String ucumCode) {
      this.type = UnitType.UCUM;
      this.ucumCode = ucumCode;
      this.iuFactor = null;
    }

    ParsedUnit(BigDecimal iuFactor) {
      this.type = UnitType.SIMPLE_IU_QUOTIENT;
      this.ucumCode = null;
      this.iuFactor = iuFactor;
    }
  }

  /**
   * Initializes the converter using an InputStream pointing to ucum-essence.xml (e.g.,
   * getClass().getResourceAsStream("/ucum-essence.xml")).
   */
  public LabUnitConverter(InputStream ucumEssenceStream) throws UcumException {
    this.ucumService = new UcumEssenceService(ucumEssenceStream);
  }

  /** Converts laboratory values using BigDecimal precision, matching etlutils::convertLabUnits. */
  public BigDecimal convertLabUnits(
      BigDecimal measuredValue,
      String measuredUnitRaw,
      String targetUnitRaw,
      BigDecimal conversionFactor,
      String conversionUnit,
      boolean ignoreErrors,
      String additionalErrorMessage) {

    if (measuredValue == null) {
      return null;
    }

    // 1. No target unit specified -> return the original value unchanged
    if (isMissingUnit(targetUnitRaw)) {
      return measuredValue;
    }

    try {
      ParsedUnit measuredUnit = parseConvertibleUnit(measuredUnitRaw);
      ParsedUnit targetUnit = parseConvertibleUnit(targetUnitRaw);

      // Invalid or missing FHIR units produce null (equivalent to NA in R)
      if (measuredUnit == null || targetUnit == null) {
        return null;
      }

      // 2. Special handling for simple International Unit quotients (U/L, IU/L, m[iU]/L, etc.)
      if (measuredUnit.type == UnitType.SIMPLE_IU_QUOTIENT
          || targetUnit.type == UnitType.SIMPLE_IU_QUOTIENT) {

        if (measuredUnit.type == targetUnit.type) {
          return measuredValue
              .multiply(measuredUnit.iuFactor, MathContext.DECIMAL128)
              .divide(targetUnit.iuFactor, MathContext.DECIMAL128)
              .stripTrailingZeros();
        }

        // Bridge unit families only if conversionFactor and conversionUnit are explicitly provided
        if (conversionFactor != null && !isMissingUnit(conversionUnit)) {
          BigDecimal intermediate =
              convertLabUnits(
                  measuredValue,
                  measuredUnitRaw,
                  conversionUnit,
                  null,
                  null,
                  ignoreErrors,
                  additionalErrorMessage);
          if (intermediate != null) {
            return intermediate
                .multiply(conversionFactor, MathContext.DECIMAL128)
                .stripTrailingZeros();
          }
        }
        return null;
      }

      // 3. Direct UCUM conversion (handles SI units, mm[Hg], m[H2O], osm/L, annotations, etc.)
      if (areDirectlyConvertible(measuredUnit.ucumCode, targetUnit.ucumCode)) {
        Decimal valDecimal = new Decimal(measuredValue.toPlainString());
        Decimal converted =
            ucumService.convert(valDecimal, measuredUnit.ucumCode, targetUnit.ucumCode);
        return new BigDecimal(converted.asDecimal()).stripTrailingZeros();
      }

      // 4. Bridge conversion via intermediate unit and substance-specific factor (e.g., molar mass)
      if (conversionFactor != null && !isMissingUnit(conversionUnit)) {
        BigDecimal intermediate =
            convertLabUnits(
                measuredValue,
                measuredUnitRaw,
                conversionUnit,
                null,
                null,
                ignoreErrors,
                additionalErrorMessage);
        if (intermediate != null) {
          return intermediate
              .multiply(conversionFactor, MathContext.DECIMAL128)
              .stripTrailingZeros();
        }
      }

      return null;

    } catch (Exception e) {
      String errorContext = (additionalErrorMessage == null) ? "" : " " + additionalErrorMessage;
      String warningMessage =
          String.format(
              "Error converting lab units from '%s' to '%s': %s%s",
              measuredUnitRaw, targetUnitRaw, e.getMessage(), errorContext);

      if (!ignoreErrors) {
        LOGGER.severe(warningMessage);
        throw new RuntimeException(warningMessage, e);
      }
      LOGGER.warning(warningMessage);
      return null;
    }
  }

  /** Overloaded convenience method for direct conversions without a bridge factor. */
  public BigDecimal convertLabUnits(
      BigDecimal measuredValue, String measuredUnit, String targetUnit) {
    return convertLabUnits(measuredValue, measuredUnit, targetUnit, null, null, true, null);
  }

  /**
   * Checks whether a unit string is supported by this converter (equivalent to
   * etlutils::isValidUnit).
   */
  public boolean isValidUnit(String unit) {
    return parseConvertibleUnit(unit) != null;
  }

  // --- Parsing & Cleaning Methods (mirroring cleanUnit & parseConvertibleUnitUncached) ---

  private boolean isMissingUnit(String unit) {
    return unit == null || unit.isBlank();
  }

  /**
   * Mirrors cleanUnit() in R: - Removes trailing "/{...}" segments (e.g., "mg/{dL}" -> "mg") -
   * Replaces '*' with '^' - Removes all whitespace
   */
  private String cleanUnit(String unit) {
    String cleaned = unit.replaceAll("/\\{[^/]+\\}$", "");
    cleaned = cleaned.replace("*", "^");
    cleaned = cleaned.replaceAll("\\s+", "");
    return cleaned;
  }

  private ParsedUnit parseConvertibleUnit(String rawUnit) {
    if (isMissingUnit(rawUnit)) {
      return null;
    }
    return unitCache.computeIfAbsent(rawUnit, this::parseConvertibleUnitUncached);
  }

  private ParsedUnit parseConvertibleUnitUncached(String rawUnit) {
    String cleaned = cleanUnit(rawUnit).replace("\u00b5", "u"); // 'µ' -> 'u'

    // Check simple IU quotients first so U/L, IU/L, [IU]/L, and [iU]/L share the exact same
    // equivalence logic as parseSimpleInternationalUnitQuotient() in R.
    Matcher iuMatcher = IU_QUOTIENT_PATTERN.matcher(cleaned);
    if (iuMatcher.matches()) {
      BigDecimal numFactor = PREFIX_FACTORS.get(iuMatcher.group(1));
      BigDecimal denFactor = PREFIX_FACTORS.get(iuMatcher.group(3));
      BigDecimal factor = numFactor.divide(denFactor, MathContext.DECIMAL128);
      return new ParsedUnit(factor);
    }

    // Normalize special R lab patterns for strict UCUM validation:
    // 1. "%{...}" -> "%"
    if (cleaned.matches("^%\\{[^{}]+\\}$")) {
      cleaned = "%";
    }
    // 2. "{...}" -> "1"
    else if (cleaned.matches("^\\{[^{}]+\\}$")) {
      cleaned = "1";
    }
    // 3. Lowercase liter with any valid SI prefix (e.g., "mg/dl" -> "mg/dL", "fl" -> "fL")
    cleaned = LOWERCASE_LITER_PATTERN.matcher(cleaned).replaceAll("$1$2L");
    // Validate against org.fhir.ucum (natively supports SI, mm[Hg], m[H2O], osm/L, etc.)
    if (ucumService.validate(cleaned) == null) {
      return new ParsedUnit(cleaned);
    }
    return null;
  }

  private boolean areDirectlyConvertible(String sourceUnit, String destUnit) {
    try {
      String canonicalSource = ucumService.getCanonicalUnits(sourceUnit);
      String canonicalDest = ucumService.getCanonicalUnits(destUnit);
      return canonicalSource != null && canonicalSource.equals(canonicalDest);
    } catch (UcumException e) {
      return false;
    }
  }
}
