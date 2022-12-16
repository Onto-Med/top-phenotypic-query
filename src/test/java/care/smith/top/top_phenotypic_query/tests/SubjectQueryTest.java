package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.DataType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Values;

public class SubjectQueryTest extends AbstractTest {

  static Phenotype age = getPhenotype("Age", "http://loinc.org", "30525-0");
  static Phenotype young = getInterval("Young", age, 18, 34);
  static Phenotype sex = getPhenotype("Sex", "http://loinc.org", "46098-0", DataType.STRING);
  static Phenotype female =
      getRestriction("Female", sex, "http://hl7.org/fhir/administrative-gender|female");
  static Entities phenotypes = Entities.of(age, young, sex, female);

  @Test
  public void test1() throws InstantiationException {
    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().inclusion(true).subjectId(female.getId()))
            .addCriteriaItem(new QueryCriterion().inclusion(true).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    assertEquals(Set.of("3"), rs.getSubjectIds());

    SubjectPhenotypes phes = rs.getPhenotypes("3");
    assertEquals(Set.of("Age", "Young", "Sex", "Female", "birthdate"), phes.getPhenotypeNames());
    assertEquals(BigDecimal.valueOf(32), Values.getNumberValue(phes.getValues("Age", null).get(0)));
    assertEquals(
        LocalDateTime.parse("1990-01-01T00:00:00"),
        Values.getDateTimeValue(phes.getValues("birthdate", null).get(0)));
    assertEquals("female", Values.getStringValue(phes.getValues("Sex", null).get(0)));
    assertTrue(Values.getBooleanValue(phes.getValues("Female", null).get(0)));
    assertTrue(Values.getBooleanValue(phes.getValues("Young", null).get(0)));
  }

  @Test
  public void test2() throws InstantiationException {
    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().inclusion(true).subjectId(female.getId()))
            .addCriteriaItem(new QueryCriterion().inclusion(false).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    assertEquals(Set.of("1"), rs.getSubjectIds());

    SubjectPhenotypes phes = rs.getPhenotypes("1");
    assertEquals(Set.of("Sex", "Female"), phes.getPhenotypeNames());
    assertEquals("female", Values.getStringValue(phes.getValues("Sex", null).get(0)));
    assertTrue(Values.getBooleanValue(phes.getValues("Female", null).get(0)));
  }

  @Test
  public void test3() throws InstantiationException {
    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().inclusion(false).subjectId(female.getId()))
            .addCriteriaItem(new QueryCriterion().inclusion(true).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    assertEquals(Set.of("4"), rs.getSubjectIds());

    SubjectPhenotypes phes = rs.getPhenotypes("4");
    assertEquals(Set.of("Age", "Young", "birthdate"), phes.getPhenotypeNames());
    assertEquals(BigDecimal.valueOf(31), Values.getNumberValue(phes.getValues("Age", null).get(0)));
    assertEquals(
        LocalDateTime.parse("1991-01-01T00:00:00"),
        Values.getDateTimeValue(phes.getValues("birthdate", null).get(0)));
    assertTrue(Values.getBooleanValue(phes.getValues("Young", null).get(0)));
  }

  @Test
  public void test4() throws InstantiationException {
    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().inclusion(false).subjectId(female.getId()))
            .addCriteriaItem(new QueryCriterion().inclusion(false).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    assertEquals(Set.of("2"), rs.getSubjectIds());
  }
}
