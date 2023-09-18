package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Cond;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Enc;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;

public class FHIRAnaphylaxisTestInternEnc {

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

  private static Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();
  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();

  private static Phenotype enc = new Phe("encounter").itemType(ItemType.ENCOUNTER).string().get();

  private static Phenotype imp = new Phe("inpatient").restriction(enc, Res.of("IMP")).get();
  private static Phenotype amb = new Phe("ambulatory").restriction(enc, Res.of("AMB")).get();

  private static Phenotype algIMPAMB = new Phe("algIMPAMB").expression(Or.of(imp, amb)).get();

  @Test
  void test() throws InstantiationException {
    client.clean();

    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));

    Reference enc1 =
        client.add(
            new Enc("e1", pat1)
                .period("2020-01-01", "2020-02-01")
                .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));
    Reference enc2 =
        client.add(
            new Enc("e2", pat1)
                .period("2020-01-01", "2020-02-01")
                .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB"));
    Reference enc3 =
        client.add(
            new Enc("e3", pat2)
                .period("2020-01-01", "2020-02-01")
                .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));
    Reference enc4 =
        client.add(
            new Enc("e4", pat2)
                .period("2020-01-01", "2020-02-01")
                .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB"));
    Reference enc5 =
        client.add(
            new Enc("e5", pat1)
                .period("2020-01-01", "2020-02-01")
                .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));
    Reference enc6 =
        client.add(
            new Enc("e6", pat1)
                .period("2020-01-01", "2020-02-01")
                .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "XYZ"));

    client.add(
        new Cond("p1c")
            .enc(enc1)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .date("2020-01-01"));
    client.add(
        new Cond("p2c")
            .enc(enc2)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .date("2020-01-01"));
    client.add(
        new Cond("p3c")
            .enc(enc3)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .date("2020-01-01"));
    client.add(
        new Cond("p4c")
            .enc(enc4)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .date("2020-01-01"));
    client.add(
        new Cond("p5c")
            .enc(enc5)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T77.0")
            .date("2020-01-01"));
    client.add(
        new Cond("p6c")
            .enc(enc6)
            .code("http://fhir.de/CodeSystem/bfarm/icd-10-gm", "T78.0")
            .date("2020-01-01"));

    ResultSet rs =
        new Que("config/Encounter_FHIR_Adapter.yml", ana, enc, imp, amb, algIMPAMB, sex, male)
            .inc(ana, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-02")))
            .inc(imp, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-02")))
            .inc(male)
            .execute();
    System.out.println(rs);
    assertEquals(Set.of(enc1.getReference()), rs.getSubjectIds());

    rs =
        new Que("config/Encounter_FHIR_Adapter.yml", ana, enc, imp, amb, algIMPAMB, sex, male)
            .inc(ana, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-02")))
            .inc(amb, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-02")))
            .inc(male)
            .execute();
    System.out.println(rs);
    assertEquals(Set.of(enc2.getReference()), rs.getSubjectIds());

    rs =
        new Que("config/Encounter_FHIR_Adapter.yml", ana, enc, imp, amb, algIMPAMB, sex, male)
            .inc(ana)
            .inc(amb, Res.lt(DateUtil.parse("2020-01-01")))
            .inc(male)
            .execute();
    assertTrue(rs.getSubjectIds().isEmpty());

    rs =
        new Que("config/Encounter_FHIR_Adapter.yml", ana, enc, imp, amb, algIMPAMB, sex, male)
            .inc(ana, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-02")))
            .inc(algIMPAMB, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-02")))
            .inc(male)
            .execute();
    System.out.println(rs);
    assertEquals(Set.of(enc1.getReference(), enc2.getReference()), rs.getSubjectIds());

    rs =
        new Que("config/Encounter_FHIR_Adapter.yml", ana, enc, imp, amb, algIMPAMB, sex, male)
            .inc(ana)
            .inc(algIMPAMB)
            .inc(male)
            .execute();
    assertEquals(Set.of(enc1.getReference(), enc2.getReference()), rs.getSubjectIds());

    rs =
        new Que("config/Encounter_FHIR_Adapter.yml", ana, enc, imp, amb, algIMPAMB, sex, male)
            .inc(ana, Res.geLe(DateUtil.parse("2020-01-02"), DateUtil.parse("2020-01-03")))
            .inc(algIMPAMB)
            .inc(male)
            .execute();
    assertTrue(rs.getSubjectIds().isEmpty());

    rs =
        new Que("config/Encounter_FHIR_Adapter.yml", ana, enc, imp, amb, algIMPAMB, sex, male)
            .inc(ana)
            .inc(algIMPAMB, Res.lt(DateUtil.parse("2020-01-01")))
            .inc(male)
            .execute();
    assertTrue(rs.getSubjectIds().isEmpty());
  }
}
