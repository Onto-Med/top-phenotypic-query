package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import care.smith.top.top_phenotypic_query.lab_units.LabUnitConverter;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LabUnitConverterTest {

  private static LabUnitConverter converter;

  @BeforeAll
  static void setUp() throws Exception {
    // Load ucum-essence.xml from src/main/resources or src/test/resources
    InputStream ucumStream = LabUnitConverterTest.class.getResourceAsStream("/ucum-essence.xml");
    assertNotNull(ucumStream, "ucum-essence.xml must be present on the classpath");
    converter = new LabUnitConverter(ucumStream);
  }

  // --- 1. Direct Physical & SI Conversions ---

  @Test
  @DisplayName("Convert standard mass concentration units (mg/dL to g/L)")
  void testDirectMassConcentrationConversion() {
    BigDecimal result = converter.convertLabUnits(new BigDecimal("100"), "mg/dL", "g/L");
    assertBigDecimalEquals(new BigDecimal("1"), result);
  }

  @Test
  @DisplayName("Convert lowercase liter units across all SI prefixes (mg/dl, fl, cl)")
  void testLowercaseLiterNormalization() {
    // mg/dl -> g/l
    assertBigDecimalEquals(
        new BigDecimal("1"), converter.convertLabUnits(new BigDecimal("100"), "mg/dl", "g/l"));

    // Standalone femtoliter (fl -> fL, e.g., for MCV)
    assertBigDecimalEquals(
        new BigDecimal("90"), converter.convertLabUnits(new BigDecimal("90"), "fl", "fL"));

    // Standalone centiliter (cl -> mL)
    assertBigDecimalEquals(
        new BigDecimal("50"), converter.convertLabUnits(new BigDecimal("5"), "cl", "mL"));
  }

  @Test
  @DisplayName("Convert molar concentration units with micro symbol (µmol/L to mmol/L)")
  void testMicroSymbolNormalization() {
    BigDecimal result = converter.convertLabUnits(new BigDecimal("150"), "µmol/L", "mmol/L");
    assertBigDecimalEquals(new BigDecimal("0.15"), result);
  }

  @Test
  @DisplayName("Handle very small numbers without scientific notation issues")
  void testVerySmallNumbers() {
    BigDecimal result = converter.convertLabUnits(new BigDecimal("0.00001"), "g/L", "mg/L");
    assertBigDecimalEquals(new BigDecimal("0.01"), result);
  }

  // --- 2. cleanUnit() Normalization Cases ---

  @Test
  @DisplayName("Strip trailing /{...} annotation segments (mg/{dL} to g)")
  void testTrailingCurlyBraceSegmentRemoval() {
    BigDecimal result = converter.convertLabUnits(new BigDecimal("500"), "mg/{dL}", "g");
    assertBigDecimalEquals(new BigDecimal("0.5"), result);
  }

  @Test
  @DisplayName("Normalize whitespace and caret/asterisk powers (10*9 / L to 10^6/uL)")
  void testPowerAndWhitespaceNormalization() {
    BigDecimal result = converter.convertLabUnits(new BigDecimal("5"), "10*9 / L", "10^6/uL");
    assertBigDecimalEquals(new BigDecimal("0.005"), result);
  }

  // --- 3. Special UCUM Lab Families (Pressure, Osmoles, Annotations) ---

  @Test
  @DisplayName("Convert pressure columns (mm[Hg] to kPa)")
  void testPressureColumnConversion() {
    BigDecimal result = converter.convertLabUnits(new BigDecimal("100"), "mm[Hg]", "kPa");
    assertNotNull(result);
    assertEquals(0, new BigDecimal("13.3322").compareTo(result.setScale(4, RoundingMode.HALF_UP)));
  }

  @Test
  @DisplayName("Convert osmolality units (mosm/L to osm/L)")
  void testOsmoleConversion() {
    BigDecimal result = converter.convertLabUnits(new BigDecimal("300"), "mosm/L", "osm/L");
    assertBigDecimalEquals(new BigDecimal("0.3"), result);
  }

  @Test
  @DisplayName("Convert standalone annotations ({cells} to 1 and %{vol} to %)")
  void testStandaloneAnnotations() {
    BigDecimal cellsResult = converter.convertLabUnits(new BigDecimal("12"), "{cells}", "1");
    assertBigDecimalEquals(new BigDecimal("12"), cellsResult);

    BigDecimal percentResult = converter.convertLabUnits(new BigDecimal("45"), "%{vol}", "%");
    assertBigDecimalEquals(new BigDecimal("45"), percentResult);
  }

  // --- 4. International Units & Enzyme Activity (U/L, IU/L, [iU]/mL) ---

  @Test
  @DisplayName("Convert across U/L, IU/L, [IU]/L, and [iU]/mL as a shared IU family")
  void testInternationalUnitEquivalence() {
    assertBigDecimalEquals(
        new BigDecimal("2.5"), converter.convertLabUnits(new BigDecimal("2500"), "mIU/L", "IU/L"));

    assertBigDecimalEquals(
        new BigDecimal("50"), converter.convertLabUnits(new BigDecimal("50"), "U/L", "IU/L"));

    assertBigDecimalEquals(
        new BigDecimal("15"), converter.convertLabUnits(new BigDecimal("15"), "uU/mL", "m[iU]/L"));
  }

  @Test
  @DisplayName("Bridge IU family to mass concentration using conversionFactor and conversionUnit")
  void testBridgeFromInternationalUnitsToMass() {
    BigDecimal result =
        converter.convertLabUnits(
            new BigDecimal("100"),
            "mIU/L",
            "ng/mL",
            new BigDecimal("0.5"),
            "IU/L",
            true,
            "for LOINC code 1234-5");
    assertBigDecimalEquals(new BigDecimal("0.05"), result);
  }

  // --- 5. Bridge Conversions (with conversionFactor & conversionUnit) ---

  @Test
  @DisplayName(
      "Convert incompatible unit families using bridge unit and factor (Creatinine mg/dL to umol/L)")
  void testBridgeConversionWithFactor() {
    BigDecimal result =
        converter.convertLabUnits(
            new BigDecimal("1.2"),
            "mg/dL",
            "umol/L",
            new BigDecimal("8.84"),
            "mg/L",
            true,
            "for LOINC code 2160-0");
    assertBigDecimalEquals(new BigDecimal("106.08"), result);
  }

  @Test
  @DisplayName("Return null when units are incompatible and no bridge factor is provided")
  void testIncompatibleUnitsWithoutFactor() {
    BigDecimal result = converter.convertLabUnits(new BigDecimal("1.2"), "mg/dL", "umol/L");
    assertNull(result);
  }

  // --- 6. Validation & Edge Cases ---

  @Test
  @DisplayName("Validate supported and unsupported unit strings via isValidUnit()")
  void testIsValidUnit() {
    assertTrue(converter.isValidUnit("mmol/L"));
    assertTrue(converter.isValidUnit("10*9/L"));
    assertTrue(converter.isValidUnit("U/L"));
    assertTrue(converter.isValidUnit("m[iU]/L"));
    assertTrue(converter.isValidUnit("mm[Hg]"));
    assertTrue(converter.isValidUnit("fl"));

    assertFalse(converter.isValidUnit("foobar"));
    assertFalse(converter.isValidUnit(null));
    assertFalse(converter.isValidUnit(""));
    assertFalse(converter.isValidUnit("NA"));
  }

  @Test
  @DisplayName("Return original value unchanged when target unit is missing or NA")
  void testMissingTargetUnitReturnsOriginalValue() {
    BigDecimal input = new BigDecimal("42.5");

    assertBigDecimalEquals(input, converter.convertLabUnits(input, "mg/dL", null));
    assertBigDecimalEquals(input, converter.convertLabUnits(input, "mg/dL", ""));
    assertBigDecimalEquals(input, converter.convertLabUnits(input, "mg/dL", "   "));
  }

  @Test
  @DisplayName("Return null when measured value or measured unit is missing")
  void testMissingMeasuredValueOrUnit() {
    assertNull(converter.convertLabUnits(null, "mg/dL", "g/L"));
    assertNull(converter.convertLabUnits(new BigDecimal("10"), null, "g/L"));
    assertNull(converter.convertLabUnits(new BigDecimal("10"), "NA", "g/L"));
  }

  @Test
  @DisplayName("Return null for invalid UCUM unit codes")
  void testInvalidUcumUnits() {
    assertNull(converter.convertLabUnits(new BigDecimal("10"), "foobar", "g/L"));
    assertNull(converter.convertLabUnits(new BigDecimal("10"), "mg/dL", "foobar"));
  }

  // --- Helper for scale-independent BigDecimal assertions ---

  @Test
  @DisplayName("Convert water column pressure units (cm[H2O] to Pa)")
  void testWaterColumnPressureConversion() {
    // In UCUM (§44 Table 18): 1 m[H2O] = 9.80665 kPa = 9806.65 Pa -> 10 cm[H2O] = 980.665 Pa
    BigDecimal result = converter.convertLabUnits(new BigDecimal("10"), "cm[H2O]", "Pa");
    assertBigDecimalEquals(new BigDecimal("980.665"), result);
  }

  @Test
  @DisplayName("Convert osmolality directly to molarity (mosm/L to mmol/L and mosm/kg to mmol/kg)")
  void testOsmoleToMoleConversion() {
    // In UCUM (§45 Table 19): 1 osm = 1 mol -> 290 mosm/L = 290 mmol/L
    BigDecimal perLiterResult =
        converter.convertLabUnits(new BigDecimal("290"), "mosm/L", "mmol/L");
    assertBigDecimalEquals(new BigDecimal("290"), perLiterResult);

    // 285 mosm/kg = 0.285 mol/kg
    BigDecimal perKgResult = converter.convertLabUnits(new BigDecimal("285"), "mosm/kg", "mol/kg");
    assertBigDecimalEquals(new BigDecimal("0.285"), perKgResult);
  }

  private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
    assertNotNull(actual, "Expected non-null BigDecimal result");
    assertEquals(
        0,
        expected.compareTo(actual),
        () ->
            String.format(
                "Expected <%s> but was <%s>", expected.toPlainString(), actual.toPlainString()));
  }
}
