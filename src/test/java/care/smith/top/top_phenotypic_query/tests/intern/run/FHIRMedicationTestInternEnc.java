package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Enc;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Med;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedAdm;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedReq;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedSta;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

public class FHIRMedicationTestInternEnc {

  private static Client client = new Client();

  private static Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();
  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();
  private static Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).number().get();
  private static Phenotype old = new Phe("old").restriction(age, Res.ge(60)).get();
  private static Phenotype med =
      new Phe("med", "http://fhir.de/CodeSystem/bfarm/atc", "atc1")
          .itemType(ItemType.MEDICATION)
          .bool()
          .get();

  @Test
  void test() {
    client.clean();

    Reference patMY = client.add(new Pat("patMY").birthDate("2001-01-01").gender("male"));
    Reference patFY = client.add(new Pat("patFY").birthDate("2001-01-01").gender("female"));
    Reference patMO = client.add(new Pat("patMO").birthDate("1951-01-01").gender("male"));
    Reference patFO = client.add(new Pat("patFO").birthDate("1951-01-01").gender("female"));

    Reference pat1a = client.add(new Enc("p1a", patMY));
    Reference pat1b = client.add(new Enc("p1b", patFY));
    Reference pat1c = client.add(new Enc("p1c", patMO));
    Reference pat1d = client.add(new Enc("p1d", patFO));
    Reference pat1e = client.add(new Enc("p1e", patMO));

    Reference pat2a = client.add(new Enc("p2a", patMY));
    Reference pat2b = client.add(new Enc("p2b", patFY));
    Reference pat2c = client.add(new Enc("p2c", patMO));
    Reference pat2d = client.add(new Enc("p2d", patFO));
    Reference pat2e = client.add(new Enc("p2e", patMO));

    Reference pat3a = client.add(new Enc("p3a", patMY));
    Reference pat3b = client.add(new Enc("p3b", patFY));
    Reference pat3c = client.add(new Enc("p3c", patMO));
    Reference pat3d = client.add(new Enc("p3d", patFO));
    Reference pat3e = client.add(new Enc("p3e", patMO));

    Reference pat4 = client.add(new Enc("p4", patMO));

    Reference med1 = client.add(new Med("m1", "atc1"));
    Reference med2 = client.add(new Med("m2", "atc2"));

    client.add(new MedAdm("p1a-m", med1).enc(pat1a).date("2020-01-01"));
    client.add(new MedAdm("p1b-m", med1).enc(pat1b).date("2020-01-01"));
    client.add(new MedAdm("p1c-m1", med1).enc(pat1c).date("2020-01-01"));
    client.add(new MedSta("p1c-m2", med1).enc(pat1c).date("2020-01-02"));
    client.add(new MedAdm("p1d-m", med1).enc(pat1d).date("2020-01-01"));
    client.add(new MedAdm("p1e-m", med1).enc(pat1e).date("2019-01-01"));

    client.add(new MedReq("p2a-m", med1).enc(pat2a).date("2020-01-01"));
    client.add(new MedReq("p2b-m", med1).enc(pat2b).date("2020-01-01"));
    client.add(new MedReq("p2c-m", med1).enc(pat2c).date("2020-01-01"));
    client.add(new MedReq("p2d-m", med1).enc(pat2d).date("2020-01-01"));
    client.add(new MedReq("p2e-m", med1).enc(pat2e).date("2019-01-01"));

    client.add(new MedSta("p3a-m", med1).enc(pat3a).date("2020-01-01"));
    client.add(new MedSta("p3b-m", med1).enc(pat3b).date("2020-01-01"));
    client.add(new MedSta("p3c-m", med1).enc(pat3c).date("2020-01-01"));
    client.add(new MedSta("p3d-m", med1).enc(pat3d).date("2020-01-01"));
    client.add(new MedSta("p3e-m", med1).enc(pat3e).date("2019-01-01"));

    client.add(new MedAdm("p4-m", med2).enc(pat4).date("2020-01-04"));

    try {
      ResultSet rs =
          new Que("config/Encounter_FHIR_Adapter.yml", med, sex, male, age, old)
              .inc(med, Res.ge(DateUtil.parse("2020-01-01")))
              .inc(male)
              .inc(old)
              .execute();
      System.out.println(rs);
      assertEquals(
          Set.of(pat1c.getReference(), pat2c.getReference(), pat3c.getReference()),
          rs.getSubjectIds());
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
  }
}
