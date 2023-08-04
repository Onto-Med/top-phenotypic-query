package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Expression;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.Date;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class FilterTest {

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

  private static Phenotype check =
      new Phe("check")
          .expression(Exists.of(Filter.of(Exp.of(eGFR), Exp.ofConstant("lt"), Date.of(dabi))))
          .get();

  @Test
  public void test1() {
    SubjectPhenotypes vals = new SubjectPhenotypes("1");
    LocalDateTime now = LocalDateTime.now();

    vals.addValue("eGFR", null, Val.of(12, now.minusDays(24)));

    Entities phens = Entities.of(dabi, eGFR, check);

    C2R c = new C2R().phenotypes(phens).values(vals);
    Expression res = c.calculate(check);

    System.out.println(vals);

    assertFalse(Expressions.getBooleanValue(res));
  }

  @Test
  public void test2() {
    SubjectPhenotypes vals = new SubjectPhenotypes("1");
    LocalDateTime now = LocalDateTime.now();

    vals.addValue("Dabigatran", null, Val.of(true, now.minusDays(15)));
    vals.addValue("eGFR", null, Val.of(12, now.minusDays(24)));

    Entities phens = Entities.of(dabi, eGFR, check);

    C2R c = new C2R().phenotypes(phens).values(vals);
    Expression res = c.calculate(check);

    System.out.println(vals);

    assertTrue(Expressions.getBooleanValue(res));
  }

  @Test
  public void test3() {
    SubjectPhenotypes vals = new SubjectPhenotypes("1");
    LocalDateTime now = LocalDateTime.now();

    vals.addValue("Dabigatran", null, Val.of(true, now.minusDays(15)));
    vals.addValue("eGFR", null, Val.of(12, now.minusDays(15)));

    Entities phens = Entities.of(dabi, eGFR, check);

    C2R c = new C2R().phenotypes(phens).values(vals);
    Expression res = c.calculate(check);

    System.out.println(vals);

    assertFalse(Expressions.getBooleanValue(res));
  }
}
