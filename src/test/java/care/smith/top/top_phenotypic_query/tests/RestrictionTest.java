package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;

import org.junit.jupiter.api.Test;

import care.smith.top.model.DataType;
import care.smith.top.model.EntityType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.StringRestriction;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.fhir.FHIRAdapter;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.util.Entities;

public class RestrictionTest extends AbstractTest {

  @Test
  public void test() {
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/FHIR_Adapter_Test.yml");
    DataAdapter adapter = new FHIRAdapter(DataAdapterConfig.getInstance(configFile.getPath()));

    Phenotype p = getPhenotype("P", "http://phe.org", "p").dataType(DataType.STRING);
    Phenotype p1 = newRestriction(p);
    Entities phenotypes = Entities.of(p, p1);

    PhenotypeFinder pf = new PhenotypeFinder(null, phenotypes, adapter);

    Phenotype actual = pf.getPhenotypes().getPhenotype("p1");

    Phenotype expected =
        newRestriction(p)
            .restriction(
                new StringRestriction()
                    .addValuesItem("http://phe.org|p1a")
                    .addValuesItem("http://phe.org|p1b")
                    .quantifier(Quantifier.MIN)
                    .cardinality(1)
                    .type(DataType.STRING));

    assertEquals(expected, actual);
  }

  private Phenotype newRestriction(Phenotype parent) {
    return (Phenotype)
        new Phenotype()
            .superPhenotype(parent)
            .entityType(EntityType.SINGLE_RESTRICTION)
            .id("p1")
            .addCodesItem(getCode("http://phe.org", "p1a"))
            .addCodesItem(getCode("http://phe.org", "p1b"));
  }
}
