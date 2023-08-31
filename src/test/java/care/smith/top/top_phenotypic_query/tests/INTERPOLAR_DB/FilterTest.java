package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.Date;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Vals;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class FilterTest {

  private static final String CONFIG = "config/INTERPOLAR_DB_Adapter.yml";

  private static Phenotype dabi =
      new Phe("Dabigatran", "http://fhir.de/CodeSystem/bfarm/atc", "B01AE07")
          .itemType(ItemType.MEDICATION)
          .bool()
          .get();

  private static Phenotype eGFR =
      new Phe("eGFR", "http://loinc.org", "2160-0")
          .itemType(ItemType.OBSERVATION)
          .number("mL/min")
          .get();

  private static Phenotype eGFRgt30 = new Phe("eGFRgt30").restriction(eGFR, Res.gt(30)).get();

  private static Phenotype check1 =
      new Phe("check1")
          .expression(Exists.of(Filter.of(Exp.of(eGFR), Exp.ofConstant("lt"), Date.of(dabi))))
          .get();

  private static Phenotype check2 =
      new Phe("check2")
          .expression(Exists.of(Filter.of(Vals.of(eGFRgt30), Exp.ofConstant("lt"), Date.of(dabi))))
          .get();

  @Test
  void test() throws InstantiationException {

    ResultSet rs =
        new Que(CONFIG, dabi, eGFR, eGFRgt30, check1)
            .inc(check1)
            .cleanDB()
            .executeSqlFromResources("INTERPOLAR_DB/db.sql", "INTERPOLAR_DB/filter.sql")
            .execute();
    assertEquals(Set.of("2", "5"), rs.getSubjectIds());

    rs =
        new Que(CONFIG, dabi, eGFR, eGFRgt30, check2)
            .inc(check2)
            .cleanDB()
            .executeSqlFromResources("INTERPOLAR_DB/db.sql", "INTERPOLAR_DB/filter.sql")
            .execute();
    assertEquals(Set.of("2"), rs.getSubjectIds());
  }
}
