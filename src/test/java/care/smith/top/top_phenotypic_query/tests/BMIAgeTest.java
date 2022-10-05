package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Phenotype;
import care.smith.top.model.QueryCriterion;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.Values;
import care.smith.top.top_phenotypic_query.search.CompositeSearch;

public class BMIAgeTest extends AbstractTest {

  @Test
  public void test() {
    QueryCriterion cri =
        new QueryCriterion()
            .inclusion(true)
            .defaultAggregationFunctionId(defAgrFunc.getId())
            .subjectId(overWeight.getId())
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

  private static ResultSet getResultSet() {
    Values weightVals1 = new Values("Weight");
    weightVals1.setDecimalValues(getDTR(2000), new DecimalValue(75));
    Values heightVals1 = new Values("Height");
    heightVals1.setDecimalValues(getDTR(2000), new DecimalValue(1.70));
    Values ageVals1 = new Values("Age");
    ageVals1.setDecimalValues(getDTR(2000), new DecimalValue(20));

    Values weightVals2 = new Values("Weight");
    weightVals2.setDecimalValues(getDTR(2000), new DecimalValue(85));
    Values heightVals2 = new Values("Height");
    heightVals2.setDecimalValues(getDTR(2000), new DecimalValue(1.80));
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
