package care.smith.top.top_phenotypic_query.ucum;

import module java.base;
import module java.logging;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;

/**
 * Converts medical laboratory values between UCUM units using {@link BigDecimal} precision.
 *
 * <p>Conversion pipeline:
 *
 * <ol>
 *   <li><b>Normalisation:</b> Cleans non-standard clinical notations and validates against UCUM.
 *   <li><b>Direct UCUM Conversion:</b> Converts values within the same canonical dimension.
 *   <li><b>Bidirectional Factor Bridge:</b> Converts across incompatible dimensions via a factor.
 * </ol>
 */
public class UnitConverter {

  private static final Logger LOGGER = Logger.getLogger(UnitConverter.class.getName());
  private final UcumService ucumService;

  /** Thread-safe cache mapping raw unit strings to validated UCUM codes (or empty if invalid). */
  private final Map<String, Optional<String>> ucumCache = new ConcurrentHashMap<>();

  /** Matches any valid UCUM metric SI prefix (yocto 'y' to yotta 'Y', including 'da') or empty. */
  private static final String PREFIX_REGEX = "(da|[YZEPTGMkhdcmunpfazy]?)";

  /**
   * Matches standalone or SI-prefixed International/Enzyme Unit tokens (U, IU, [IU], [iU]) anywhere
   * in an expression without touching lowercase 'u' (micro) or '{...}' annotations.
   */
  private static final Pattern IU_TOKEN_PATTERN =
      Pattern.compile(
          "(^|/|\\.|\\()" + PREFIX_REGEX + "(U|IU|\\[IU\\]|\\[iU\\])(?=$|/|\\.|\\)|\\d|\\^|\\*|-)");

  /** Matches lowercase litre 'l' with an optional SI prefix and converts it to uppercase 'L'. */
  private static final Pattern LOWERCASE_LITRE_PATTERN =
      Pattern.compile("(^|/|\\.|\\()" + PREFIX_REGEX + "(l)(?=$|/|\\.|\\)|\\d|\\^|\\*|-)");

  /** Matches exponents written with '^' or '*' after a unit symbol (e.g., "m^2" -> "m2"). */
  private static final Pattern UNIT_EXPONENT_PATTERN =
      Pattern.compile("([a-zA-Z\\]])[\\^\\*]([+-]?\\d+)(?!\\^)");

  /** Converts power-of-ten carets ("10^3", "10^-3") to UCUM asterisk syntax ("10*3", "10*-3"). */
  private static final Pattern POWER_OF_TEN_CARET_PATTERN =
      Pattern.compile("(^|[/\\.\\(])10\\^([+-]?\\d+)");

  /**
   * Replaces multiplication '*' with UCUM dots '.', preserving valid "10*" exponent expressions.
   */
  private static final Pattern MULTIPLICATION_ASTERISK_PATTERN =
      Pattern.compile("(?<!(?:^|[/\\.\\(])10)\\*");

