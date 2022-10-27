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
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.search.CompositeSearch;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Values;

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

    for (Phenotype p : phenotypes.values()) {
      if (Phenotypes.isRestriction(p)) p.setExpression(Expressions.restrictionToExpression(p));
    }

    ResultSet initialRS = getResultSet();

    CompositeSearch search = new CompositeSearch(null, cri, getResultSet(), phenotypes);
    ResultSet finalRS = search.execute();

    assertEquals(Set.of("Subject1"), finalRS.getSubjectIds());

    SubjectPhenotypes phes = finalRS.getPhenotypes("Subject1");
    assertEquals(phenotypes.keySet(), phes.getPhenotypeNames());

    assertFalse(Values.getBooleanValue(getValue("Old", phes)));
    assertTrue(Values.getBooleanValue(getValue("Young", phes)));

    assertEquals(new BigDecimal("25.95155709342561"), Values.getNumberValue(getValue("BMI", phes)));
    assertFalse(Values.getBooleanValue(getValue("BMI19_25", phes)));
    assertTrue(Values.getBooleanValue(getValue("BMI19_27", phes)));
    assertTrue(Values.getBooleanValue(getValue("BMI25_30", phes)));
    assertFalse(Values.getBooleanValue(getValue("BMI27_30", phes)));

    assertEquals(BigDecimal.ONE, Values.getNumberValue(getValue("Finding", phes)));
    assertTrue(Values.getBooleanValue(getValue("Overweight", phes)));
  }

  private static ResultSet getResultSet() {
    PhenotypeValues weightVals1 = new PhenotypeValues("Weight");
    weightVals1.setValues(getDTR(2000), Values.newValue(75));
    PhenotypeValues heightVals1 = new PhenotypeValues("Height");
    heightVals1.setValues(getDTR(2000), Values.newValue(1.70));
    PhenotypeValues ageVals1 = new PhenotypeValues("Age");
    ageVals1.setValues(getDTR(2000), Values.newValue(20));

    PhenotypeValues weightVals2 = new PhenotypeValues("Weight");
    weightVals2.setValues(getDTR(2000), Values.newValue(85));
    PhenotypeValues heightVals2 = new PhenotypeValues("Height");
    heightVals2.setValues(getDTR(2000), Values.newValue(1.80));
    PhenotypeValues ageVals2 = new PhenotypeValues("Age");
    ageVals2.setValues(getDTR(2000), Values.newValue(40));

    SubjectPhenotypes phes1 = new SubjectPhenotypes("Subject1");
    phes1.setValues(weightVals1, heightVals1, ageVals1);

    SubjectPhenotypes phes2 = new SubjectPhenotypes("Subject2");
    phes2.setValues(weightVals2, heightVals2, ageVals2);

    ResultSet rs = new ResultSet();
    rs.setPhenotypes(phes1, phes2);
    return rs;
  }
}
