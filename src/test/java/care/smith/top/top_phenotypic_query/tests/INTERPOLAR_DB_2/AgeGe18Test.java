package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import module java.base;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import org.junit.jupiter.api.Test;

public class AgeGe18Test {

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

  private static Phenotype enc = new Phe("enc").itemType(ItemType.ENCOUNTER).string().get();

  private static Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();

  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();

  private static Phenotype obs =
      new Phe("obs", "http://loinc.org", "123").itemType(ItemType.OBSERVATION).number().get();

  private static Phenotype pro =
      new Phe("pro", "pro", "123").itemType(ItemType.PROCEDURE).bool().get();

  private static Phenotype con =
      new Phe("con", "con", "123").itemType(ItemType.CONDITION).bool().get();

  private static Phenotype ma =
      new Phe("ma", "ma", "123").itemType(ItemType.MEDICATION_ADMINISTRATION).bool().get();

  private static Phenotype ms =
      new Phe("ms", "ms", "123").itemType(ItemType.MEDICATION_STATEMENT).bool().get();

  private static Phenotype mr =
      new Phe("mr", "mr", "123").itemType(ItemType.MEDICATION_REQUEST).bool().get();

  @Test
  void male() throws InstantiationException {
    assertEquals(Set.of("1"), search(male));
  }

  @Test
  void enc() throws InstantiationException {
    assertEquals(Set.of("1"), search(enc));
  }

  @Test
  void obs() throws InstantiationException {
    assertEquals(Set.of("1"), search(obs));
  }

  @Test
  void con() throws InstantiationException {
    assertEquals(Set.of("1"), search(con));
  }

  @Test
  void pro() throws InstantiationException {
    assertEquals(Set.of("1"), search(pro));
  }

  @Test
  void ma() throws InstantiationException {
    assertEquals(Set.of("1"), search(ma));
  }

  @Test
  void ms() throws InstantiationException {
    assertEquals(Set.of("1"), search(ms));
  }

  @Test
  void mr() throws InstantiationException {
    assertEquals(Set.of("1"), search(mr));
  }

  private Set<String> search(Phenotype inc) throws InstantiationException {
    Que q = new Que(CONFIG, enc, sex, male, obs, con, pro, ma, ms, mr).inc(inc);

    ResultSet rs =
        q.executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/age_ge18.sql")
            .execute();

    return rs.getSubjectIds();
  }
}
