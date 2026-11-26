package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import module java.base;

import care.smith.top.top_phenotypic_query.ucum.UnitConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Comprehensive unit test suite verifying the 3-stage conversion pipeline of {@link UnitConverter}:
 *
 * <ol>
 *   <li><b>Normalisation:</b> Cleaning non-standard clinical notations and validating against UCUM.
 *   <li><b>Direct Conversion:</b> Converting values within the same canonical physical dimension.
 *   <li><b>Bidirectional Factor Bridge:</b> Bridging across incompatible dimensions in both
 *       directions.
 * </ol>
 */
class UnitConverterTest {

  private static UnitConverter converter;

  /**
   * Loads the official UCUM essence definitions once before executing any tests. Fails fast if
   * {@code ucum-essence.xml} is missing from the test classpath.
   */
  @BeforeAll
  static void setUp() throws Exception {
    InputStream stream = UnitConverterTest.class.getResourceAsStream("/ucum-essence.xml");
    assertNotNull(stream, "ucum-essence.xml must be present on the classpath");
    converter = new UnitConverter(stream);
  }

  // =========================================================================
  // STEP 1: Normalisation Tests
  // =========================================================================

  @Nested
  @DisplayName("Step 1: Normalisation to UCUM")
  class NormalisationTests {

    /**
     * Verifies that common non-standard clinical notations (lowercase litre, Unicode micro signs,
     * trailing denominator annotations, caret/asterisk exponents, and Enzyme/International Unit
     * variants) are reliably normalised into valid UCUM codes.
     */
    @ParameterizedTest(name = "Raw \"{0}\" -> UCUM \"{1}\"")
    @CsvSource({
      // Lowercase litre variants -> uppercase 'L'
      "'mg/dl', 'mg/dL'",
      "'fl', 'fL'",
      "'l', 'L'",
      // Unicode micro symbols (U+00B5 Micro Sign & U+03BC Greek Mu) -> UCUM 'u'
      "'µl', 'uL'",
      "'µg/l', 'ug/L'",
      "'μmol/l', 'umol/L'",
      // Trailing denominator annotations & special clinical shorthand notations
      "'mg/{dL}', 'mg'",
      "'%{Total}', '%'",
      "'{Ratio}', '1'",
      // Exponents & power-of-ten notations
      "'m^2', 'm2'",
      "'m*2', 'm2'",
      "'10*3/ul', '10*3/uL'",
      "'10^-3/ul', '10*-3/uL'",
      "'10^3.U/l', '10*3.[IU]/L'",
      // Enzyme / International Unit unification -> '[IU]'
      "'U/L', '[IU]/L'",
      "'IU/l', '[IU]/L'",
      "'[iU]/ml', '[IU]/mL'",
      "'mU/g', 'm[IU]/g'",
      "'kU/min', 'k[IU]/min'",
      "'U/(24.h)', '[IU]/(24.h)'",
      "'U', '[IU]'",
      // ASCII and Unicode whitespace removal (e.g., non-breaking space \u00A0 from Excel/PDFs)
      "'  mg / dL  ', 'mg/dL'",
      "'mg\u00A0/\u00A0dL', 'mg/dL'",
      "'mg / {dL} ', 'mg'",
      // Unbracketed pressure units -> mm[Hg], cm[H2O]
      "'mmHg', 'mm[Hg]'",
      "'cmH2O', 'cm[H2O]'",
      "'mH2O', 'm[H2O]'",
      "'mmHg/s', 'mm[Hg]/s'",
      // Osmole variants -> osm
      "'mOsm/L', 'mosm/L'",
      "'mOsm/kg', 'mosm/kg'",
      "'mosmol/l', 'mosm/L'",
      "'Osmol/L', 'osm/L'",
      // Unbracketed parts-per notations -> [ppm], [ppb], [ppt]
      "'ppm', '[ppm]'",
      "'ppb', '[ppb]'",
      // Alternative temperature notations -> Cel, [degF]
      "'degC', 'Cel'",
      "'degF', '[degF]'",
      "'degC/min', 'Cel/min'",
      // Common clinical aliases -> ug, uL, s, h, cm3
      "'mcg/dL', 'ug/dL'",
      "'mg/mcL', 'mg/uL'",
      "'mg/mcl', 'mg/uL'",
      "'mL/sec', 'mL/s'",
      "'mL/sek', 'mL/s'",
      "'mL/hr', 'mL/h'",
      "'mL/hrs', 'mL/h'",
      "'sec^2', 's2'",
      "'cc', 'cm3'",
      "'ccm', 'cm3'",
      "'mg/cc', 'mg/cm3'"
    })
    void normalisesValidUnits(String raw, String expectedUcum) {
      assertEquals(Optional.of(expectedUcum), converter.normaliseToUcum(raw));
      assertTrue(converter.isValidUnit(raw));
    }

