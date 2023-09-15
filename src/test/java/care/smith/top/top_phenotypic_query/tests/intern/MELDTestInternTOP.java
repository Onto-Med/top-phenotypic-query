package care.smith.top.top_phenotypic_query.tests.intern;

import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Switch;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Ln;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Multiply;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Sum;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Gt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Lt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class MELDTestInternTOP {

  private static String CONFIG = "config/TOP_FHIR_Adapter.yml";
  private static Client client = new Client(CONFIG);

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

  private static Phenotype biliClean =
      new Phe("biliClean")
          .expression(If.of(Lt.of(Exp.of(bili), Exp.of(1)), Exp.of(1), Exp.of(bili)))
          .get();

  private static Phenotype inrClean =
      new Phe("inrClean")
          .expression(If.of(Lt.of(Exp.of(inr), Exp.of(1)), Exp.of(1), Exp.of(inr)))
          .get();

  private static Phenotype existDia0_7 =
      new Phe("existDia0_7").expression(Exists.of(Filter.of(Exp.of(dia), Exp.of(7)))).get();

  private static Phenotype creaClean =
      new Phe("creaClean")
          .expression(
              Switch.of(
                  Gt.of(Exp.of(crea), Exp.of(3)),
                  Exp.of(3),
                  Exp.of(existDia0_7),
                  Exp.of(3),
                  Lt.of(Exp.of(crea), Exp.of(1)),
                  Exp.of(1),
                  Exp.of(crea)))
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

  private static Phenotype meld0 = new Phe("meld0").restriction(meld, Res.lt(7.5)).get();
  private static Phenotype meld1 = new Phe("meld1").restriction(meld, Res.geLt(7.5, 10)).get();
  private static Phenotype meld2 = new Phe("meld2").restriction(meld, Res.geLt(10, 15)).get();
  private static Phenotype meld3 = new Phe("meld3").restriction(meld, Res.ge(15)).get();

  @Test
  void test() throws InstantiationException {
    //    client.clean();

    //    Reference pat0 = client.add(new Pat("p0").birthDate("2001-01-01").gender("male"));
    //    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    //    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));
    //    Reference pat3 = client.add(new Pat("p3").birthDate("1951-01-01").gender("male"));
    //    Reference pat4 = client.add(new Pat("p4").birthDate("1951-01-01").gender("female"));
    //    Reference pat5 = client.add(new Pat("p5").birthDate("1951-01-01").gender("male"));
    //    Reference pat6 = client.add(new Pat("p6").birthDate("1951-01-01").gender("male"));
    //    Reference pat7 = client.add(new Pat("p7").birthDate("1951-01-01").gender("male"));
    //    Reference pat8 = client.add(new Pat("p8").birthDate("1951-01-01").gender("male"));
    //
    //    client.add(
    //        new Obs("p0crea1", pat0)
    //            .code("http://loinc.org", "2160-0")
    //            .value(0.1, "mg/dL")
    //            .date("2020-01-01"));
    //    client.add(
    //        new Obs("p0crea2", pat0)
    //            .code("http://loinc.org", "2160-0")
    //            .value(2.1, "mg/dL")
    //            .date("2019-01-02"));
    //
    //    client.add(
    //        new Obs("p0bili", pat0)
    //            .code("http://loinc.org", "42719-5")
    //            .value(0.2, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p0inr", pat0).code("http://loinc.org",
    // "6301-6").value(0.3).date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p1crea", pat1)
    //            .code("http://loinc.org", "2160-0")
    //            .value(1.1, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p1bili", pat1)
    //            .code("http://loinc.org", "42719-5")
    //            .value(1.2, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p1inr", pat1).code("http://loinc.org",
    // "6301-6").value(1.3).date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p2crea", pat2)
    //            .code("http://loinc.org", "2160-0")
    //            .value(2.1, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p2bili", pat2)
    //            .code("http://loinc.org", "42719-5")
    //            .value(2.2, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p2inr", pat2).code("http://loinc.org",
    // "6301-6").value(2.3).date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p3crea", pat3)
    //            .code("http://loinc.org", "2160-0")
    //            .value(3.1, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p3bili", pat3)
    //            .code("http://loinc.org", "42719-5")
    //            .value(3.2, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p3inr", pat3).code("http://loinc.org",
    // "6301-6").value(3.3).date("2020-01-01"));
    //
    //    Reference m = client.add(new Med("m", "B01AA"));
    //    client.add(new MedAdm("p3med", pat3, m).date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p4crea", pat4)
    //            .code("http://loinc.org", "2160-0")
    //            .value(0.1, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p4bili", pat4)
    //            .code("http://loinc.org", "42719-5")
    //            .value(0.2, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p4inr", pat4).code("http://loinc.org",
    // "6301-6").value(0.3).date("2020-01-01"));
    //
    //    client.add(
    //        new Proc("p4dia1", pat4)
    //            .code("http://fhir.de/CodeSystem/bfarm/ops", "8-853")
    //            .date(LocalDateTime.now().minusDays(8)));
    //    client.add(
    //        new Proc("p4dia2", pat4)
    //            .code("http://fhir.de/CodeSystem/bfarm/ops", "8-853")
    //            .date(LocalDateTime.now().minusDays(2)));
    //
    //    client.add(
    //        new Obs("p5crea", pat5)
    //            .code("http://loinc.org", "2160-0")
    //            .value(2.1, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p5bili", pat5)
    //            .code("http://loinc.org", "42719-5")
    //            .value(2.2, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p6crea", pat6)
    //            .code("http://loinc.org", "2160-0")
    //            .value(2.1, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p6inr", pat6).code("http://loinc.org",
    // "6301-6").value(2.3).date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p7bili", pat7)
    //            .code("http://loinc.org", "42719-5")
    //            .value(2.2, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p7inr", pat7).code("http://loinc.org",
    // "6301-6").value(2.3).date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p8bili", pat8)
    //            .code("http://loinc.org", "42719-5")
    //            .value(3.2, "mg/dL")
    //            .date("2020-01-01"));
    //
    //    client.add(
    //        new Obs("p8inr", pat8).code("http://loinc.org",
    // "6301-6").value(3.3).date("2020-01-01"));
    //
    //    client.add(
    //        new Proc("p8dia", pat8)
    //            .code("http://fhir.de/CodeSystem/bfarm/ops", "8-853")
    //            .date(LocalDateTime.now().minusDays(2)));
    //
    //    assertEquals(Set.of(pat0.getReference()), search(meld0, null));
    //    assertEquals(Set.of(pat1.getReference()), search(meld2, null));
    //    assertEquals(
    //        Set.of(pat2.getReference(), pat3.getReference(), pat4.getReference(),
    // pat8.getReference()),
    //        search(meld3, null));
    //    assertEquals(
    //        Set.of(pat2.getReference(), pat4.getReference(), pat8.getReference()), search(meld3,
    // med));
  }

  private Set<String> search(Phenotype inc, Phenotype exc) throws InstantiationException {
    Que q =
        new Que(
                CONFIG,
                crea,
                bili,
                inr,
                creaClean,
                biliClean,
                inrClean,
                dia,
                existDia0_7,
                med,
                meld,
                meld0,
                meld1,
                meld2,
                meld3)
            .inc(inc);

    if (exc != null) q.exc(exc);

    ResultSet rs = q.execute();
    System.out.println(rs);

    return rs.getSubjectIds();
  }
}
