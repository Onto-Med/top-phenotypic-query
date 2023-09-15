package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.Date;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Vals;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Med;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedAdm;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Obs;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

public class FilterTestIntern {

  private static Client client = new Client();

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
    client.clean();

    client.add(new Pat("p0").birthDate("2001-01-01").gender("male"));
    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));
    Reference pat3 = client.add(new Pat("p3").birthDate("1951-01-01").gender("male"));
    Reference pat4 = client.add(new Pat("p4").birthDate("1951-01-01").gender("male"));
    Reference pat5 = client.add(new Pat("p5").birthDate("1951-01-01").gender("male"));

    Reference m = client.add(new Med("m", "B01AE07"));

    client.add(new MedAdm("p1med", pat1, m).date("2020-01-01", "2021-01-01"));
    client.add(
        new Obs("p1eGFR", pat1)
            .code("http://loinc.org", "2160-0")
            .value(10, "mL/min")
            .date("2020-01-01"));

    client.add(new MedAdm("p2med", pat2, m).date("2020-01-02", "2021-01-01"));
    client.add(
        new Obs("p2eGFR", pat2)
            .code("http://loinc.org", "2160-0")
            .value(31, "mL/min")
            .date("2020-01-01"));

    client.add(new MedAdm("p5med", pat5, m).date("2020-01-02", "2021-01-01"));
    client.add(
        new Obs("p5eGFR", pat5)
            .code("http://loinc.org", "2160-0")
            .value(10, "mL/min")
            .date("2020-01-01"));

    client.add(new MedAdm("p3med", pat3, m));
    client.add(
        new Obs("p3eGFR", pat3)
            .code("http://loinc.org", "2160-0")
            .value(10, "mL/min")
            .date("2020-01-01"));

    client.add(
        new Obs("p4eGFR", pat4)
            .code("http://loinc.org", "2160-0")
            .value(10, "mL/min")
            .date("2020-01-01"));

    ResultSet rs =
        new Que("config/Default_FHIR_Adapter_Test.yml", dabi, eGFR, eGFRgt30, check1)
            .inc(check1)
            .execute();
    assertEquals(Set.of(pat2.getReference(), pat5.getReference()), rs.getSubjectIds());

    rs =
        new Que("config/Default_FHIR_Adapter_Test.yml", dabi, eGFR, eGFRgt30, check2)
            .inc(check2)
            .execute();
    assertEquals(Set.of(pat2.getReference()), rs.getSubjectIds());

    System.out.println(
        new Que("config/Default_FHIR_Adapter_Test.yml", dabi, eGFR, eGFRgt30, check1, check2)
            .pro(dabi)
            .pro(eGFR)
            .pro(eGFRgt30)
            .pro(check1)
            .pro(check2)
            .execute());
  }
}
