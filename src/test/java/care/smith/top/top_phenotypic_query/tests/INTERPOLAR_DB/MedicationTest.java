package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class MedicationTest {

  private static final String CONFIG = "config/INTERPOLAR_DB_Adapter.yml";

  private static Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();
  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();
  private static Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).number().get();
  private static Phenotype old = new Phe("old").restriction(age, Res.ge(60)).get();
  private static Phenotype med =
      new Phe("med", "http://fhir.de/CodeSystem/bfarm/atc", "atc1")
          .itemType(ItemType.MEDICATION)
          .bool()
          .get();

  @Test
  void medication() throws InstantiationException {
    ResultSet rs =
        new Que(CONFIG, med, sex, male, age, old)
            .inc(med, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2021-01-01")))
            .inc(male)
            .inc(old)
            .cleanDB()
            .executeSqlFromResources("INTERPOLAR_DB/db.sql", "INTERPOLAR_DB/medication.sql")
            .execute();
    assertEquals(Set.of("13", "23", "33"), rs.getSubjectIds());
  }
}
