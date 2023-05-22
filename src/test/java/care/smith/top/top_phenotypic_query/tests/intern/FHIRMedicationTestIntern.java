package care.smith.top.top_phenotypic_query.tests.intern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Med;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedAdm;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedReq;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedSta;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;

public class FHIRMedicationTestIntern {

  private static Client client = new Client();

  private static Phenotype med =
      new Phe("med", "http://www.whocc.no/atc", "atc1").itemType(ItemType.MEDICATION).bool().get();

  @Test
  void test() {
    client.clean();

    Reference pat1 = client.add(new Pat("p1").birthDate("2000-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2000-01-02").gender("female"));
    Reference pat3 = client.add(new Pat("p3").birthDate("2000-01-02").gender("male"));
    Reference pat4 = client.add(new Pat("p4").birthDate("2000-01-03").gender("female"));

    Reference med1 = client.add(new Med("m1", "atc1"));
    Reference med2 = client.add(new Med("m2", "atc2"));
    //    Reference med3 = client.add(new Med("m3", "atc3"));
    //    Reference med4 = client.add(new Med("m4", "atc4"));
    //    Reference med5 = client.add(new Med("m5", "atc5"));
    //    Reference med6 = client.add(new Med("m6", "atc6"));
    //    Reference med7 = client.add(new Med("m7", "atc7"));
    //    Reference med8 = client.add(new Med("m8", "atc8"));

    client.add(new MedAdm("p1m1a", pat1, med1).date("2020-01-01"));
    client.add(new MedReq("p2m1r", pat2, med1).date("2020-01-02"));
    client.add(new MedSta("p3m1s", pat3, med1).date("2020-01-03"));
    client.add(new MedAdm("p4m2a", pat4, med2).date("2020-01-04"));

    try {
      ResultSet rs = new Que("config/Default_FHIR_Adapter_Test.yml", med).inc(med).execute();
      assertEquals(
          Set.of(pat1.getReference(), pat2.getReference(), pat3.getReference()),
          rs.getSubjectIds());
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
  }
}
