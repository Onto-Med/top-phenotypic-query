package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import care.smith.top.top_phenotypic_query.lab_units.CutoffEvaluationResult;
import care.smith.top.top_phenotypic_query.lab_units.CutoffRule;
import care.smith.top.top_phenotypic_query.lab_units.LabCutoffEvaluator;
import care.smith.top.top_phenotypic_query.lab_units.LabObservation;
import care.smith.top.top_phenotypic_query.lab_units.LabUnitConverter;
import care.smith.top.top_phenotypic_query.lab_units.MatchedObservation;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LabCutoffEvaluatorTest {

  private static LabUnitConverter unitConverter;
  private LabCutoffEvaluator evaluator;

  @BeforeAll
  static void initConverter() throws Exception {
    InputStream ucumStream = LabCutoffEvaluatorTest.class.getResourceAsStream("/ucum-essence.xml");
    assertNotNull(ucumStream, "ucum-essence.xml must be present on the classpath");
    unitConverter = new LabUnitConverter(ucumStream);
  }

  @BeforeEach
  void setUp() {
    evaluator = new LabCutoffEvaluator(unitConverter);
  }

  // --- 1. Unit Conversion & Threshold Evaluation (ULN / LLN) ---

  @Test
  @DisplayName("Convert observation unit to reference unit and match against ULN multiplier")
  void testUnitConversionAndUlnComparison() {
    // 61 mg/dL -> 0.61 g/L, Reference High = 0.10 g/L, Multiplier = 3 -> Threshold = 0.30 g/L
    LabObservation obs =
        new LabObservation(
            "obs-1", "1742-6", "61", "mg/dL", "normal", null, null, new BigDecimal("0.10"), "g/L");
    CutoffRule rule =
        new CutoffRule(
            CutoffRule.ReferenceType.ULN, new BigDecimal("3"), CutoffRule.Operator.GREATER_THAN);

    CutoffEvaluationResult result = evaluator.evaluateCutoff(List.of(obs), rule);

    assertTrue(result.invalidObservations().isEmpty());
    assertEquals(1, result.evaluatedObservations().size());

    MatchedObservation evaluated = result.evaluatedObservations().get(0);
    assertBigDecimalEquals(new BigDecimal("0.61"), evaluated.convertedValue());
    assertEquals("g/L", evaluated.convertedUnit());
    assertBigDecimalEquals(new BigDecimal("0.3"), evaluated.threshold());
    assertTrue(evaluated.matched());
  }

  @Test
  @DisplayName("Evaluate LLN cutoff with <= operator")
  void testLlnComparison() {
    // 2.5 mmol/L <= (3.5 mmol/L * 0.8 = 2.8 mmol/L) -> true
    LabObservation obs =
        new LabObservation(
            "obs-2",
            "2823-3",
            "2.5",
            "mmol/L",
            "normal",
            new BigDecimal("3.5"),
            "mmol/L",
            null,
            null);
    CutoffRule rule =
        new CutoffRule(
            CutoffRule.ReferenceType.LLN,
            new BigDecimal("0.8"),
            CutoffRule.Operator.LESS_THAN_OR_EQUAL);

    CutoffEvaluationResult result = evaluator.evaluateCutoff(List.of(obs), rule);

    assertEquals(1, result.evaluatedObservations().size());
    assertTrue(result.evaluatedObservations().get(0).matched());
    assertBigDecimalEquals(
        new BigDecimal("2.8"), result.evaluatedObservations().get(0).threshold());
  }

  // --- 2. Reference Range Conflict Resolution ---

  @Test
  @DisplayName(
      "Prioritize 'normal' reference range over other types and move unselected rows to invalidObservations")
  void testReferenceRangePriorityNormalOverTherapeutic() {
    LabObservation therapeuticRange =
        new LabObservation(
            "obs-3",
            "1234-5",
            "50",
            "mg/L",
            "therapeutic",
            null,
            null,
            new BigDecimal("100"),
            "mg/L");
    LabObservation normalRange =
        new LabObservation(
            "obs-3", "1234-5", "50", "mg/L", "normal", null, null, new BigDecimal("20"), "mg/L");

    CutoffRule rule =
        new CutoffRule(
            CutoffRule.ReferenceType.ULN, new BigDecimal("2"), CutoffRule.Operator.GREATER_THAN);

    CutoffEvaluationResult result =
        evaluator.evaluateCutoff(List.of(therapeuticRange, normalRange), rule);

    // 'normal' range (20 * 2 = 40) must be chosen -> 50 > 40 is true
    assertEquals(1, result.evaluatedObservations().size());
    assertTrue(result.evaluatedObservations().get(0).matched());
    assertBigDecimalEquals(new BigDecimal("40"), result.evaluatedObservations().get(0).threshold());

    // Unselected duplicate candidate is collected in invalidObservations
    assertEquals(1, result.invalidObservations().size());
    assertEquals("therapeutic", result.invalidObservations().get(0).referenceRangeType());
  }

  @Test
  @DisplayName("Fallback to null referenceRangeType when no 'normal' range exists")
  void testFallbackToNullReferenceRangeType() {
    LabObservation unspecifiedRange =
        new LabObservation(
            "obs-4", "1234-5", "15", "mmol/L", null, null, null, new BigDecimal("10"), "mmol/L");
    CutoffRule rule =
        new CutoffRule(
            CutoffRule.ReferenceType.ULN,
            BigDecimal.ONE,
            CutoffRule.Operator.GREATER_THAN_OR_EQUAL);

    CutoffEvaluationResult result = evaluator.evaluateCutoff(List.of(unspecifiedRange), rule);

    assertEquals(1, result.evaluatedObservations().size());
    assertTrue(result.evaluatedObservations().get(0).matched());
    assertTrue(result.invalidObservations().isEmpty());
  }

  @Test
  @DisplayName(
      "Select the convertible reference range when multiple 'normal' candidates have different units")
  void testSelectConvertibleReferenceRangeAmongMultipleNormals() {
    // Incompatible reference unit (mol/L vs g/L)
    LabObservation incompatibleNormal =
        new LabObservation(
            "obs-5", "1742-6", "50", "mg/dL", "normal", null, null, new BigDecimal("5"), "mmol/L");
    // Compatible reference unit (g/L vs mg/dL)
    LabObservation compatibleNormal =
        new LabObservation(
            "obs-5", "1742-6", "50", "mg/dL", "normal", null, null, new BigDecimal("1.0"), "g/L");

    CutoffRule rule =
        new CutoffRule(CutoffRule.ReferenceType.ULN, BigDecimal.ONE, CutoffRule.Operator.LESS_THAN);

    CutoffEvaluationResult result =
        evaluator.evaluateCutoff(List.of(incompatibleNormal, compatibleNormal), rule);

    // 50 mg/dL = 0.5 g/L < 1.0 g/L -> true
    assertEquals(1, result.evaluatedObservations().size());
    assertTrue(result.evaluatedObservations().get(0).matched());
    assertEquals(1, result.invalidObservations().size());
  }

  // --- 3. Missing Reference Unit & Invalid Rows ---

  @Test
  @DisplayName("Keep original value when reference range has no unit code")
  void testMissingReferenceUnitKeepsOriginalValue() {
    LabObservation obsNoRefUnit =
        new LabObservation(
            "obs-6", "9999-9", "120", "U/L", "normal", null, null, new BigDecimal("40"), null);
    CutoffRule rule =
        new CutoffRule(
            CutoffRule.ReferenceType.ULN, new BigDecimal("2"), CutoffRule.Operator.GREATER_THAN);

    CutoffEvaluationResult result = evaluator.evaluateCutoff(List.of(obsNoRefUnit), rule);

    assertEquals(1, result.evaluatedObservations().size());
    assertBigDecimalEquals(
        new BigDecimal("120"), result.evaluatedObservations().get(0).convertedValue());
    assertEquals("U/L", result.evaluatedObservations().get(0).convertedUnit());
    assertTrue(result.evaluatedObservations().get(0).matched());
  }

  @Test
  @DisplayName(
      "Reject observations with non-numeric values, invalid units, missing reference limits, or incompatible units")
  void testInvalidObservationsFiltering() {
    LabObservation nonNumeric =
        new LabObservation(
            "obs-invalid-1",
            "1742-6",
            "< 5.0",
            "mg/dL",
            "normal",
            null,
            null,
            new BigDecimal("10"),
            "mg/dL");
    LabObservation invalidUnit =
        new LabObservation(
            "obs-invalid-2",
            "1742-6",
            "15",
            "INVALID_UNIT",
            "normal",
            null,
            null,
            new BigDecimal("10"),
            "mg/dL");
    LabObservation missingRefLimit =
        new LabObservation(
            "obs-invalid-3", "1742-6", "15", "mg/dL", "normal", null, null, null, "mg/dL");
    LabObservation incompatibleUnits =
        new LabObservation(
            "obs-invalid-4",
            "1742-6",
            "15",
            "mg/dL",
            "normal",
            null,
            null,
            new BigDecimal("10"),
            "mmol/L");

    CutoffRule rule =
        new CutoffRule(
            CutoffRule.ReferenceType.ULN, BigDecimal.ONE, CutoffRule.Operator.GREATER_THAN);

    CutoffEvaluationResult result =
        evaluator.evaluateCutoff(
            List.of(nonNumeric, invalidUnit, missingRefLimit, incompatibleUnits), rule);

    assertTrue(result.evaluatedObservations().isEmpty());
    assertEquals(4, result.invalidObservations().size());
  }

  // --- Helper for scale-independent BigDecimal assertions ---

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
