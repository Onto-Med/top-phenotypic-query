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
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
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

    SubjectPhenotypes phes = finalRS.getPhenotypes("Subject1");
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
    PhenotypeValues weightVals1 = new PhenotypeValues("Weight");
    weightVals1.setDecimalValues(getDTR(2000), new DecimalValue(75));
    PhenotypeValues heightVals1 = new PhenotypeValues("Height");
    heightVals1.setDecimalValues(getDTR(2000), new DecimalValue(1.70));
    PhenotypeValues ageVals1 = new PhenotypeValues("Age");
    ageVals1.setDecimalValues(getDTR(2000), new DecimalValue(20));

    PhenotypeValues weightVals2 = new PhenotypeValues("Weight");
    weightVals2.setDecimalValues(getDTR(2000), new DecimalValue(85));
    PhenotypeValues heightVals2 = new PhenotypeValues("Height");
    heightVals2.setDecimalValues(getDTR(2000), new DecimalValue(1.80));
    PhenotypeValues ageVals2 = new PhenotypeValues("Age");
    ageVals2.setDecimalValues(getDTR(2000), new DecimalValue(40));

    SubjectPhenotypes phes1 = new SubjectPhenotypes("Subject1");
    phes1.setValues(weightVals1, heightVals1, ageVals1);

    SubjectPhenotypes phes2 = new SubjectPhenotypes("Subject2");
    phes2.setValues(weightVals2, heightVals2, ageVals2);

    ResultSet rs = new ResultSet();
    rs.setPhenotypes(phes1, phes2);
    return rs;
  }
}
