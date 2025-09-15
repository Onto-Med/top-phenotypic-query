package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import care.smith.top.model.Entity;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.encounter.EncAge;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.util.Entities.NoCodesException;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.net.URL;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class NoCodesTest {

  private static Phenotype crea =
      new Phe("crea")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
          .number("mmol/L")
          .get();

  private static Phenotype bili =
      new Phe("bili")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Bilirubin.total [Mass/volume] in Blood")
          .number("mg/dL")
          .get();

  private static Phenotype age1 = new Phe("age1").itemType(ItemType.SUBJECT_AGE).number("a").get();
  private static Phenotype young = new Phe("young").restriction(age1, Res.lt(18)).get();
  private static Phenotype old = new Phe("old").restriction(age1, Res.ge(18)).get();

  private static Phenotype sex = new Phe("sex").string().itemType(ItemType.SUBJECT_SEX).get();
  private static Phenotype bd =
      new Phe("birthdate").dateTime().itemType(ItemType.SUBJECT_BIRTH_DATE).get();
  private static Phenotype enc = new Phe("encounter").string().itemType(ItemType.ENCOUNTER).get();
  private static Phenotype age2 = new Phe("age2").expression(EncAge.of(bd, enc)).get();

  private static Entity[] entities = {crea, bili, age1, young, old, sex, bd, enc, age2};

  @Test
  @Disabled
  public void test() throws InstantiationException {
    URL configFile =
        Thread.currentThread()
            .getContextClassLoader()
            .getResource("config/Default_FHIR_Adapter_Test.yml");
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(null, entities, adapter);

    Exception exception =
        assertThrows(
            NoCodesException.class,
            () -> {
              pf.execute();
            });

    String msg = exception.getMessage();

    assertEquals(
        "The following single phenotypes have no codes: crea (Creatinine [Mass/volume] in Serum or Plasma), bili (Bilirubin.total [Mass/volume] in Blood)!",
        msg);
  }
}
