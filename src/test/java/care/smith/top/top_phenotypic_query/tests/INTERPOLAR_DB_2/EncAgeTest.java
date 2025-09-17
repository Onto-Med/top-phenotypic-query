package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.DateTimeValue;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.encounter.EncAge;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import org.junit.jupiter.api.Test;

public class EncAgeTest {

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

  private static final Phenotype bd =
      new Phe("birth date").itemType(ItemType.SUBJECT_BIRTH_DATE).dateTime().get();
  private static final Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).number().get();
  private static final Phenotype enc =
      new Phe("encounter").itemType(ItemType.ENCOUNTER).string().get();
  private static final Phenotype encAge =
      new Phe("encounter age").expression(EncAge.of(bd, enc)).get();

  @Test
  void test() throws InstantiationException {
    ResultSet rs =
        new Que(CONFIG, enc, bd, age, encAge)
            .pro(bd)
            .pro(age)
            .pro(encAge)
            .executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/encAge.sql")
            .execute();

    LocalDate birthdate11 = LocalDate.of(1940, Month.JANUARY, 1);
    LocalDate birthdate21 = LocalDate.of(1980, Month.JULY, 1);

    assertEquals(
        birthdate11,
        ((DateTimeValue) rs.getValues("HOSP-0001-E-11", "birth date", null).get(0))
            .getValue()
            .toLocalDate());
    assertEquals(
        birthdate21,
        ((DateTimeValue) rs.getValues("HOSP-0002-E-21", "birth date", null).get(0))
            .getValue()
            .toLocalDate());

    int age11 = Period.between(birthdate11, LocalDate.now()).getYears();
    int age21 = Period.between(birthdate21, LocalDate.now()).getYears();

    assertEquals(age11, rs.getNumberValue("HOSP-0001-E-11", "age", null).intValue());
    assertEquals(60, rs.getNumberValue("HOSP-0001-E-11", "encounter age", null).intValue());
    assertEquals(age11, rs.getNumberValue("HOSP-0001-E-12", "age", null).intValue());
    assertEquals(70, rs.getNumberValue("HOSP-0001-E-12", "encounter age", null).intValue());

    assertEquals(age21, rs.getNumberValue("HOSP-0002-E-21", "age", null).intValue());
    assertEquals(9, rs.getNumberValue("HOSP-0002-E-21", "encounter age", null).intValue());
    assertEquals(age21, rs.getNumberValue("HOSP-0002-E-22", "age", null).intValue());
    assertEquals(39, rs.getNumberValue("HOSP-0002-E-22", "encounter age", null).intValue());
  }
}
