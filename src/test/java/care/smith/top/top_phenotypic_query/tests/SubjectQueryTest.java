package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Quantifier;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.SQLAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;

public class SubjectQueryTest extends AbstractTest {

  @Test
  public void test() throws URISyntaxException {
    Phenotype age = getSinglePhenotype("Age", "http://loinc.org", "30525-0", DataType.NUMBER);
    Phenotype young =
        getRestriction("Young", age, 18, 34, EntityType.SINGLE_RESTRICTION, Quantifier.MIN, 1);

    Phenotype sex = getSinglePhenotype("Sex", "http://loinc.org", "46098-0", DataType.STRING);
    Phenotype female =
        getRestriction(
            "Female",
            sex,
            EntityType.SINGLE_RESTRICTION,
            Quantifier.MIN,
            1,
            "http://hl7.org/fhir/administrative-gender|female");

    Map<String, Phenotype> phenotypes = new HashMap<>();
    phenotypes.put(age.getId(), age);
    phenotypes.put(young.getId(), young);
    phenotypes.put(sex.getId(), sex);
    phenotypes.put(female.getId(), female);

    Query query =
        new Query()
            .addCriteriaItem(new QueryCriterion().exclusion(false).subject(female))
            .addCriteriaItem(new QueryCriterion().exclusion(false).subject(young));
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
}
