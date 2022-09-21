package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.ExpressionFunction;
import care.smith.top.backend.model.ExpressionFunction.NotationEnum;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.SQLAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;

public class FullBMIAgeTest extends BMIAgeTest {

  static Phenotype age = getPhenotype("Age", "http://loinc.org", "30525-0");
  static Phenotype young = getInterval("Young", age, 18, 34);
  static Phenotype old = getIntervalMin("Old", age, 34);
  static Phenotype sex = getPhenotype("Sex", "http://loinc.org", "46098-0", DataType.STRING);
  static Phenotype female =
      getRestriction("Female", sex, "http://hl7.org/fhir/administrative-gender|female");
  static Phenotype weight = getPhenotype("Weight", "http://loinc.org", "3141-9");
  static Phenotype height = getPhenotype("Height", "http://loinc.org", "3137-7");
  static Phenotype bmi = getPhenotype("BMI", getBMIExpression());
  static Phenotype bmi19_25 = getInterval("BMI19_25", bmi, 19, 25);
  static Phenotype bmi19_27 = getInterval("BMI19_27", bmi, 19, 27);
  static Phenotype bmi25_30 = getInterval("BMI25_30", bmi, 25, 30);
  static Phenotype bmi27_30 = getInterval("BMI27_30", bmi, 27, 30);
  static Phenotype finding = getPhenotype("Finding", getFindingExpression());
  static Phenotype overWeight = getRestriction("Overweight", finding, 1);

  static Map<String, Phenotype> phenotypes =
      getPhenotypeMap(
          age,
          young,
          old,
          sex,
          female,
          weight,
          height,
          bmi,
          bmi19_25,
          bmi19_27,
          bmi25_30,
          bmi27_30,
          finding,
          overWeight);

  static ExpressionFunction defAgrFunc =
      new ExpressionFunction().id("last").minArgumentNumber(1).notation(NotationEnum.PREFIX);

  @Override
  @Test
  public void test() {
    QueryCriterion cri1 =
        new QueryCriterion()
            .exclusion(false)
            .defaultAggregationFunction(defAgrFunc)
            .subject(overWeight)
            .dateTimeRestriction(getDTR(2000));
    QueryCriterion cri2 = new QueryCriterion().exclusion(false).subject(female);
    Query query = new Query().addCriteriaItem(cri1).addCriteriaItem(cri2);
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test3.yml");
    assertNotNull(configFile);
    DataAdapterConfig config = DataAdapterConfig.getInstance(configFile.getPath());
    SQLAdapter adapter = new SQLAdapter(config);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    System.out.println(rs);

    assertEquals(Set.of("1"), rs.getSubjectIds());

    Phenotypes phes = rs.getPhenotypes("1");
    Set<String> phesExpected = new HashSet<>(phenotypes.keySet());
    phesExpected.add("birthdate");
    assertEquals(phesExpected, phes.getPhenotypeNames());

    assertEquals(new BigDecimal(20), getValue("Age", phes).getValueDecimal());
    assertFalse(getValue("Old", phes).asBooleanValue().getValue());
    assertTrue(getValue("Young", phes).asBooleanValue().getValue());

    assertEquals("female", getValue("Sex", phes).asStringValue().getValue());
    assertTrue(getValue("Female", phes).asBooleanValue().getValue());

    assertEquals(new BigDecimal("25.95155709342561"), getValue("BMI", phes).getValueDecimal());
    assertFalse(getValue("BMI19_25", phes).asBooleanValue().getValue());
    assertTrue(getValue("BMI19_27", phes).asBooleanValue().getValue());
    assertTrue(getValue("BMI25_30", phes).asBooleanValue().getValue());
    assertFalse(getValue("BMI27_30", phes).asBooleanValue().getValue());

    assertEquals(BigDecimal.ONE, getValue("Finding", phes).getValueDecimal());
    assertTrue(getValue("Overweight", phes).asBooleanValue().getValue());
  }
}
