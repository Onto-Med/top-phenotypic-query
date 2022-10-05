package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.DataType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.sql.SQLAdapter;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;

public class SubjectQueryTest extends AbstractTest {

  static Phenotype age = getPhenotype("Age", "http://loinc.org", "30525-0");
  static Phenotype young = getInterval("Young", age, 18, 34);
  static Phenotype sex = getPhenotype("Sex", "http://loinc.org", "46098-0", DataType.STRING);
  static Phenotype female =
      getRestriction("Female", sex, "http://hl7.org/fhir/administrative-gender|female");
  static Map<String, Phenotype> phenotypes = getPhenotypeMap(age, young, sex, female);

  @Test
  public void test1() {
    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().inclusion(true).subjectId(female.getId()))
            .addCriteriaItem(new QueryCriterion().inclusion(true).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapterConfig config = DataAdapterConfig.getInstance(configFile.getPath());
    SQLAdapter adapter = new SQLAdapter(config);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    System.out.println(rs);
    assertEquals(Set.of("3"), rs.getSubjectIds());

    Phenotypes phes = rs.getPhenotypes("3");
    assertEquals(Set.of("Age", "Young", "Sex", "Female", "birthdate"), phes.getPhenotypeNames());
    assertEquals(
        BigDecimal.valueOf(32),
        phes.getValues("Age", null).getValues().get(0).asDecimalValue().getValue());
    assertEquals(
        LocalDateTime.parse("1990-01-01T00:00:00"),
        phes.getValues("birthdate", null).getValues().get(0).asDateTimeValue().getValue());
    assertEquals(
        "female", phes.getValues("Sex", null).getValues().get(0).asStringValue().getValue());
    assertTrue(phes.getValues("Female", null).getValues().get(0).asBooleanValue().getValue());
    assertTrue(phes.getValues("Young", null).getValues().get(0).asBooleanValue().getValue());
  }

  @Test
  public void test2() {
    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().inclusion(true).subjectId(female.getId()))
            .addCriteriaItem(new QueryCriterion().inclusion(false).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapterConfig config = DataAdapterConfig.getInstance(configFile.getPath());
    SQLAdapter adapter = new SQLAdapter(config);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    System.out.println(rs);
    assertEquals(Set.of("1"), rs.getSubjectIds());

    Phenotypes phes = rs.getPhenotypes("1");
    assertEquals(Set.of("Sex", "Female"), phes.getPhenotypeNames());
    assertEquals(
        "female", phes.getValues("Sex", null).getValues().get(0).asStringValue().getValue());
    assertTrue(phes.getValues("Female", null).getValues().get(0).asBooleanValue().getValue());
  }

  @Test
  public void test3() {
    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().inclusion(false).subjectId(female.getId()))
            .addCriteriaItem(new QueryCriterion().inclusion(true).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapterConfig config = DataAdapterConfig.getInstance(configFile.getPath());
    SQLAdapter adapter = new SQLAdapter(config);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    System.out.println(rs);
    assertEquals(Set.of("4"), rs.getSubjectIds());

    Phenotypes phes = rs.getPhenotypes("4");
    assertEquals(Set.of("Age", "Young", "birthdate"), phes.getPhenotypeNames());
    assertEquals(
        BigDecimal.valueOf(31),
        phes.getValues("Age", null).getValues().get(0).asDecimalValue().getValue());
    assertEquals(
        LocalDateTime.parse("1991-01-01T00:00:00"),
        phes.getValues("birthdate", null).getValues().get(0).asDateTimeValue().getValue());
    assertTrue(phes.getValues("Young", null).getValues().get(0).asBooleanValue().getValue());
  }

  @Test
  public void test4() {
    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().inclusion(false).subjectId(female.getId()))
            .addCriteriaItem(new QueryCriterion().inclusion(false).subjectId(young.getId()));
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test2.yml");
    assertNotNull(configFile);
    DataAdapterConfig config = DataAdapterConfig.getInstance(configFile.getPath());
    SQLAdapter adapter = new SQLAdapter(config);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    System.out.println(rs);
    assertEquals(Set.of("2"), rs.getSubjectIds());
  }
}
