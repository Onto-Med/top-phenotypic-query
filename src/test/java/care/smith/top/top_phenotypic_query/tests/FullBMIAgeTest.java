package care.smith.top.top_phenotypic_query.tests;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import org.junit.jupiter.api.Test;

import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.util.Values;

import static org.junit.jupiter.api.Assertions.*;

public class FullBMIAgeTest extends AbstractTest {

  @Test
  public void test() throws InstantiationException {
    QueryCriterion cri1 =
        new QueryCriterion()
            .inclusion(true)
            .defaultAggregationFunctionId(defAgrFunc.getId())
            .subjectId(overWeight.getId())
            .dateTimeRestriction(getDTR(2000));
    QueryCriterion cri2 = new QueryCriterion().inclusion(true).subjectId(female.getId());
    Query query = new Query().addCriteriaItem(cri1).addCriteriaItem(cri2);
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test3.yml");
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    System.out.println(rs);

    assertEquals(Set.of("1"), rs.getSubjectIds());

    SubjectPhenotypes phes = rs.getPhenotypes("1");
    Set<String> phesExpected = new HashSet<>(phenotypes.keySet());
    phesExpected.add("birthdate");
    assertEquals(phesExpected, phes.getPhenotypeNames());

    assertEquals(new BigDecimal(20), Values.getNumberValue(getValue("Age", phes)));
    assertFalse(Values.getBooleanValue(getValue("Old", phes)));
    assertTrue(Values.getBooleanValue(getValue("Young", phes)));

    assertEquals("female", Values.getStringValue(getValue("Sex", phes)));
    assertTrue(Values.getBooleanValue(getValue("Female", phes)));

    assertEquals(new BigDecimal("25.95155709342561"), Values.getNumberValue(getValue("BMI", phes)));
    assertFalse(Values.getBooleanValue(getValue("BMI19_25", phes)));
    assertTrue(Values.getBooleanValue(getValue("BMI19_27", phes)));
    assertTrue(Values.getBooleanValue(getValue("BMI25_30", phes)));
    assertFalse(Values.getBooleanValue(getValue("BMI27_30", phes)));

    assertEquals(BigDecimal.ONE, Values.getNumberValue(getValue("Finding", phes)));
    assertTrue(Values.getBooleanValue(getValue("Overweight", phes)));
  }
}