  /** Matches ALL ASCII and Unicode whitespace characters (including non-breaking spaces \u00A0) */
  private static final Pattern WHITESPACE_PATTERN =
      Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);

  /** Matches trailing denominator annotations (e.g., "mg/{dL}", "mL/min/{1.73_m2}") */
  private static final Pattern ANNOTATION_PATTERN = Pattern.compile("/\\{[^/]+\\}$");

  /** Matches Unicode micro symbols (U+00B5 and U+03BC) */
  private static final Pattern MICRO_PATTERN = Pattern.compile("[\u00b5\u03bc]");

  /**
   * Matches standalone UCUM curly-brace annotations (e.g. "{cells}") with an optional leading "%"
   * (e.g. "%{vol}")
   */
  private static final Pattern BRACE_UNIT_PATTERN = Pattern.compile("^(%?)\\{[^{}]+\\}$");

  /**
   * Matches unbracketed mercury/water pressure units (e.g., "mmHg" -> "mm[Hg]", "cmH2O" ->
   * "cm[H2O]").
   */
  private static final Pattern PRESSURE_UNIT_PATTERN =
      Pattern.compile("(^|/|\\.|\\()" + PREFIX_REGEX + "m(Hg|H2O)(?=$|/|\\.|\\)|\\d|\\^|\\*|-)");

  /** Matches Osmole variants ("Osm", "osmol", "Osmol" -> "osm") with optional SI prefix. */
  private static final Pattern OSMOLE_PATTERN =
      Pattern.compile(
          "(^|/|\\.|\\()" + PREFIX_REGEX + "(?:Osm|osmol|Osmol)(?=$|/|\\.|\\)|\\d|\\^|\\*|-)");

  /** Matches unbracketed parts-per notations ("ppm", "ppb" -> "[ppm]", "[ppb]"). */
  private static final Pattern PARTS_PER_PATTERN =
      Pattern.compile("(^|/|\\.|\\()(ppm|ppb)(?=$|/|\\.|\\)|\\d|\\^|\\*|-)");

  /**
   * Matches common non-UCUM clinical aliases ("mcg" -> "ug", "mcL" -> "uL", "sec"/"sek" -> "s",
   * "hr"/"hrs" -> "h").
   */
  private static final Pattern CLINICAL_ALIAS_PATTERN =
      Pattern.compile("(^|/|\\.|\\()(mcg|mcL|mcl|sec|sek|hrs?)(?=$|/|\\.|\\)|\\d|\\^|\\*|-)");

  /** Matches cubic centimetre aliases ("cc", "ccm" -> "cm3") without trailing exponents. */
  private static final Pattern CUBIC_CM_PATTERN =
      Pattern.compile("(^|/|\\.|\\()ccm?(?=$|/|\\.|\\))");

  /** Matches alternative temperature notations ("degC" -> "Cel", "degF" -> "[degF]"). */
  private static final Pattern TEMPERATURE_ALIAS_PATTERN =
      Pattern.compile("(^|/|\\.|\\()(degC|degF)(?=$|/|\\.|\\)|\\d|\\^|\\*|-)");

  public UnitConverter(InputStream ucumEssenceStream) throws UcumException {
    this.ucumService = new UcumEssenceService(ucumEssenceStream);
  }

  public static boolean equals(String unit1, String unit2) {
    if (unit1 == null && unit2 == null) return true;
    if (unit1 == null || unit2 == null) return false;
    String cleanedUnit1 = WHITESPACE_PATTERN.matcher(unit1).replaceAll("");
    String cleanedUnit2 = WHITESPACE_PATTERN.matcher(unit2).replaceAll("");
    return cleanedUnit1.equals(cleanedUnit2);
  }

  /**
   * Converts a measured laboratory value from a source unit into a target unit using the 3-step
   * pipeline: (1) normalise to UCUM, (2) direct conversion, (3) bidirectional factor bridge.
   */
  public Optional<BigDecimal> convertLabUnits(
      BigDecimal measuredValue,
      String measuredUnitRaw,
      String targetUnitRaw,
      BigDecimal conversionFactor,
      String factorInUnitRaw,
      String factorOutUnitRaw) {

    if (measuredValue == null) {
      return Optional.empty();
    }
    if (isBlank(targetUnitRaw)) {
      return Optional.of(normaliseDecimal(measuredValue));
    }

    try {
      // Step 1: Normalise both raw unit strings into validated UCUM representations
      Optional<String> sourceUcum = normaliseToUcum(measuredUnitRaw);
      Optional<String> targetUcum = normaliseToUcum(targetUnitRaw);

      if (sourceUcum.isEmpty() || targetUcum.isEmpty()) {
        return Optional.empty();
      }

      // Step 2: Attempt direct UCUM conversion, falling back to Step 3 (bidirectional factor
      // bridge)
      return convertDirect(measuredValue, sourceUcum.get(), targetUcum.get())
          .or(() ->
              convertWithFactor(
                  measuredValue,
                  sourceUcum.get(),
                  targetUcum.get(),
                  conversionFactor,
                  factorInUnitRaw,
                  factorOutUnitRaw));

    } catch (Exception e) {
      String warningMessage =
          String.format(
              "Error converting lab units from '%s' to '%s': %s",
              measuredUnitRaw, targetUnitRaw, e.getMessage());
      LOGGER.warning(warningMessage);
      return Optional.empty();
    }
  }

  /** Convenience overload for direct unit conversions without a bridge factor. */
  public Optional<BigDecimal> convertLabUnits(
      BigDecimal measuredValue, String measuredUnit, String targetUnit) {
    return convertLabUnits(measuredValue, measuredUnit, targetUnit, null, null, null);
  }

  /** Checks whether a raw unit string can be normalised into a valid UCUM unit. */
  public boolean isValidUnit(String unit) {
    return normaliseToUcum(unit).isPresent();
  }

  // =========================================================================
  // STEP 1: Normalisation to UCUM
  // =========================================================================

  /** Normalises and validates a raw unit string into a canonical UCUM code (cached). */
  public Optional<String> normaliseToUcum(String rawUnit) {
    if (isBlank(rawUnit)) {
      return Optional.empty();
    }
    return ucumCache.computeIfAbsent(rawUnit, this::normaliseToUcumUncached);
  }

  private Optional<String> normaliseToUcumUncached(String rawUnit) {
    // 1. Remove ALL ASCII and Unicode whitespace characters (including non-breaking spaces \u00A0)
    String cleaned = WHITESPACE_PATTERN.matcher(rawUnit).replaceAll("");

    // 2. Strip trailing denominator annotations (e.g., "mg/{dL}" -> "mg", "mL/min/{1.73_m2}" ->
    // "mL/min")
    cleaned = ANNOTATION_PATTERN.matcher(cleaned).replaceAll("");

    // 3. Replace Unicode micro symbols (U+00B5 and U+03BC) with 'u' before prefix matching
    cleaned = MICRO_PATTERN.matcher(cleaned).replaceAll("u");

    // 4. Normalise unit exponents ("m^2" -> "m2") and power-of-ten carets ("10^3" -> "10*3")
    cleaned = UNIT_EXPONENT_PATTERN.matcher(cleaned).replaceAll("$1$2");
    cleaned = POWER_OF_TEN_CARET_PATTERN.matcher(cleaned).replaceAll("$110*$2");

    // 5. Replace multiplication asterisks '*' with UCUM dots '.', preserving "10*"
    cleaned = MULTIPLICATION_ASTERISK_PATTERN.matcher(cleaned).replaceAll(".");

    // 6. Normalise standalone curly-brace units to "%" (if prefixed with "%") or "1"
    // (dimensionless); otherwise standardise IU and litre symbols within compound units
    Matcher braceMatcher = BRACE_UNIT_PATTERN.matcher(cleaned);
    if (braceMatcher.matches()) {
      cleaned = braceMatcher.group(1).isEmpty() ? "1" : "%";
    } else {
      cleaned = IU_TOKEN_PATTERN.matcher(cleaned).replaceAll("$1$2[IU]");
      cleaned = LOWERCASE_LITRE_PATTERN.matcher(cleaned).replaceAll("$1$2L");
      cleaned = PRESSURE_UNIT_PATTERN.matcher(cleaned).replaceAll("$1$2m[$3]");
      cleaned = OSMOLE_PATTERN.matcher(cleaned).replaceAll("$1$2osm");
      cleaned = PARTS_PER_PATTERN.matcher(cleaned).replaceAll("$1[$2]");
      cleaned = CUBIC_CM_PATTERN.matcher(cleaned).replaceAll("$1cm3");
      cleaned =
          TEMPERATURE_ALIAS_PATTERN
              .matcher(cleaned)
              .replaceAll(m -> m.group(1) + (m.group(2).equals("degC") ? "Cel" : "[degF]"));
      cleaned =
          CLINICAL_ALIAS_PATTERN
              .matcher(cleaned)
              .replaceAll(m -> {
                String replacement =
                    switch (m.group(2)) {
                      case "mcg" -> "ug";
                      case "mcL", "mcl" -> "uL";
                      case "sec", "sek" -> "s";
                      case "hr", "hrs" -> "h";
                      default -> m.group(2);
                    };
                return m.group(1) + replacement;
              });
    }

    // 7. Validate against UCUM service (returns null when valid)
    if (ucumService.validate(cleaned) == null) {
      return Optional.of(cleaned);
    }
    return Optional.empty();
  }

  // =========================================================================
  // STEP 2: Direct UCUM Conversion
  // =========================================================================

  /** Converts a value directly between two validated UCUM units of the same physical dimension. */
  public Optional<BigDecimal> convertDirect(
      BigDecimal value, String sourceUcum, String targetUcum) {
    if (value == null || isBlank(sourceUcum) || isBlank(targetUcum)) {
      return Optional.empty();
    }

    // Fast path: identical UCUM codes require no conversion math
    if (sourceUcum.equals(targetUcum)) {
      return Optional.of(normaliseDecimal(value));
    }

    // Special case: non-linear temperature offsets (Cel, K, [degF]) not supported by org.fhir.ucum
    Optional<BigDecimal> temperatureResult = convertTemperature(value, sourceUcum, targetUcum);
    if (temperatureResult.isPresent()) {
      return temperatureResult;
    }

    try {
      Decimal valDecimal = new Decimal(value.toPlainString());
      Decimal converted = ucumService.convert(valDecimal, sourceUcum, targetUcum);
      return Optional.of(normaliseDecimal(new BigDecimal(converted.asDecimal())));
    } catch (UcumException e) {
      // Thrown by UcumService when units belong to incompatible dimensions (e.g., mass vs. molar)
      return Optional.empty();
    }
  }

  private static final Set<String> TEMPERATURE_UNITS = Set.of("K", "Cel", "[degF]");
  private static final BigDecimal KELVIN_CELSIUS_OFFSET = new BigDecimal("273.15");
  private static final BigDecimal RANKINE_FAHRENHEIT_OFFSET = new BigDecimal("459.67");
  private static final BigDecimal FIVE = new BigDecimal("5");
  private static final BigDecimal NINE = new BigDecimal("9");

  /**
   * Handles conversions between Kelvin ({@code K}), degrees Celsius ({@code Cel}), and degrees
   * Fahrenheit ({@code [degF]}) via Kelvin as common intermediate anchor.
   */
  private Optional<BigDecimal> convertTemperature(
      BigDecimal value, String sourceUcum, String targetUcum) {
    if (!TEMPERATURE_UNITS.contains(sourceUcum) || !TEMPERATURE_UNITS.contains(targetUcum)) {
      return Optional.empty();
    }

    // 1. Convert source unit to Kelvin (K)
    BigDecimal kelvin =
        switch (sourceUcum) {
          case "K" -> value;
          case "Cel" -> value.add(KELVIN_CELSIUS_OFFSET, MathContext.DECIMAL128);
          case "[degF]" ->
              value
                  .add(RANKINE_FAHRENHEIT_OFFSET, MathContext.DECIMAL128)
                  .multiply(FIVE, MathContext.DECIMAL128)
                  .divide(NINE, MathContext.DECIMAL128);
          default -> throw new IllegalStateException("Unexpected temperature unit: " + sourceUcum);
        };

    // 2. Convert Kelvin (K) to target unit
    BigDecimal result =
        switch (targetUcum) {
          case "K" -> kelvin;
          case "Cel" -> kelvin.subtract(KELVIN_CELSIUS_OFFSET, MathContext.DECIMAL128);
          case "[degF]" ->
              kelvin
                  .multiply(NINE, MathContext.DECIMAL128)
                  .divide(FIVE, MathContext.DECIMAL128)
                  .subtract(RANKINE_FAHRENHEIT_OFFSET, MathContext.DECIMAL128);
          default -> throw new IllegalStateException("Unexpected temperature unit: " + targetUcum);
        };

    return Optional.of(normaliseDecimal(result));
  }

  // =========================================================================
  // STEP 3: Factor-based Bridge Conversion (Bidirectional)
  // =========================================================================

  /**
   * Bridges a conversion across incompatible unit dimensions using a conversion factor: - Forward:
   * sourceUcum -> factorInUnit -> (* factor) -> factorOutUnit -> targetUcum - Reverse: sourceUcum
   * -> factorOutUnit -> (/ factor) -> factorInUnit -> targetUcum
   */
  public Optional<BigDecimal> convertWithFactor(
      BigDecimal value,
      String sourceUcum,
      String targetUcum,
      BigDecimal factor,
      String factorInUnitRaw,
      String factorOutUnitRaw) {

    if (value == null
        || isBlank(sourceUcum)
        || isBlank(targetUcum)
        || factor == null
        || factor.compareTo(BigDecimal.ZERO) == 0) {
      return Optional.empty();
    }

    Optional<String> factorInUcum = normaliseToUcum(factorInUnitRaw);
    Optional<String> factorOutUcum = normaliseToUcum(factorOutUnitRaw);

    if (factorInUcum.isEmpty() || factorOutUcum.isEmpty()) {
      return Optional.empty();
    }

    // Direction A (Forward): source -> factorIn -> (* factor) -> factorOut -> target
    Optional<BigDecimal> forwardResult =
        convertDirect(value, sourceUcum, factorInUcum.get())
            .map(inValue -> inValue.multiply(factor, MathContext.DECIMAL128))
            .flatMap(bridged -> convertDirect(bridged, factorOutUcum.get(), targetUcum));

    // Direction B (Reverse): source -> factorOut -> (/ factor) -> factorIn -> target
    return forwardResult.or(() ->
        convertDirect(value, sourceUcum, factorOutUcum.get())
            .map(outValue -> outValue.divide(factor, MathContext.DECIMAL128))
            .flatMap(bridged -> convertDirect(bridged, factorInUcum.get(), targetUcum)));
  }

  // =========================================================================
  // Helpers
  // =========================================================================
  private boolean isBlank(String unit) {
    return unit == null || unit.isBlank();
  }

  /** Strips trailing zeros while keeping scale >= 0 so BigDecimal#equals behaves predictably. */
  private BigDecimal normaliseDecimal(BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    BigDecimal stripped = value.stripTrailingZeros();
    return (stripped.scale() < 0) ? stripped.setScale(0, RoundingMode.UNNECESSARY) : stripped;
  }
}
