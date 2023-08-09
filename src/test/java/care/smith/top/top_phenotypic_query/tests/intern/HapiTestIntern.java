package care.smith.top.top_phenotypic_query.tests.intern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Entity;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Power;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.Date;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
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
  private static Phenotype light = new Phe("light").restriction(weight, Res.lt(100)).get();

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

  private static Phenotype enc = new Phe("enc").itemType(ItemType.ENCOUNTER).string().get();

  private static Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).number().get();

  private static Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();
  private static Phenotype female = new Phe("female").restriction(sex, Res.of("female")).get();
  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();

  private static Phenotype med =
      new Phe("med", "http://hl7.org/fhir/sid/ndc", "50580-506-02")
          .itemType(ItemType.MEDICATION)
          .bool()
          .get();

  private static Phenotype check1 =
      new Phe("check1")
          .expression(Exists.of(Filter.of(Exp.of(weight), Exp.ofConstant("lt"), Date.of(med))))
          .get();

  private static Phenotype check2 =
      new Phe("check2")
          .expression(
              Ge.of(Filter.of(Exp.of(weight), Exp.ofConstant("lt"), Date.of(med)), Exp.of(60)))
          .get();

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
  public void testEnc() throws InstantiationException {
    ResultSet rs = new Que(CONFIG, enc).pro(enc).execute();
    assertEquals(869, rs.size());
  }

  @Test
  public void testAge() throws InstantiationException {
    ResultSet rs = new Que(CONFIG, age).pro(age).execute();
    assertEquals(5700, rs.size());
  }

  @Test
  public void testFemale() throws InstantiationException {
    ResultSet rs = new Que(CONFIG, sex, female).inc(female).execute();
    assertEquals(10000, rs.size());
  }

  @Test
  public void testMale() throws InstantiationException {
    ResultSet rs = new Que(CONFIG, sex, male).inc(male).execute();
    assertEquals(10000, rs.size());
  }

  @Test
  public void testMed() throws InstantiationException {
    ResultSet rs = new Que(CONFIG, med).inc(med).execute();
    assertEquals(100, rs.size());
  }

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
