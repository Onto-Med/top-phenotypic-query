package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.Overlap2;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Med;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.MedReq;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import java.util.Set;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Disabled
public class DrugDrugTestIntern {

  private static final Logger LOGGER = LoggerFactory.getLogger(DrugDrugTestIntern.class);

  private static Client client = new Client();

  private static Phenotype drug1 =
      new Phe("drug1", "http://fhir.de/CodeSystem/bfarm/atc", "X1", "X2", "X3")
          .itemType(ItemType.MEDICATION_REQUEST)
          .bool()
          .get();

  private static Phenotype drug2 =
      new Phe("drug2", "http://fhir.de/CodeSystem/bfarm/atc", "Y1", "Y2", "Y3")
          .itemType(ItemType.MEDICATION_REQUEST)
          .bool()
          .get();

  private static Phenotype overlap = new Phe("overlap").expression(Overlap2.of(drug1, drug2)).get();

  @Test
  void test() throws InstantiationException {
    client.clean();

    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));
    Reference pat3 = client.add(new Pat("p3").birthDate("1951-01-01").gender("male"));
    Reference pat4 = client.add(new Pat("p4").birthDate("1951-01-01").gender("male"));
    Reference pat5 = client.add(new Pat("p5").birthDate("1951-01-01").gender("male"));
    Reference pat6 = client.add(new Pat("p6").birthDate("1951-01-01").gender("male"));

    Reference d1 = client.add(new Med("d1", "X1"));
    Reference d2 = client.add(new Med("d2", "Y3"));

    client.add(new MedReq("p1d1a", pat1, d1).dosageInstructionDate("2020-01-03", "2020-01-04"));
    client.add(new MedReq("p1d1b", pat1, d1).dosageInstructionDate("2020-01-07", "2020-01-08"));
    client.add(new MedReq("p1d2a", pat1, d2).dosageInstructionDate("2020-01-05", "2020-01-06"));
    client.add(new MedReq("p1d2b", pat1, d2).dosageInstructionDate("2020-01-09", "2020-01-10"));

    client.add(new MedReq("p2d1a", pat2, d1).dosageInstructionDate("2020-01-03", "2020-01-04"));
    client.add(new MedReq("p2d1b", pat2, d1).dosageInstructionDate("2020-01-07", "2020-01-08"));
    client.add(new MedReq("p2d2a", pat2, d2).dosageInstructionDate("2020-01-04", "2020-01-06"));
    client.add(new MedReq("p2d2b", pat2, d2).dosageInstructionDate("2020-01-09", "2020-01-10"));

    client.add(new MedReq("p3d1a", pat3, d1).dosageInstructionDate("2020-01-03", "2020-01-04"));
    client.add(new MedReq("p3d1b", pat3, d1).dosageInstructionDate("2020-01-07", "2020-01-08"));
    client.add(new MedReq("p3d2a", pat3, d2).dosageInstructionDate("2020-01-05", "2020-01-06"));
    client.add(new MedReq("p3d2b", pat3, d2).dosageInstructionDate("2020-01-01", "2020-01-03"));

    client.add(new MedReq("p4d1a", pat4, d1).dosageInstructionDate("2020-01-03", "2020-01-04"));
    client.add(new MedReq("p4d1b", pat4, d1).dosageInstructionDate("2020-01-04", "2020-01-05"));

    client.add(new MedReq("p5d1a", pat5, d1).dosageInstructionDate("2020-01-03", "2020-01-04"));
    client.add(new MedReq("p5d1b", pat5, d1).dosageInstructionStartDate("2020-01-07"));
    client.add(new MedReq("p5d2a", pat5, d2).dosageInstructionDate("2020-01-05", "2020-01-06"));
    client.add(new MedReq("p5d2b", pat5, d2).dosageInstructionDate("2020-01-09", "2020-01-10"));

    client.add(new MedReq("p6d1a", pat6, d1).dosageInstructionDate("2020-01-03", "2020-01-04"));
    client.add(new MedReq("p6d2a", pat6, d1).dosageInstructionDate("2020-01-06", "2020-01-07"));
    client.add(new MedReq("p6d1b", pat6, d2).dosageInstructionStartDate("2020-01-05"));
    client.add(new MedReq("p6d2b", pat6, d2).dosageInstructionDate("2020-01-09", "2020-01-10"));

    ResultSet rs =
        new Que("config/Default_FHIR_Adapter_Test.yml", drug1, drug2, overlap)
            .inc(overlap)
            .execute();

    LOGGER.trace(rs.toString());

    assertEquals(
        Set.of(pat2.getReference(), pat3.getReference(), pat5.getReference(), pat6.getReference()),
        rs.getSubjectIds());
  }
}
