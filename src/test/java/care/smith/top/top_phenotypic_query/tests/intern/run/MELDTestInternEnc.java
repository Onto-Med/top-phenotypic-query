package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Enc;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Med;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedAdm;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Obs;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Proc;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.time.LocalDateTime;
import java.util.Set;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class MELDTestInternEnc {

  private static Client client = new Client();

  private static Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();
  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();

  private static Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).number().get();
  private static Phenotype old = new Phe("old").restriction(age, Res.gt(70)).get();

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
    client.clean();

    Reference patMY = client.add(new Pat("pMY").birthDate("2001-01-01").gender("male"));
    Reference patMO = client.add(new Pat("pMO").birthDate("1950-01-01").gender("male"));
    Reference patFO = client.add(new Pat("pFO").birthDate("1940-01-01").gender("female"));

    Reference e0a = client.add(new Enc("e0a", patMY));
    Reference e0b = client.add(new Enc("e0b", patMY).partOf(e0a));
    Reference e0c = client.add(new Enc("e0c", patMY).partOf(e0b));
    Reference e0d = client.add(new Enc("e0d", patMY).partOf(e0b));

    Reference e1a = client.add(new Enc("e1a", patFO));
    Reference e1b = client.add(new Enc("e1b", patFO).partOf(e1a));
    Reference e1c = client.add(new Enc("e1c", patFO).partOf(e1b));

    Reference e2a = client.add(new Enc("e2a", patMO));
    Reference e2b = client.add(new Enc("e2b", patMO).partOf(e2a));
    Reference e2c = client.add(new Enc("e2c", patMO).partOf(e2b));

    Reference e3a = client.add(new Enc("e3a", patFO));
    Reference e3b = client.add(new Enc("e3b", patFO).partOf(e3a));
    Reference e3c = client.add(new Enc("e3c", patFO).partOf(e3a));
    Reference e3d = client.add(new Enc("e3d", patFO).partOf(e3b));

    Reference e4a = client.add(new Enc("e4a", patMY));
    Reference e4b = client.add(new Enc("e4b", patMY).partOf(e4a));
    Reference e4c = client.add(new Enc("e4c", patMY).partOf(e4b));
    Reference e4d = client.add(new Enc("e4d", patMY).partOf(e4c));
    Reference e4e = client.add(new Enc("e4e", patMY).partOf(e4d));

    Reference e5a = client.add(new Enc("e5a", patFO));
    Reference e5b = client.add(new Enc("e5b", patFO).partOf(e5a));

    Reference e6a = client.add(new Enc("e6a", patMY));
    Reference e6b = client.add(new Enc("e6b", patMY).partOf(e6a));

    Reference e7a = client.add(new Enc("e7a", patFO));
    Reference e7b = client.add(new Enc("e7b", patFO).partOf(e7a));

    Reference e8a = client.add(new Enc("e8a", patMO));
    Reference e8b = client.add(new Enc("e8b", patMO).partOf(e8a));
    Reference e8c = client.add(new Enc("e8c", patMO).partOf(e8a));

    client.add(
        new Obs("e0crea1")
            .enc(e0a)
            .code("http://loinc.org", "2160-0")
            .value(0.1, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e0crea2")
            .enc(e0b)
            .code("http://loinc.org", "2160-0")
            .value(2.1, "mg/dL")
            .date("2019-01-02"));
    client.add(
        new Obs("e0bili")
            .enc(e0c)
            .code("http://loinc.org", "42719-5")
            .value(0.2, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e0inr").enc(e0d).code("http://loinc.org", "6301-6").value(0.3).date("2020-01-01"));

    client.add(
        new Obs("e1crea")
            .enc(e1a)
            .code("http://loinc.org", "2160-0")
            .value(1.1, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e1bili")
            .enc(e1b)
            .code("http://loinc.org", "42719-5")
            .value(1.2, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e1inr").enc(e1c).code("http://loinc.org", "6301-6").value(1.3).date("2020-01-01"));

    client.add(
        new Obs("e2crea")
            .enc(e2a)
            .code("http://loinc.org", "2160-0")
            .value(2.1, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e2bili")
            .enc(e2b)
            .code("http://loinc.org", "42719-5")
            .value(2.2, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e2inr").enc(e2c).code("http://loinc.org", "6301-6").value(2.3).date("2020-01-01"));

    client.add(
        new Obs("e3crea")
            .enc(e3a)
            .code("http://loinc.org", "2160-0")
            .value(3.1, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e3bili")
            .enc(e3b)
            .code("http://loinc.org", "42719-5")
            .value(3.2, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e3inr").enc(e3c).code("http://loinc.org", "6301-6").value(3.3).date("2020-01-01"));
    Reference m = client.add(new Med("m", "B01AA"));
    client.add(new MedAdm("e3med", m).enc(e3d).date("2020-01-01"));

    client.add(
        new Obs("e4crea")
            .enc(e4a)
            .code("http://loinc.org", "2160-0")
            .value(0.1, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e4bili")
            .enc(e4b)
            .code("http://loinc.org", "42719-5")
            .value(0.2, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e4inr").enc(e4c).code("http://loinc.org", "6301-6").value(0.3).date("2020-01-01"));
    client.add(
        new Proc("e4dia1")
            .enc(e4d)
            .code("http://fhir.de/CodeSystem/bfarm/ops", "8-853")
            .date(LocalDateTime.now().minusDays(8)));
    client.add(
        new Proc("e4dia2")
            .enc(e4e)
            .code("http://fhir.de/CodeSystem/bfarm/ops", "8-853")
            .date(LocalDateTime.now().minusDays(6)));

    client.add(
        new Obs("e5crea")
            .enc(e5a)
            .code("http://loinc.org", "2160-0")
            .value(2.1, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e5bili")
            .enc(e5b)
            .code("http://loinc.org", "42719-5")
            .value(2.2, "mg/dL")
            .date("2020-01-01"));

    client.add(
        new Obs("e6crea")
            .enc(e6a)
            .code("http://loinc.org", "2160-0")
            .value(2.1, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e6inr").enc(e6b).code("http://loinc.org", "6301-6").value(2.3).date("2020-01-01"));

    client.add(
        new Obs("e7bili")
            .enc(e7a)
            .code("http://loinc.org", "42719-5")
            .value(2.2, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e7inr").enc(e7b).code("http://loinc.org", "6301-6").value(2.3).date("2020-01-01"));

    client.add(
        new Obs("e8bili")
            .enc(e8a)
            .code("http://loinc.org", "42719-5")
            .value(3.2, "mg/dL")
            .date("2020-01-01"));
    client.add(
        new Obs("e8inr").enc(e8b).code("http://loinc.org", "6301-6").value(3.3).date("2020-01-01"));
    client.add(
        new Proc("e8dia")
            .enc(e8c)
            .code("http://fhir.de/CodeSystem/bfarm/ops", "8-853")
            .date(LocalDateTime.now().minusDays(6)));

    assertEquals(Set.of(e0a.getReference()), search(null, meld0));
    assertEquals(Set.of(e1a.getReference()), search(null, meld2));
    assertEquals(
        Set.of(e2a.getReference(), e3a.getReference(), e4a.getReference(), e8a.getReference()),
        search(null, meld3));
    assertEquals(Set.of(e2a.getReference(), e8a.getReference()), search(null, meld3, male, old));
    assertEquals(
        Set.of(e2a.getReference(), e4a.getReference(), e8a.getReference()), search(med, meld3));
  }

  private Set<String> search(Phenotype exc, Phenotype... inc) throws InstantiationException {
    Que q =
        new Que(
            "config/Encounter_FHIR_Adapter.yml",
            sex,
            male,
            age,
            old,
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
            meld3);

    if (exc != null) q.exc(exc);
    for (Phenotype i : inc) q.inc(i);

    ResultSet rs = q.execute();
    //    System.out.println(rs);

    return rs.getSubjectIds();
  }
}
