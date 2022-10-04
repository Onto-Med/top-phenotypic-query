package care.smith.top.top_phenotypic_query.tests.intern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.sql.SQLAdapter;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.tests.AbstractTest;

public class FullBMIAgeTestIntern extends AbstractTest {

  @Disabled
  @Test
  public void test() {
    QueryCriterion cri1 =
        new QueryCriterion()
            .exclusion(false)
            .defaultAggregationFunction(defAgrFunc)
            .subject(overWeight)
            .dateTimeRestriction(getDTR(2000));
    QueryCriterion cri2 = new QueryCriterion().exclusion(false).subject(female);
    QueryCriterion cri3 = new QueryCriterion().exclusion(false).subject(old);
    Query query = new Query().addCriteriaItem(cri1).addCriteriaItem(cri2).addCriteriaItem(cri3);

    DataAdapterConfig config =
        DataAdapterConfig.getInstance("test_files/SQL_Adapter_Test_intern.yml");
    SQLAdapter adapter = new SQLAdapter(config);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    //    System.out.println(rs);

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
