package care.smith.top.top_phenotypic_query.tests.intern;

import java.time.LocalDateTime;

import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Max;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Ln;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Multiply;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Sum;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Gt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.PlusDays;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Obs;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Proc;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class MELDTestIntern {

  private static Client client = new Client();

  private static Phenotype crea =
      new Phe("crea", "http://loinc.org", "2160-0")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
          .number("mg/dL")
          .get();

  private static Phenotype bili =
      new Phe("bili", "http://loinc.org", "42719-5")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Bilirubin.total [Mass/volume] in Blood")
          .number("mg/dL")
          .get();

  private static Phenotype inr =
      new Phe("inr", "http://loinc.org", "6301-6", "34714-6", "38875-1")
          .itemType(ItemType.OBSERVATION)
          .titleEn("International Normalized Ratio")
          .number()
          .get();

  private static Phenotype dia =
      new Phe(
              "dia",
              "http://fhir.de/CodeSystem/bfarm/ops",
              "8-853",
              "8-854",
              "8-855",
              "8-857",
              "8-85a")
          .itemType(ItemType.PROCEDURE)
          .titleEn("Dialysis")
          .bool()
          .get();

  private static Phenotype med =
      new Phe("med", "http://fhir.de/CodeSystem/bfarm/atc", "B01AA", "B01AF")
          .itemType(ItemType.MEDICATION)
          .bool()
          .get();

  private static Phenotype creaClean1 =
      new Phe("creaClean1").expression(Max.of(Exp.of(crea), Exp.of(1))).get();

  private static Phenotype biliClean =
      new Phe("biliClean").expression(Max.of(Exp.of(bili), Exp.of(1))).get();

  private static Phenotype inrClean =
      new Phe("inrClean").expression(Max.of(Exp.of(inr), Exp.of(1))).get();

  private static Phenotype existDia0_7 =
      new Phe("existDia0_7")
          .expression(
              Exists.of(
                  Filter.of(
                      Exp.of(dia),
                      Exp.ofConstant("ge"),
                      PlusDays.of(Exp.ofConstant("now"), Exp.of(-7)),
                      Exp.ofConstant("le"),
                      Exp.ofConstant("now"))))
          .get();

  private static Phenotype creaClean =
      new Phe("creaClean")
          .expression(
              If.of(
                  Or.of(Gt.of(Exp.of(creaClean1), Exp.of(3)), Exp.of(existDia0_7)),
                  Exp.of(3),
                  Exp.of(creaClean1)))
          .get();

  private static Phenotype meld =
      new Phe("meld")
          .expression(
              Sum.of(
                  Multiply.of(Exp.of(9.57), Ln.of(creaClean)),
                  Multiply.of(Exp.of(3.78), Ln.of(biliClean)),
                  Multiply.of(Exp.of(11.2), Ln.of(inrClean)),
                  Exp.of(6.43)))
          .get();

  private static Phenotype meld1 = new Phe("meld1").restriction(meld, Res.geLt(7.5, 10)).get();
  private static Phenotype meld2 = new Phe("meld2").restriction(meld, Res.geLt(10, 15)).get();
  private static Phenotype meld3 = new Phe("meld3").restriction(meld, Res.ge(15)).get();

  @Test
  void test() {
    client.clean();

    Reference pat0 = client.add(new Pat("p0").birthDate("2001-01-01").gender("male"));
    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));
    Reference pat3 = client.add(new Pat("p3").birthDate("1951-01-01").gender("male"));
    Reference pat4 = client.add(new Pat("p4").birthDate("1951-01-01").gender("female"));
    Reference pat5 = client.add(new Pat("p5").birthDate("1951-01-01").gender("male"));
    Reference pat6 = client.add(new Pat("p6").birthDate("1951-01-01").gender("male"));
    Reference pat7 = client.add(new Pat("p7").birthDate("1951-01-01").gender("male"));

    client.add(
        new Obs("p0crea", pat0)
            .code("http://loinc.org", "2160-0")
            .value(0.1, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p0bili", pat0)
            .code("http://loinc.org", "42719-5")
            .value(0.2, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p0inr", pat0).code("http://loinc.org", "6301-6").value(0.3).date("2020-01-01"));

    client.add(
        new Obs("p1crea", pat1)
            .code("http://loinc.org", "2160-0")
            .value(1.1, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p1bili", pat1)
            .code("http://loinc.org", "42719-5")
            .value(1.2, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p1inr", pat1).code("http://loinc.org", "6301-6").value(1.3).date("2020-01-01"));

    client.add(
        new Obs("p2crea", pat2)
            .code("http://loinc.org", "2160-0")
            .value(2.1, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p2bili", pat2)
            .code("http://loinc.org", "42719-5")
            .value(2.2, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p2inr", pat2).code("http://loinc.org", "6301-6").value(2.3).date("2020-01-01"));

    client.add(
        new Obs("p3crea", pat3)
            .code("http://loinc.org", "2160-0")
            .value(3.1, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p3bili", pat3)
            .code("http://loinc.org", "42719-5")
            .value(3.2, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p3inr", pat3).code("http://loinc.org", "6301-6").value(3.3).date("2020-01-01"));

    client.add(
        new Obs("p4crea", pat4)
            .code("http://loinc.org", "2160-0")
            .value(0.1, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p4bili", pat4)
            .code("http://loinc.org", "42719-5")
            .value(0.2, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p4inr", pat4).code("http://loinc.org", "6301-6").value(0.3).date("2020-01-01"));

    client.add(
        new Proc("p4dia", pat4)
            .code("http://fhir.de/CodeSystem/bfarm/ops", "8-853")
            .date(LocalDateTime.now().minusDays(5)));

    client.add(
        new Obs("p5crea", pat5)
            .code("http://loinc.org", "2160-0")
            .value(2.1, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p5bili", pat5)
            .code("http://loinc.org", "42719-5")
            .value(2.2, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p6crea", pat6)
            .code("http://loinc.org", "2160-0")
            .value(2.1, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p6inr", pat6).code("http://loinc.org", "6301-6").value(2.3).date("2020-01-01"));

    client.add(
        new Obs("p7bili", pat7)
            .code("http://loinc.org", "42719-5")
            .value(2.2, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("p7inr", pat7).code("http://loinc.org", "6301-6").value(2.3).date("2020-01-01"));

    print();

    //    try {
    //      ResultSet rs =
    //          new Que(
    //                  "config/Default_FHIR_Adapter_Test.yml",
    //                  crea,
    //                  bili,
    //                  inr,
    //                  creaClean,
    //                  biliClean,
    //                  inrClean,
    //                  dia,
    //                  existDia0_7,
    //                  med,
    //                  meld,
    //                  meld1,
    //                  meld2,
    //                  meld3)
    //              .inc(meld3)
    //              .exc(med)
    //              .execute();
    //      System.out.println(rs);
    //      assertEquals(
    //          Set.of(pat1.getReference(), pat2.getReference(), pat3.getReference()),
    //          rs.getSubjectIds());
    //    } catch (InstantiationException e) {
    //      e.printStackTrace();
    //    }
  }

  private void print() {
    try {
      ResultSet rs =
          new Que(
                  "config/Default_FHIR_Adapter_Test.yml",
                  crea,
                  bili,
                  inr,
                  creaClean1,
                  creaClean,
                  biliClean,
                  inrClean,
                  dia,
                  existDia0_7,
                  med,
                  meld,
                  meld1,
                  meld2,
                  meld3)
              .pro(meld)
              .execute();
      System.out.println(rs);
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
  }
}
