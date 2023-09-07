package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.encounter.EncType;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Cond;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Enc;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class FHIRAnaphylaxisTestIntern {

  private static Client client = new Client();

  private static Phenotype ana =
      new Phe(
              "anaphylaxis",
              "http://fhir.de/CodeSystem/bfarm/icd-10-gm",
              "T78.0",
              "T78.2",
              "T80.5",
              "T88.6")
          .itemType(ItemType.CONDITION)
          .bool()
          .get();

  private static Phenotype enc = new Phe("encounter").itemType(ItemType.ENCOUNTER).string().get();

  private static Phenotype imp = new Phe("inpatient").restriction(enc, Res.of("IMP")).get();
  private static Phenotype amb = new Phe("ambulatory").restriction(enc, Res.of("AMB")).get();

  private static Phenotype algIMP = new Phe("algIMP").expression(EncType.of(ana, imp)).get();
  private static Phenotype algAMB = new Phe("algAMB").expression(EncType.of(ana, amb)).get();
  private static Phenotype algIMPAMB =
      new Phe("algIMPAMB").expression(EncType.of(ana, imp, amb)).get();

  @Test
  void test() throws InstantiationException {
    client.clean();

    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));
    Reference pat3 = client.add(new Pat("p3").birthDate("1951-01-01").gender("male"));
    Reference pat4 = client.add(new Pat("p4").birthDate("1951-01-01").gender("female"));
    Reference pat5 = client.add(new Pat("p5").birthDate("2001-01-01").gender("male"));
    Reference pat6 = client.add(new Pat("p6").birthDate("1951-01-01").gender("female"));

    Reference enc1 =
        client.add(
            new Enc("e1", pat1).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));
    Reference enc2 =
        client.add(
            new Enc("e2", pat2).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB"));
    Reference enc3 =
        client.add(
            new Enc("e3", pat3).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));
    Reference enc4 =
        client.add(
            new Enc("e4", pat4).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB"));
    Reference enc5 =
        client.add(
            new Enc("e5", pat5).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));
    client.add(new Enc("e6a", pat6).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));
    Reference enc6b =
        client.add(
            new Enc("e6b", pat6).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "XYZ"));

    client.add(
        new Cond("p1c", pat1)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .enc(enc1)
            .date("2020-01-01"));
    client.add(
        new Cond("p1cx", pat1)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .enc(enc1)
            .date("2020-01-02"));
    client.add(
        new Cond("p2c", pat2)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .enc(enc2)
            .date("2020-01-01"));
    client.add(
        new Cond("p3c", pat3)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .enc(enc3)
            .date("2020-01-01"));
    client.add(
        new Cond("p4c", pat4)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .enc(enc4)
            .date("2020-01-01"));
    client.add(
        new Cond("p5c", pat5)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T77.0")
            .enc(enc5)
            .date("2020-01-01"));
    client.add(
        new Cond("p6c", pat6)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .enc(enc6b)
            .date("2020-01-01"));

    ResultSet rs =
        new Que(
                "config/Default_FHIR_Adapter_Test.yml",
                ana,
                enc,
                imp,
                amb,
                algIMP,
                algAMB,
                algIMPAMB)
            .inc(algIMP)
            .execute();
    System.out.println(rs);
    assertEquals(Set.of(pat1.getReference(), pat3.getReference()), rs.getSubjectIds());

    rs =
        new Que(
                "config/Default_FHIR_Adapter_Test.yml",
                ana,
                enc,
                imp,
                amb,
                algIMP,
                algAMB,
                algIMPAMB)
            .inc(algAMB)
            .execute();
    System.out.println(rs);
    assertEquals(Set.of(pat2.getReference(), pat4.getReference()), rs.getSubjectIds());

    rs =
        new Que(
                "config/Default_FHIR_Adapter_Test.yml",
                ana,
                enc,
                imp,
                amb,
                algIMP,
                algAMB,
                algIMPAMB)
            .inc(algIMPAMB)
            .execute();
    System.out.println(rs);
    assertEquals(
        Set.of(pat1.getReference(), pat3.getReference(), pat2.getReference(), pat4.getReference()),
        rs.getSubjectIds());
  }
}
