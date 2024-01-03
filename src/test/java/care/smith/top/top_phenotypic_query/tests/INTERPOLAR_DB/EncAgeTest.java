package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.encounter.EncAge;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import org.junit.jupiter.api.Test;

public class EncAgeTest {

  private static final String CONFIG = "config/INTERPOLAR_DB_Adapter.yml";

  private static Phenotype bd =
      new Phe("birth date").itemType(ItemType.SUBJECT_BIRTH_DATE).dateTime().get();
  private static Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).number().get();
  private static Phenotype enc = new Phe("encounter").itemType(ItemType.ENCOUNTER).string().get();
  private static Phenotype encAge = new Phe("encounter age").expression(EncAge.of(bd, enc)).get();

  @Test
  void test() throws InstantiationException {
    ResultSet rs =
        new Que(CONFIG, enc, bd, age, encAge)
            .pro(bd)
            .pro(age)
            .pro(encAge)
            .cleanDB()
            .executeSqlFromResources("INTERPOLAR_DB/db.sql", "INTERPOLAR_DB/encAge.sql")
            .execute();

    System.out.println(rs);

    assertEquals(84, rs.getNumberValue("11", "age", null).intValue());
    assertEquals(60, rs.getNumberValue("11", "encounter age", null).intValue());
    assertEquals(84, rs.getNumberValue("12", "age", null).intValue());
    assertEquals(70, rs.getNumberValue("12", "encounter age", null).intValue());

    assertEquals(43, rs.getNumberValue("21", "age", null).intValue());
    assertEquals(9, rs.getNumberValue("21", "encounter age", null).intValue());
    assertEquals(43, rs.getNumberValue("22", "age", null).intValue());
    assertEquals(39, rs.getNumberValue("22", "encounter age", null).intValue());
  }
}
