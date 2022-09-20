package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.Expression;
import care.smith.top.backend.model.ExpressionFunction;
import care.smith.top.backend.model.ExpressionFunction.NotationEnum;
import care.smith.top.backend.model.ExpressionValue;
import care.smith.top.backend.model.NumberValue;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.Values;
import care.smith.top.top_phenotypic_query.search.CompositeSearch;

public class BMIAgeTest extends AbstractTest {

  @Test
  public void test() {
    Phenotype weight = getPhenotype("Weight");
    Phenotype height = getPhenotype("Height");

    Phenotype age = getPhenotype("Age");
    Phenotype young = getInterval("Young", age, 18, 34);
    Phenotype old = getIntervalMin("Old", age, 34);

    Phenotype bmi = getPhenotype("BMI", getBMIExpression());
    Phenotype bmi19_25 = getInterval("BMI19_25", bmi, 19, 25);
    Phenotype bmi19_27 = getInterval("BMI19_27", bmi, 19, 27);
    Phenotype bmi25_30 = getInterval("BMI25_30", bmi, 25, 30);
    Phenotype bmi27_30 = getInterval("BMI27_30", bmi, 27, 30);

    Phenotype finding = getPhenotype("Finding", getFindingExpression());
    //    Phenotype normalWeight = getRestriction("Normal_weight", finding, 0, 1);
    Phenotype overWeight = getInterval("Overweight", finding, 1, 2);

    ExpressionFunction defAgrFunc =
        new ExpressionFunction().id("last").minArgumentNumber(1).notation(NotationEnum.PREFIX);

    QueryCriterion cri =
        new QueryCriterion()
            .exclusion(false)
            .defaultAggregationFunction(defAgrFunc)
            .subject(overWeight)
            .dateTimeRestriction(getDTR(2000));

    Map<String, Phenotype> phenotypes =
        getPhenotypeMap(
            weight,
            height,
            age,
            young,
            old,
            bmi,
            bmi19_25,
            bmi19_27,
            bmi25_30,
            bmi27_30,
            finding,
            overWeight);

    ResultSet initialRS = getResultSet();
    System.out.println(initialRS);

    CompositeSearch search = new CompositeSearch(null, cri, getResultSet(), phenotypes);
    ResultSet finalRS = search.execute();
    System.out.println(finalRS);

    assertEquals(Set.of("Subject1"), finalRS.getSubjectIds());

    Phenotypes phes = finalRS.getPhenotypes("Subject1");
    assertEquals(phenotypes.keySet(), phes.getPhenotypeNames());

    assertFalse(getValue("Old", phes).asBooleanValue().getValue());
    assertTrue(getValue("Young", phes).asBooleanValue().getValue());

    assertEquals(new BigDecimal("25.95155709342561"), getValue("BMI", phes).getValueDecimal());
    assertFalse(getValue("BMI19_25", phes).asBooleanValue().getValue());
    assertTrue(getValue("BMI19_27", phes).asBooleanValue().getValue());
    assertTrue(getValue("BMI25_30", phes).asBooleanValue().getValue());
    assertFalse(getValue("BMI27_30", phes).asBooleanValue().getValue());

    assertEquals(BigDecimal.ONE, getValue("Finding", phes).getValueDecimal());
    assertTrue(getValue("Overweight", phes).asBooleanValue().getValue());
  }

  private static Value getValue(String pheName, Phenotypes phes) {
    return phes.getValues(pheName, getDTR(2000)).getValues().get(0);
  }

  private static Expression getValue(int value) {
    NumberValue v = new NumberValue().value(BigDecimal.valueOf(value));
    v.setDataType(DataType.NUMBER);
    return new Expression().value(new ExpressionValue().value(v));
  }

  private static Expression getBMIExpression() {
    return new Expression()
        .function("divide")
        .addArgumentsItem(new Expression().entityId("Weight"))
        .addArgumentsItem(
            new Expression()
                .function("power")
                .addArgumentsItem(new Expression().entityId("Height"))
                .addArgumentsItem(getValue(2)));
  }

  private static Expression getFindingExpression() {
    Expression youngAndBmi19_25 =
        new Expression()
            .function("and")
            .addArgumentsItem(new Expression().entityId("Young"))
            .addArgumentsItem(new Expression().entityId("BMI19_25"));
    Expression oldAndBmi19_27 =
        new Expression()
            .function("and")
            .addArgumentsItem(new Expression().entityId("Old"))
            .addArgumentsItem(new Expression().entityId("BMI19_27"));
    Expression normalWeight =
        new Expression()
            .function("or")
            .addArgumentsItem(youngAndBmi19_25)
            .addArgumentsItem(oldAndBmi19_27);

    Expression youngAndBmi25_30 =
        new Expression()
            .function("and")
            .addArgumentsItem(new Expression().entityId("Young"))
            .addArgumentsItem(new Expression().entityId("BMI25_30"));
    Expression oldAndBmi27_30 =
        new Expression()
            .function("and")
            .addArgumentsItem(new Expression().entityId("Old"))
            .addArgumentsItem(new Expression().entityId("BMI27_30"));
    Expression overweight =
        new Expression()
            .function("or")
            .addArgumentsItem(youngAndBmi25_30)
            .addArgumentsItem(oldAndBmi27_30);

    return new Expression()
        .function("switch")
        .addArgumentsItem(normalWeight)
        .addArgumentsItem(getValue(0))
        .addArgumentsItem(overweight)
        .addArgumentsItem(getValue(1));
  }

  private static ResultSet getResultSet() {
    Values weightVals1 = new Values("Weight");
    weightVals1.setDecimalValues(getDTR(2000), new DecimalValue(75));
    Values heightVals1 = new Values("Height");
    heightVals1.setDecimalValues(getDTR(2000), new DecimalValue(1.7));
    Values ageVals1 = new Values("Age");
    ageVals1.setDecimalValues(getDTR(2000), new DecimalValue(20));

    Values weightVals2 = new Values("Weight");
    weightVals2.setDecimalValues(getDTR(2000), new DecimalValue(85));
    Values heightVals2 = new Values("Height");
    heightVals2.setDecimalValues(getDTR(2000), new DecimalValue(1.8));
    Values ageVals2 = new Values("Age");
    ageVals2.setDecimalValues(getDTR(2000), new DecimalValue(40));

    Phenotypes phes1 = new Phenotypes("Subject1");
    phes1.setValues(weightVals1, heightVals1, ageVals1);

    Phenotypes phes2 = new Phenotypes("Subject2");
    phes2.setValues(weightVals2, heightVals2, ageVals2);

    ResultSet rs = new ResultSet();
    rs.setPhenotypes(phes1, phes2);
    return rs;
  }
}