    /** Ensures blank strings and unrecognised unit tokens return {@link Optional#empty()}. */
    @ParameterizedTest(name = "Invalid input \"{0}\" -> Optional.empty()")
    @ValueSource(strings = {"", "   ", "invalid_unit", "mg/invalid", "foo/bar", "mg/(dL", "cc^2"})
    void returnsEmptyForInvalidOrBlankUnits(String raw) {
      assertEquals(Optional.empty(), converter.normaliseToUcum(raw));
      assertFalse(converter.isValidUnit(raw));
    }

    /** Ensures {@code null} inputs are handled safely without throwing exceptions. */
    @Test
    void returnsEmptyForNull() {
      assertEquals(Optional.empty(), converter.normaliseToUcum(null));
      assertFalse(converter.isValidUnit(null));
    }
  }

  // =========================================================================
  // STEP 2: Direct Conversion Tests
  // =========================================================================

  @Nested
  @DisplayName("Step 2: Direct UCUM Conversion")
  class DirectConversionTests {

    /**
     * Tests metric mass concentration conversions and confirms that whole numbers retain a
     * non-negative {@link BigDecimal#scale()} so {@link BigDecimal#equals(Object)} succeeds.
     */
    @Test
    void convertsMetricMassConcentrationsWithCleanScale() {
      // 100 mg/dL = 1 g/L
      assertEquals(
          Optional.of(new BigDecimal("1")),
          converter.convertLabUnits(new BigDecimal("100"), "mg/dL", "g/L"));

      // 2 g/L = 2000 mg/L (verifies scale is 0 rather than -3 / 2E+3)
      assertEquals(
          Optional.of(new BigDecimal("2000")),
          converter.convertLabUnits(new BigDecimal("2"), "g/L", "mg/L"));
    }

    /**
     * Verifies physiological pressure unit conversions ({@code mm[Hg]} to {@code kPa}) and metric
     * length conversions ({@code cm} to {@code m}).
     */
    @Test
    void convertsPressureAndLengthUnits() {
      // Systolic blood pressure: 120 mm[Hg] = 15.99864 kPa (UCUM defines 1 mm[Hg] = 133.322 Pa)
      assertEquals(
          Optional.of(new BigDecimal("15.99864")),
          converter.convertLabUnits(new BigDecimal("120"), "mm[Hg]", "kPa"));

      // Body height: 180 cm = 1.8 m
      assertEquals(
          Optional.of(new BigDecimal("1.8")),
          converter.convertLabUnits(new BigDecimal("180"), "cm", "m"));
    }

    /**
     * Verifies non-linear temperature offset conversions ({@code Cel}, {@code [degF]}, {@code K})
     * as well as physiological pressure units ({@code mm[Hg]} to {@code kPa}).
     */
    @Test
    void convertsTemperatureAndPressureUnits() {
      // Body temperature: 37 °C = 98.6 °F = 310.15 K
      assertEquals(
          Optional.of(new BigDecimal("98.6")),
          converter.convertLabUnits(new BigDecimal("37"), "Cel", "[degF]"));

      assertEquals(
          Optional.of(new BigDecimal("310.15")),
          converter.convertLabUnits(new BigDecimal("37"), "Cel", "K"));

      assertEquals(
          Optional.of(new BigDecimal("37")),
          converter.convertLabUnits(new BigDecimal("98.6"), "[degF]", "Cel"));

      // Systolic blood pressure: 120 mm[Hg] = 15.99864 kPa (UCUM defines 1 mm[Hg] = 133.322 Pa)
      assertEquals(
          Optional.of(new BigDecimal("15.99864")),
          converter.convertLabUnits(new BigDecimal("120"), "mm[Hg]", "kPa"));
    }

