package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.sql.SQLException;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class BlazeTestIntern {

  private static final String CONFIG = "config/Blaze_Adapter_Test.yml";

  private static Phenotype hemoglobin =
      new Phe("hemoglobin", "http://loinc.org", "59260-0").number("g/dl").get();
  private static Phenotype hemoglobinOver14_5 =
      new Phe("hemoglobinOver14_5").restriction(hemoglobin, Res.gt(14.5)).get();
  private static Entity[] entities = {hemoglobin, hemoglobinOver14_5};

  @Test
  public void test1() throws InstantiationException, SQLException {
    ResultSet rs = new Que(CONFIG, entities).inc(hemoglobinOver14_5).execute();

    System.out.println(rs.getSubjectIds());

    assertEquals(
        Set.of(
            "Patient/UKB-0013",
            "Patient/UKB-0014",
            "Patient/UKE-0001",
            "Patient/UKFAU-0003",
            "Patient/UKFAU-0009",
            "Patient/UKFR-0003"),
        rs.getSubjectIds());

    int obsCount = 0;
    obsCount +=
        rs.getNumberValues("Patient/UKB-0013", "hemoglobin_values_hemoglobinOver14_5", null).size();
    obsCount +=
        rs.getNumberValues("Patient/UKB-0014", "hemoglobin_values_hemoglobinOver14_5", null).size();
    obsCount +=
        rs.getNumberValues("Patient/UKE-0001", "hemoglobin_values_hemoglobinOver14_5", null).size();
    obsCount +=
        rs.getNumberValues("Patient/UKFAU-0003", "hemoglobin_values_hemoglobinOver14_5", null)
            .size();
    obsCount +=
        rs.getNumberValues("Patient/UKFAU-0009", "hemoglobin_values_hemoglobinOver14_5", null)
            .size();
    obsCount +=
        rs.getNumberValues("Patient/UKFR-0003", "hemoglobin_values_hemoglobinOver14_5", null)
            .size();

    assertEquals(13, obsCount);
  }

  //  @Test
  //  public void test2() throws InstantiationException {
  //    Phenotype enc = new Phe("encounter").itemType(ItemType.ENCOUNTER).string().get();
  //    Phenotype stat = new Phe("stationaer").restriction(enc, Res.of("stationaer")).get();
  //    Entity[] entities = {enc, stat};
  //
  //    ResultSet rs = new Que(CONFIG, entities).inc(stat).execute();
  //
  //    System.out.println(rs.getSubjectIds());
  //    System.out.println(rs.getSubjectIds().size());
  //  }
  //
  //  @Test
  //  public void testEncPartOf() throws InstantiationException {
  //    Phenotype bd = new Phe("birthdate").dateTime().itemType(ItemType.SUBJECT_BIRTH_DATE).get();
  //    Phenotype enc = new Phe("encounter").string().itemType(ItemType.ENCOUNTER).get();
  //    Phenotype age = new Phe("age").expression(EncAge.of(bd, enc)).get();
  //    Phenotype old = new Phe("old").restriction(age, Res.ge(83)).get();
  //
  //    ResultSet rs =
  //        new Que("config/Blaze_Enc_Adapter_Test.yml", bd, enc, age, old).inc(old).execute();
  //
  //    System.out.println(rs);
  //  }
  //
  //  @Test
  //  public void testEncAge() throws InstantiationException {
  //    Phenotype bd = new Phe("birthdate").dateTime().itemType(ItemType.SUBJECT_BIRTH_DATE).get();
  //    Phenotype enc = new Phe("encounter").string().itemType(ItemType.ENCOUNTER).get();
  //    Phenotype age = new Phe("age").expression(EncAge.of(bd, enc)).get();
  //    Phenotype old = new Phe("old").restriction(age, Res.gt(82)).get();
  //
  //    ResultSet rs = new Que(CONFIG, bd, enc, age, old).inc(old).execute();
  //
  //    System.out.println(rs);
  //
  //    assertEquals(
  //        Set.of("Patient/UKSH-0004", "Patient/VHF09998", "Patient/VHF09999", "Patient/VHF10000"),
  //        rs.getSubjectIds());
  //  }
}
