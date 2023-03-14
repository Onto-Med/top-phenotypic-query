package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Entity;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Empty;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Filter;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Count;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.CutLast;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Median;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Min;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Not;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.PlusDays;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class AKITest extends DelirTest {

  private static final String CONFIG = "config/AKI_SQL_Adapter.yml";

  private static Phenotype crea =
      new Phe("crea", "http://loinc.org", "2160-0")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
          .number("mmol/L")
          .get();
  //  private static Phenotype creaRIf =
  //      new Phe("creaRIf")
  //          .titleEn("Creatinine reference interval female")
  //          .restriction(crea, Res.geLe(44, 97))
  //          .get();
  //  private static Phenotype creaRIm =
  //      new Phe("creaRIm")
  //          .titleEn("Creatinine reference interval male")
  //          .restriction(crea, Res.geLe(53, 106))
  //          .get();
  //  private static Phenotype creaLtRIf =
  //      new Phe("creaLtRIf")
  //          .titleEn("Creatinine under reference interval female")
  //          .restriction(crea, Res.lt(44))
  //          .get();
  //  private static Phenotype creaLtRIm =
  //      new Phe("creaLtRIm")
  //          .titleEn("Creatinine under reference interval male")
  //          .restriction(crea, Res.lt(53))
  //          .get();
  //  private static Phenotype creaGt354 =
  //      new Phe("creaGt354").titleEn("Creatinine > 354").restriction(crea, Res.gt(354)).get();

  private static Phenotype count =
      new Phe("count").titleEn("Creatinine values count").expression(Count.of(crea)).get();
  //  private static Phenotype countEq1 =
  //      new Phe("countEq1")
  //          .titleEn("Creatinine values count = 1")
  //          .restriction(count, Res.of(1))
  //          .get();
  private static Phenotype countGt1 =
      new Phe("countGt1")
          .titleEn("Creatinine values count > 1")
          .restriction(count, Res.gt(1))
          .get();
  //  private static Phenotype countGe1 =
  //      new Phe("countGe1")
  //          .titleEn("Creatinine values count >= 1")
  //          .restriction(count, Res.ge(1))
  //          .get();

  //  private static Phenotype age =
  //      new Phe("age", "http://loinc.org", "30525-0")
  //          .itemType(ItemType.OBSERVATION)
  //          .titleDe("Alter")
  //          .titleEn("Age")
  //          .number("a")
  //          .get();
  //  private static Phenotype under18 =
  //      new Phe("under18")
  //          .titleDe("Unter 18 Jahren")
  //          .titleEn("Under 18 years")
  //          .restriction(age, Res.lt(18))
  //          .get();
  //
  //  private static Phenotype sex = new Phe("sex", "http://loinc.org", "46098-0").string().get();
  //  private static Phenotype female = new Phe("female").restriction(sex, Res.of("female")).get();
  //  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();
  //
  //  private static Phenotype creaWithinRI =
  //      new Phe("creaWithinRI")
  //          .expression(Or.of(And.of(male, creaRIm), And.of(female, creaRIf)))
  //          .get();
  //  private static Phenotype creaUnderRI =
  //      new Phe("creaUnderRI")
  //          .expression(Or.of(And.of(male, creaLtRIm), And.of(female, creaLtRIf)))
  //          .get();

  private static Phenotype furtherCreaVals =
      new Phe("furtherCreaVals").expression(CutLast.of(crea)).get();

  private static Phenotype furtherCreaVals0_7 =
      new Phe("furtherCreaVals0_7")
          .expression(
              Filter.of(
                  Exp.of(furtherCreaVals),
                  Exp.ofConstant("ge"),
                  PlusDays.of(Exp.ofConstant("today"), Exp.of(-7)),
                  Exp.ofConstant("lt"),
                  Exp.ofConstant("now")))
          .get();
  private static Phenotype furtherCreaVals8_365 =
      new Phe("furtherCreaVals8_365")
          .expression(
              Filter.of(
                  Exp.of(furtherCreaVals),
                  Exp.ofConstant("ge"),
                  PlusDays.of(Exp.ofConstant("today"), Exp.of(-365)),
                  Exp.ofConstant("lt"),
                  PlusDays.of(Exp.ofConstant("today"), Exp.of(-7))))
          .get();
  //  private static Phenotype furtherCreaVals48 =
  //      new Phe("furtherCreaVals48")
  //          .expression(
  //              Filter.of(
  //                  Exp.of(furtherCreaVals),
  //                  Exp.ofConstant("ge"),
  //                  PlusDays.of(Exp.ofConstant("now"), Exp.of(-2)),
  //                  Exp.ofConstant("lt"),
  //                  Exp.ofConstant("now")))
  //          .get();

  private static Phenotype exist0_7 =
      new Phe("exist0_7").expression(Not.of(Empty.of(furtherCreaVals0_7))).get();

  //  private static Phenotype exist48 =
  //      new Phe("exist48").expression(Not.of(Empty.of(furtherCreaVals48))).get();

  private static Phenotype rv1 = new Phe("rv1").expression(Min.of(furtherCreaVals0_7)).get();
  private static Phenotype rv2 = new Phe("rv2").expression(Median.of(furtherCreaVals8_365)).get();

  private static Phenotype rv1Or2 = new Phe("rv1Or2").expression(If.of(exist0_7, rv1, rv2)).get();

  private static Phenotype rv = new Phe("rv").expression(Divide.of(crea, rv1Or2)).get();
  private static Phenotype rvGt1_5 = new Phe("rvGt1_5").restriction(rv, Res.gt(1.5)).get();

  //  private static Phenotype dif48 =
  //      new Phe("dif48").expression(Subtract.of(Exp.of(crea), Min.of(furtherCreaVals48))).get();
  //  private static Phenotype dif48exists = new Phe("dif48exists").restriction(dif48,
  // Res.gt(0)).get();
  //  private static Phenotype dif48gt26 = new Phe("dif48gt26").restriction(dif48,
  // Res.gt(26)).get();

  private static Entity[] entities = {
    crea,
    count,
    countGt1,
    furtherCreaVals,
    furtherCreaVals0_7,
    furtherCreaVals8_365,
    exist0_7,
    rv1,
    rv2,
    rv1Or2,
    rv,
    rvGt1_5
  };

  @Test
  public void test() throws InstantiationException {
    DateTimeRestriction dtr =
        Res.geLe(
            LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(365), LocalDateTime.now());
    ResultSet rs = new Que(CONFIG, entities).inc(countGt1, dtr).inc(rvGt1_5, dtr).execute();

    System.out.println(rs);

    assertEquals(Set.of("1", "2"), rs.getSubjectIds());

    assertEquals(BigDecimal.valueOf(90), rs.getNumberValue("1", "rv1Or2", dtr));
    assertEquals(BigDecimal.valueOf(2), rs.getNumberValue("1", "rv", dtr));
    assertTrue(rs.getBooleanValue("1", "rvGt1_5", dtr));

    assertEquals(BigDecimal.valueOf(60), rs.getNumberValue("2", "rv1Or2", dtr));
    assertEquals(BigDecimal.valueOf(3), rs.getNumberValue("2", "rv", dtr));
    assertTrue(rs.getBooleanValue("2", "rvGt1_5", dtr));
  }
}