    /**
     * Verifies that clinical enzyme units ({@code U}) and International Units ({@code [IU]}) are
     * treated as compatible across various denominators and SI prefixes.
     */
    @Test
    void convertsIuAndEnzymeUnitsSeamlessly() {
      // Direct equivalence: 50 U/L == 50 [IU]/L
      assertEquals(
          Optional.of(new BigDecimal("50")),
          converter.convertLabUnits(new BigDecimal("50"), "U/L", "[IU]/L"));

      // Prefix scaling in numerator: 2 kU/min = 2000 [IU]/min
      assertEquals(
          Optional.of(new BigDecimal("2000")),
          converter.convertLabUnits(new BigDecimal("2"), "kU/min", "[IU]/min"));

      // Combined prefix scaling in numerator and denominator: 500 mU/g = 500 [IU]/kg
      assertEquals(
          Optional.of(new BigDecimal("500")),
          converter.convertLabUnits(new BigDecimal("500"), "mU/g", "[IU]/kg"));
    }

    /**
     * Verifies that converting across incompatible physical dimensions (mass vs. substance amount)
     * returns {@link Optional#empty()} when no bridge factor is supplied.
     */
    @Test
    void returnsEmptyForIncompatibleDimensionsWithoutFactor() {
      assertTrue(converter.convertLabUnits(new BigDecimal("100"), "mg/dL", "mmol/L").isEmpty());
      assertTrue(converter.convertDirect(new BigDecimal("100"), "mg/dL", "mmol/L").isEmpty());
    }

    /**
     * Ensures zero values with trailing decimal zeros normalise cleanly to {@link BigDecimal#ZERO}.
     */
    @Test
    void handlesZeroValueCorrectly() {
      assertEquals(
          Optional.of(BigDecimal.ZERO),
          converter.convertLabUnits(new BigDecimal("0.00"), "mg/dL", "g/L"));
    }

    /**
     * Verifies end-to-end conversions using unbracketed clinical aliases ({@code mmHg}, {@code
     * mOsm/kg}, {@code degC}/{@code degF}, {@code mcg}, {@code mcL}, {@code hr}, {@code ccm}).
     */
    @Test
    void convertsClinicalAliasesSeamlessly() {
      // Unbracketed pressure: 120 mmHg == 120 mm[Hg] = 15.99864 kPa
      assertEquals(
          Optional.of(new BigDecimal("15.99864")),
          converter.convertLabUnits(new BigDecimal("120"), "mmHg", "kPa"));

      // Osmolarity: 300 mOsm/kg = 0.3 osm/kg
      assertEquals(
          Optional.of(new BigDecimal("0.3")),
          converter.convertLabUnits(new BigDecimal("300"), "mOsm/kg", "osm/kg"));

      // Temperature aliases: 37 degC = 98.6 degF
      assertEquals(
          Optional.of(new BigDecimal("98.6")),
          converter.convertLabUnits(new BigDecimal("37"), "degC", "degF"));

      // Clinical shorthand aliases: 500 mcg/mcL = 500000 mg/L (500 g/L), 120 mL/hr = 2 mL/min, 2.5
      // ccm = 2.5 mL
      assertEquals(
          Optional.of(new BigDecimal("500000")),
          converter.convertLabUnits(new BigDecimal("500"), "mcg/mcL", "mg/L"));

      assertEquals(
          Optional.of(new BigDecimal("2")),
          converter.convertLabUnits(new BigDecimal("120"), "mL/hr", "mL/min"));
      assertEquals(
          Optional.of(new BigDecimal("2.5")),
          converter.convertLabUnits(new BigDecimal("2.5"), "ccm", "mL"));
    }
  }

  // =========================================================================
  // STEP 3: Bidirectional Factor Conversion Tests
  // =========================================================================

  @Nested
  @DisplayName("Step 3: Bidirectional Factor Conversion")
  class FactorConversionTests {

    /** Glucose conversion factor: 1 mg/dL = 0.0555 mmol/L. */
    private final BigDecimal glucoseFactor = new BigDecimal("0.0555");

    /**
     * Tests forward conversion (Direction A) where both source and target units require metric
     * scaling before and after applying the factor: {@code 1 g/L -> 100 mg/dL -> (* 0.0555) -> 5.55
     * mmol/L -> 5550 umol/L}.
     */
    @Test
    void convertsForwardWithIntermediateUnitScaling() {
      assertEquals(
          Optional.of(new BigDecimal("5550")),
          converter.convertLabUnits(
              new BigDecimal("1"), "g/L", "umol/L", glucoseFactor, "mg/dL", "mmol/L"));
    }

    /**
     * Tests reverse conversion (Direction B) using the exact same factor definition in the opposite
     * direction: {@code 5550 umol/L -> 5.55 mmol/L -> (/ 0.0555) -> 100 mg/dL -> 1 g/L}.
     */
    @Test
    void convertsBackwardWithIntermediateUnitScaling() {
      assertEquals(
          Optional.of(new BigDecimal("1")),
          converter.convertLabUnits(
              new BigDecimal("5550"), "umol/L", "g/L", glucoseFactor, "mg/dL", "mmol/L"));
    }

