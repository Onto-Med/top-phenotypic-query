package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import care.smith.top.model.DataType;
import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.util.Entities.NoCodesException;
import care.smith.top.top_phenotypic_query.util.Values;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class SubjectQueryTest extends AbstractTest {

  static Phenotype age = getPhenotype("Age", "http://loinc.org", "30525-0");
  static Phenotype young = getInterval("Young", age, 18, 34);
  static Phenotype sex = getPhenotype("Sex", "http://loinc.org", "46098-0", DataType.STRING);
  static Phenotype female =
      getRestriction("Female", sex, "http://hl7.org/fhir/administrative-gender|female");
  static Entity[] phenotypes = {age, young, sex, female};

  static String DATA_SOURCE = "data_source_1";

  @Test
  public void test1() throws InstantiationException, SQLException, NoCodesException {
    PhenotypeQuery query =
        ((PhenotypeQuery) new PhenotypeQuery().dataSource(DATA_SOURCE))
            .addCriteriaItem(
                (QueryCriterion) new QueryCriterion().inclusion(true).subjectId(female.getId()))
            .addCriteriaItem(
                (QueryCriterion) new QueryCriterion().inclusion(true).subjectId(young.getId()));
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
    assertEquals(BigDecimal.valueOf(33), Values.getNumberValue(phes.getValues("Age", null).get(0)));
    assertEquals(
        LocalDateTime.now().getYear() - 33,
        Values.getDateTimeValue(phes.getValues("birthdate", null).get(0)).getYear());
    assertEquals("female", Values.getStringValue(phes.getValues("Sex", null).get(0)));
    assertTrue(Values.getBooleanValue(phes.getValues("Female", null).get(0)));
    assertTrue(Values.getBooleanValue(phes.getValues("Young", null).get(0)));
  }

  @Test
  public void test2() throws InstantiationException, SQLException, NoCodesException {
    PhenotypeQuery query =
        ((PhenotypeQuery) new PhenotypeQuery().dataSource(DATA_SOURCE))
            .addCriteriaItem(
                (QueryCriterion) new QueryCriterion().inclusion(true).subjectId(female.getId()))
            .addCriteriaItem(
                (QueryCriterion) new QueryCriterion().inclusion(false).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    assertEquals(Set.of("1"), rs.getSubjectIds());

    SubjectPhenotypes phes = rs.getPhenotypes("1");
    assertEquals(Set.of("Sex", "Female", "Young", "birthdate", "Age"), phes.getPhenotypeNames());
    assertEquals("female", Values.getStringValue(phes.getValues("Sex", null).get(0)));
    assertTrue(Values.getBooleanValue(phes.getValues("Female", null).get(0)));
  }

  @Test
  public void test3() throws InstantiationException, SQLException, NoCodesException {
    PhenotypeQuery query =
        ((PhenotypeQuery) new PhenotypeQuery().dataSource(DATA_SOURCE))
            .addCriteriaItem(
                (QueryCriterion) new QueryCriterion().inclusion(false).subjectId(female.getId()))
            .addCriteriaItem(
                (QueryCriterion) new QueryCriterion().inclusion(true).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    assertEquals(Set.of("4"), rs.getSubjectIds());

    SubjectPhenotypes phes = rs.getPhenotypes("4");
    assertEquals(Set.of("Sex", "Female", "Young", "birthdate", "Age"), phes.getPhenotypeNames());
    assertEquals(BigDecimal.valueOf(32), Values.getNumberValue(phes.getValues("Age", null).get(0)));
    assertEquals(
        LocalDateTime.now().getYear() - 32,
        Values.getDateTimeValue(phes.getValues("birthdate", null).get(0)).getYear());
    assertTrue(Values.getBooleanValue(phes.getValues("Young", null).get(0)));
  }

  @Test
  public void test4() throws InstantiationException, SQLException, NoCodesException {
    PhenotypeQuery query =
        ((PhenotypeQuery) new PhenotypeQuery().dataSource(DATA_SOURCE))
            .addCriteriaItem(
                (QueryCriterion) new QueryCriterion().inclusion(false).subjectId(female.getId()))
            .addCriteriaItem(
                (QueryCriterion) new QueryCriterion().inclusion(false).subjectId(young.getId()));
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
