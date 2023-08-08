package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Expression;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.ForEach;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Min;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Gt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.RefValues;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class ForEachTest {

  private static Phenotype crea =
      new Phe("crea", "http://loinc.org", "2160-0")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
          .number("mmol/L")
          .get();

  private static Phenotype ref =
      new Phe("ref")
          .expression(Divide.of(Exp.of(crea), Min.of(RefValues.of(Exp.of(crea), Exp.of(7)))))
          .get();

  private static Phenotype check = new Phe("check").expression(Gt.of(Exp.of(ref), Exp.of(2))).get();

  private static Phenotype forEach = new Phe("forEach").expression(ForEach.of(crea, check)).get();

  @Test
  public void test() {
    SubjectPhenotypes vals = new SubjectPhenotypes("1");
    LocalDateTime now = LocalDateTime.now();

    vals.addValue("crea", null, Val.of(10, now.minusDays(15)));
    vals.addValue("crea", null, Val.of(12, now.minusDays(14)));
    vals.addValue("crea", null, Val.of(14, now.minusDays(13)));
    vals.addValue("crea", null, Val.of(16, now.minusDays(12)));
    vals.addValue("crea", null, Val.of(18, now.minusDays(11)));
    vals.addValue("crea", null, Val.of(20, now.minusDays(10)));
    vals.addValue("crea", null, Val.of(22, now.minusDays(9)));
    vals.addValue("crea", null, Val.of(24, now.minusDays(8)));
    vals.addValue("crea", null, Val.of(26, now.minusDays(7)));
    vals.addValue("crea", null, Val.of(28, now.minusDays(6)));
    vals.addValue("crea", null, Val.of(30, now.minusDays(5)));
    vals.addValue("crea", null, Val.of(32, now.minusDays(4)));
    vals.addValue("crea", null, Val.of(34, now.minusDays(3)));
    vals.addValue("crea", null, Val.of(36, now.minusDays(2)));
    vals.addValue("crea", null, Val.of(38, now.minusDays(1)));

    Entities phens = Entities.of(crea, ref, check, forEach);

    C2R c = new C2R().phenotypes(phens).values(vals);
    Expression res = c.calculate(forEach);

    assertEquals(
        List.of(
            false, false, false, false, false, true, true, true, false, false, false, false, false,
            false),
        Expressions.getBooleanValues(res));
  }
}