    /**
     * Tests bidirectional conversion between International Units ({@code U/L}) and mass
     * concentration ({@code ug/L}) using prolactin (1 ng/mL = 21.2 mIU/L).
     */
    @Test
    void convertsBetweenIuAndMassViaFactor() {
      BigDecimal prolactinFactor = new BigDecimal("21.2");

      // Forward: 10 ug/L (= 10 ng/mL) -> (* 21.2) -> 212 m[IU]/L -> 0.212 U/L
      assertEquals(
          Optional.of(new BigDecimal("0.212")),
          converter.convertLabUnits(
              new BigDecimal("10"), "ug/L", "U/L", prolactinFactor, "ng/mL", "m[IU]/L"));

      // Reverse: 0.212 U/L (= 212 m[IU]/L) -> (/ 21.2) -> 10 ng/mL -> 10 ug/L
      assertEquals(
          Optional.of(new BigDecimal("10")),
          converter.convertLabUnits(
              new BigDecimal("0.212"), "U/L", "ug/L", prolactinFactor, "ng/mL", "m[IU]/L"));
    }

    /**
     * Ensures zero factors (which would cause division by zero in reverse mode) and invalid bridge
     * anchor units safely yield {@link Optional#empty()}.
     */
    @Test
    void returnsEmptyWhenFactorOrBridgeUnitsAreInvalid() {
      assertTrue(
          converter
              .convertWithFactor(
                  new BigDecimal("100"), "mg/dL", "mmol/L", BigDecimal.ZERO, "mg/dL", "mmol/L")
              .isEmpty());
      assertTrue(
          converter
              .convertWithFactor(
                  new BigDecimal("100"), "mg/dL", "mmol/L", glucoseFactor, "invalid", "mmol/L")
              .isEmpty());
    }

    /**
     * Verifies bidirectional factor conversion when source, target, or bridge anchor units use
     * clinical shorthand aliases ({@code mcg/dL}, {@code mcg/mcL}).
     */
    @Test
    void convertsBidirectionallyWithClinicalAliases() {
      // Vitamin B12 example: 1 pg/mL = 0.738 pmol/L (equivalent to 1 ng/L = 0.738 pmol/L)
      BigDecimal b12Factor = new BigDecimal("0.738");

      // Forward: 50 mcg/dL (= 500 ug/L = 500000 ng/L) -> (* 0.738) -> 369000 pmol/L -> 369 nmol/L
      assertEquals(
          Optional.of(new BigDecimal("369")),
          converter.convertLabUnits(
              new BigDecimal("50"), "mcg/dL", "nmol/L", b12Factor, "pg/mL", "pmol/L"));

      // Reverse: 369 nmol/L (= 369000 pmol/L) -> (/ 0.738) -> 500000 pg/mL -> 50 mcg/dL
      assertEquals(
          Optional.of(new BigDecimal("50")),
          converter.convertLabUnits(
              new BigDecimal("369"), "nmol/L", "mcg/dL", b12Factor, "pg/mL", "pmol/L"));
    }
  }

  // =========================================================================
  // Edge Cases & Null Handling Tests
  // =========================================================================
  @Nested
  @DisplayName("Edge Cases & Null Handling")
  class EdgeCaseTests {
    /** Verifies that a {@code null} measured value yields {@link Optional#empty()}. */
    @Test
    void returnsEmptyWhenMeasuredValueIsNull() {
      assertTrue(converter.convertLabUnits(null, "mg/dL", "g/L").isEmpty());
    }

    /**
     * Verifies that omitting the target unit ({@code null} or blank) returns the input value
     * unchanged, with trailing zeros stripped.
     */
    @Test
    void returnsNormalisedUnchangedValueWhenTargetUnitIsMissing() {
      assertEquals(
          Optional.of(new BigDecimal("42")),
          converter.convertLabUnits(new BigDecimal("42.00"), "mg/dL", null));
      assertEquals(
          Optional.of(new BigDecimal("42.5")),
          converter.convertLabUnits(new BigDecimal("42.50"), "mg/dL", "   "));
    }

    /**
     * Verifies that direct calls to {@code convertDirect} with {@code null} return empty safely.
     */
    @Test
    void returnsEmptyWhenNullPassedToDirectConversion() {
      assertTrue(converter.convertDirect(null, "mg/dL", "g/L").isEmpty());
    }
  }
}
