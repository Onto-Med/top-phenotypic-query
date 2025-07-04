package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.TimeDistance;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Vals;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Obs;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class FHIRHyperglycemiaTestIntern2 {

  private static Client client = new Client();

  private static Phenotype glu1 =
      new Phe("glucose1", "http://loinc.org", "15074-8", "72516-8")
          .itemType(ItemType.OBSERVATION)
          .number("mmol/L")
          .get();
  private static Phenotype glu1Range1 =
      new Phe("glucose1Range1").restriction(glu1, Res.gt(16.7)).get();
  private static Phenotype glu1Range2 =
      new Phe("glucose1Range2").restriction(glu1, Res.gt(13.9)).get();
  private static Phenotype glu1Range3 =
      new Phe("glucose1Range3").restriction(glu1, Res.gt(11.1)).get();

  private static Phenotype glu2 =
      new Phe("glucose2", "http://loinc.org", "2339-0", "2340-8")
          .itemType(ItemType.OBSERVATION)
          .number("mg/dL")
          .get();
  private static Phenotype glu2Range1 =
      new Phe("glucose2Range1").restriction(glu2, Res.gt(300)).get();
  private static Phenotype glu2Range2 =
      new Phe("glucose2Range2").restriction(glu2, Res.gt(250)).get();
  private static Phenotype glu2Range3 =
      new Phe("glucose2Range3").restriction(glu2, Res.gt(200)).get();

  private static Phenotype check1 =
      new Phe("check 1").expression(Exists.of(Vals.of(glu1Range1, glu2Range1))).get();

  private static Phenotype check2 =
      new Phe("check 2")
          .expression(
              TimeDistance.of(
                  Vals.of(glu1Range2, glu2Range2),
                  Exp.of(2),
                  Exp.ofConstant("gt"),
                  Exp.of(6),
                  Exp.ofConstant("le"),
                  Exp.of(72)))
          .get();

  private static Phenotype check3 =
      new Phe("check 3")
          .expression(
              TimeDistance.of(
                  Vals.of(glu1Range3, glu2Range3),
                  Exp.of(3),
                  Exp.ofConstant("gt"),
                  Exp.of(6),
                  Exp.ofConstant("le"),
                  Exp.of(72)))
          .get();

  private static Phenotype hyperGlu =
      new Phe("hyperglycemia")
          .expression(Or.of(Exp.of(check1), Exp.of(check2), Exp.of(check3)))
          .get();

  @Test
  void test() {
    client.clean();

    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));
    Reference pat3 = client.add(new Pat("p3").birthDate("1951-01-01").gender("male"));
    Reference pat4 = client.add(new Pat("p4").birthDate("1951-01-01").gender("female"));
    Reference pat6 = client.add(new Pat("p6").birthDate("1951-01-01").gender("male"));
    Reference pat7 = client.add(new Pat("p6").birthDate("1951-01-01").gender("male"));

    client.add(
        new Obs("p1a", pat1)
            .code("http://loinc.org", "15074-8")
            .value(16.8, "mmol/L")
            .date("2020-01-01"));

    client.add(
        new Obs("p2a", pat2)
            .code("http://loinc.org", "15074-8")
            .value(14.1, "mmol/L")
            .date("2020-01-01T12:00"));
    client.add(
        new Obs("p2b", pat2)
            .code("http://loinc.org", "2340-8")
            .value(251, "mg/dL")
            .date("2020-01-01T19:00"));

    client.add(
        new Obs("p3a", pat3)
            .code("http://loinc.org", "15074-8")
            .value(11.2, "mmol/L")
            .date("2020-01-01T12:00"));
    client.add(
        new Obs("p3b", pat3)
            .code("http://loinc.org", "15074-8")
            .value(11.3, "mmol/L")
            .date("2020-01-01T19:00"));
    client.add(
        new Obs("p3c", pat3)
            .code("http://loinc.org", "2340-8")
            .value(201, "mg/dL")
            .date("2020-01-04T19:00"));

    client.add(
        new Obs("p4a", pat4)
            .code("http://loinc.org", "15074-8")
            .value(16.7, "mmol/L")
            .date("2020-01-03T19:00"));

    client.add(
        new Obs("p6a", pat6)
            .code("http://loinc.org", "15074-8")
            .value(14.1, "mmol/L")
            .date("2020-01-01T12:00"));
    client.add(
        new Obs("p6b", pat6)
            .code("http://loinc.org", "2340-8")
            .value(251, "mg/dL")
            .date("2020-01-01T18:00"));

    client.add(
        new Obs("p7a", pat7)
            .code("http://loinc.org", "15074-8")
            .value(11.2, "mmol/L")
            .date("2020-01-01T12:00"));
    client.add(
        new Obs("p7b", pat7)
            .code("http://loinc.org", "15074-8")
            .value(11.3, "mmol/L")
            .date("2020-01-01T19:00"));
    client.add(
        new Obs("p7c", pat7)
            .code("http://loinc.org", "2340-8")
            .value(201, "mg/dL")
            .date("2020-01-04T19:01"));

    try {
      ResultSet rs =
          new Que(
                  "config/Default_FHIR_Adapter_Test.yml",
                  glu1,
                  glu2,
                  glu1Range1,
                  glu1Range2,
                  glu1Range3,
                  glu2Range1,
                  glu2Range2,
                  glu2Range3,
                  check1,
                  check2,
                  check3,
                  hyperGlu)
              .inc(hyperGlu)
              .execute();
      System.out.println(rs);
      assertEquals(
          Set.of(pat1.getReference(), pat2.getReference(), pat3.getReference()),
          rs.getSubjectIds());
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
  }
}
