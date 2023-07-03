package care.smith.top.top_phenotypic_query.tests.intern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Entity;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Power;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class HapiTestIntern {

  private static final String CONFIG = "config/Hapi_Adapter_Test.yml";

  private static Phenotype weight =
      new Phe("weight", "http://loinc.org", "3141-9").number("kg").get();
  private static Phenotype heavy = new Phe("heavy").restriction(weight, Res.ge(100)).get();

  private static Phenotype height =
      new Phe("height", "http://loinc.org", "3137-7", "8302-2").number("cm").get();
  private static Phenotype high = new Phe("high").restriction(height, Res.ge(150)).get();

  private static Phenotype bmi =
      new Phe("bmi", "http://loinc.org", "39156-5")
          .expression(
              Divide.of(
                  Exp.of(weight), Power.of(Divide.of(Exp.of(height), Exp.of(100)), Exp.of(2))))
          .get();
  private static Phenotype underweight =
      new Phe("underweight").restriction(bmi, Res.lt(18.5)).get();
  private static Phenotype normalWeight =
      new Phe("normalWeight").restriction(bmi, Res.geLt(18.5, 25)).get();
  private static Phenotype preObesity =
      new Phe("preObesity").restriction(bmi, Res.geLt(25, 30)).get();
  private static Phenotype obesityClassI =
      new Phe("obesityClassI").restriction(bmi, Res.geLt(30, 35)).get();
  private static Phenotype obesityClassII =
      new Phe("obesityClassII").restriction(bmi, Res.geLt(35, 40)).get();
  private static Phenotype obesityClassIII =
      new Phe("obesityClassIII").restriction(bmi, Res.ge(40)).get();

  private static Entity[] entities = {
    weight,
    heavy,
    height,
    high,
    bmi,
    underweight,
    normalWeight,
    preObesity,
    obesityClassI,
    obesityClassII,
    obesityClassIII
  };

  @Test
  public void test() throws InstantiationException {
    ResultSet rs =
        new Que(CONFIG, entities)
            .inc(obesityClassI, Res.ge(DateUtil.parse("2010-01-01")))
            .execute();

    System.out.println(rs.getSubjectIds());

    assertEquals(
        Set.of(
            "Patient/7045382",
            "Patient/7045388",
            "Patient/7045432",
            "Patient/7046218",
            "Patient/7046869"),
        rs.getSubjectIds());
  }

  @Test
  public void test2() throws InstantiationException {
    Phenotype enc = new Phe("encounter").itemType(ItemType.ENCOUNTER).string().get();
    Phenotype amb = new Phe("AMB").restriction(enc, Res.of("AMB")).get();
    Entity[] entities = {enc, amb};

    ResultSet rs = new Que(CONFIG, entities).inc(amb).execute();

    System.out.println(rs.getSubjectIds());
    System.out.println(rs.getSubjectIds().size());
  }
}
