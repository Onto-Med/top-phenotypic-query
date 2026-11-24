package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import module java.base;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import org.junit.jupiter.api.Test;

public class StatusTest {

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

  private static Phenotype enc = new Phe("enc").itemType(ItemType.ENCOUNTER).string().get();
  private static Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();
  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();

  // Observation & Age
  private static Phenotype obs =
      new Phe("obs", "http://loinc.org", "123").itemType(ItemType.OBSERVATION).number().get();
  private static Phenotype obsFinal = new Phe("obsFinal").restriction(obs, Res.of(31)).get();
  private static Phenotype obsPreliminary =
      new Phe("obsPreliminary").restriction(obs, Res.of(32)).get();
  private static Phenotype obsAmended = new Phe("obsAmended").restriction(obs, Res.of(33)).get();
  private static Phenotype obsCorrected =
      new Phe("obsCorrected").restriction(obs, Res.of(34)).get();
  private static Phenotype obsNull = new Phe("obsNull").restriction(obs, Res.of(35)).get();
  private static Phenotype obsWithoutIdFinal =
      new Phe("obsWithoutIdFinal").restriction(obs, Res.of(36)).get();
  private static Phenotype obsWithoutIdNull =
      new Phe("obsWithoutIdNull").restriction(obs, Res.of(37)).get();
  private static Phenotype obsExcluded = new Phe("obsExcluded").restriction(obs, Res.ge(90)).get();
  private static Phenotype age =
      new Phe("age", "http://loinc.org", "29553-5").itemType(ItemType.OBSERVATION).number().get();

  // Condition
  private static Phenotype conConfirmed =
      new Phe("conConfirmed", "con", "1").itemType(ItemType.CONDITION).bool().get();
  private static Phenotype conUnconfirmed =
      new Phe("conUnconfirmed", "con", "2").itemType(ItemType.CONDITION).bool().get();
  private static Phenotype conProvisional =
      new Phe("conProvisional", "con", "3").itemType(ItemType.CONDITION).bool().get();
  private static Phenotype conDifferential =
      new Phe("conDifferential", "con", "4").itemType(ItemType.CONDITION).bool().get();
  private static Phenotype conNull =
      new Phe("conNull", "con", "5").itemType(ItemType.CONDITION).bool().get();
  private static Phenotype conRefuted =
      new Phe("conRefuted", "con", "6").itemType(ItemType.CONDITION).bool().get();
  private static Phenotype conError =
      new Phe("conError", "con", "7").itemType(ItemType.CONDITION).bool().get();

  // Procedure
  private static Phenotype proCompleted =
      new Phe("proCompleted", "pro", "1").itemType(ItemType.PROCEDURE).bool().get();
  private static Phenotype proInProgress =
      new Phe("proInProgress", "pro", "2").itemType(ItemType.PROCEDURE).bool().get();
  private static Phenotype proNotDone =
      new Phe("proNotDone", "pro", "3").itemType(ItemType.PROCEDURE).bool().get();
  private static Phenotype proStopped =
      new Phe("proStopped", "pro", "4").itemType(ItemType.PROCEDURE).bool().get();
  private static Phenotype proNull =
      new Phe("proNull", "pro", "5").itemType(ItemType.PROCEDURE).bool().get();
  private static Phenotype proError =
      new Phe("proError", "pro", "6").itemType(ItemType.PROCEDURE).bool().get();

  // MedicationAdministration
  private static Phenotype maCompleted =
      new Phe("maCompleted", "ma", "1").itemType(ItemType.MEDICATION_ADMINISTRATION).bool().get();
  private static Phenotype maInProgress =
      new Phe("maInProgress", "ma", "2").itemType(ItemType.MEDICATION_ADMINISTRATION).bool().get();
  private static Phenotype maOnHold =
      new Phe("maOnHold", "ma", "3").itemType(ItemType.MEDICATION_ADMINISTRATION).bool().get();
  private static Phenotype maStopped =
      new Phe("maStopped", "ma", "4").itemType(ItemType.MEDICATION_ADMINISTRATION).bool().get();
  private static Phenotype maNull =
      new Phe("maNull", "ma", "5").itemType(ItemType.MEDICATION_ADMINISTRATION).bool().get();
  private static Phenotype maNotDone =
      new Phe("maNotDone", "ma", "6").itemType(ItemType.MEDICATION_ADMINISTRATION).bool().get();
  private static Phenotype maError =
      new Phe("maError", "ma", "7").itemType(ItemType.MEDICATION_ADMINISTRATION).bool().get();

  // MedicationStatement
  private static Phenotype msActive =
      new Phe("msActive", "ms", "1").itemType(ItemType.MEDICATION_STATEMENT).bool().get();
  private static Phenotype msCompleted =
      new Phe("msCompleted", "ms", "2").itemType(ItemType.MEDICATION_STATEMENT).bool().get();
  private static Phenotype msIntended =
      new Phe("msIntended", "ms", "3").itemType(ItemType.MEDICATION_STATEMENT).bool().get();
  private static Phenotype msStopped =
      new Phe("msStopped", "ms", "4").itemType(ItemType.MEDICATION_STATEMENT).bool().get();
  private static Phenotype msNull =
      new Phe("msNull", "ms", "5").itemType(ItemType.MEDICATION_STATEMENT).bool().get();
  private static Phenotype msError =
      new Phe("msError", "ms", "6").itemType(ItemType.MEDICATION_STATEMENT).bool().get();

  // MedicationRequest
  private static Phenotype mrActive =
      new Phe("mrActive", "mr", "1").itemType(ItemType.MEDICATION_REQUEST).bool().get();
  private static Phenotype mrCompleted =
      new Phe("mrCompleted", "mr", "2").itemType(ItemType.MEDICATION_REQUEST).bool().get();
  private static Phenotype mrDraft =
      new Phe("mrDraft", "mr", "3").itemType(ItemType.MEDICATION_REQUEST).bool().get();
  private static Phenotype mrUnknown =
      new Phe("mrUnknown", "mr", "4").itemType(ItemType.MEDICATION_REQUEST).bool().get();
  private static Phenotype mrNull =
      new Phe("mrNull", "mr", "5").itemType(ItemType.MEDICATION_REQUEST).bool().get();
  private static Phenotype mrOnHold =
      new Phe("mrOnHold", "mr", "6").itemType(ItemType.MEDICATION_REQUEST).bool().get();
  private static Phenotype mrCancelled =
      new Phe("mrCancelled", "mr", "7").itemType(ItemType.MEDICATION_REQUEST).bool().get();
  private static Phenotype mrError =
      new Phe("mrError", "mr", "8").itemType(ItemType.MEDICATION_REQUEST).bool().get();
  private static Phenotype mrStopped =
      new Phe("mrStopped", "mr", "9").itemType(ItemType.MEDICATION_REQUEST).bool().get();

  @Test
  void encounterAndSubject() throws InstantiationException {
    // Encounters 1-4 ('finished', 'in-progress', 'triaged', 'onleave') and 5 (null) remain; 6-9 are
    // excluded
    assertEquals(Set.of("1", "2", "3", "4", "5"), search(male));
    assertEquals(Set.of("1", "2", "3", "4", "5"), search(enc));
    assertEquals(Set.of("1", "2", "3", "4", "5"), search(age));
  }

  @Test
  void observation() throws InstantiationException {
    // Valid statuses in Encounter 1 with obs_id ('final', 'preliminary', 'amended', 'corrected',
    // null)
    assertEquals(Set.of("1"), search(obsFinal));
    assertEquals(Set.of("1"), search(obsPreliminary));
    assertEquals(Set.of("1"), search(obsAmended));
    assertEquals(Set.of("1"), search(obsCorrected));
    assertEquals(Set.of("1"), search(obsNull));
    // Valid statuses in Encounter 1 without obs_id (NULL and empty string)
    assertEquals(Set.of("1"), search(obsWithoutIdFinal));
    assertEquals(Set.of("1"), search(obsWithoutIdNull));
    // Observations 91-97 (with and without obs_id) are excluded via obs_status or enc_status
    // ('Encounter/6')
    assertEquals(Set.of(), search(obsExcluded));
  }

  @Test
  void condition() throws InstantiationException {
    assertEquals(Set.of("1"), search(conConfirmed));
    assertEquals(Set.of("1"), search(conUnconfirmed));
    assertEquals(Set.of("1"), search(conProvisional));
    assertEquals(Set.of("1"), search(conDifferential));
    assertEquals(Set.of("1"), search(conNull));
    assertEquals(Set.of(), search(conRefuted));
    assertEquals(Set.of(), search(conError));
  }

  @Test
  void procedure() throws InstantiationException {
    assertEquals(Set.of("1"), search(proCompleted));
    assertEquals(Set.of("1"), search(proInProgress));
    assertEquals(Set.of("1"), search(proNotDone));
    assertEquals(Set.of("1"), search(proStopped));
    assertEquals(Set.of("1"), search(proNull));
    assertEquals(Set.of(), search(proError));
  }

  @Test
  void medicationAdministration() throws InstantiationException {
    assertEquals(Set.of("1"), search(maCompleted));
    assertEquals(Set.of("1"), search(maInProgress));
    assertEquals(Set.of("1"), search(maOnHold));
    assertEquals(Set.of("1"), search(maStopped));
    assertEquals(Set.of("1"), search(maNull));
    assertEquals(Set.of(), search(maNotDone));
    assertEquals(Set.of(), search(maError));
  }

  @Test
  void medicationStatement() throws InstantiationException {
    assertEquals(Set.of("1"), search(msActive));
    assertEquals(Set.of("1"), search(msCompleted));
    assertEquals(Set.of("1"), search(msIntended));
    assertEquals(Set.of("1"), search(msStopped));
    assertEquals(Set.of("1"), search(msNull));
    assertEquals(Set.of(), search(msError));
  }

  @Test
  void medicationRequest() throws InstantiationException {
    assertEquals(Set.of("1"), search(mrActive));
    assertEquals(Set.of("1"), search(mrCompleted));
    assertEquals(Set.of("1"), search(mrDraft));
    assertEquals(Set.of("1"), search(mrUnknown));
    assertEquals(Set.of("1"), search(mrNull));
    assertEquals(Set.of(), search(mrOnHold));
    assertEquals(Set.of(), search(mrCancelled));
    assertEquals(Set.of(), search(mrError));
    assertEquals(Set.of(), search(mrStopped));
  }

  private Set<String> search(Phenotype inc) throws InstantiationException {
    Que q =
        new Que(
                CONFIG,
                enc,
                sex,
                male,
                obs,
                obsFinal,
                obsPreliminary,
                obsAmended,
                obsCorrected,
                obsNull,
                obsWithoutIdFinal,
                obsWithoutIdNull,
                obsExcluded,
                age,
                conConfirmed,
                conUnconfirmed,
                conProvisional,
                conDifferential,
                conNull,
                conRefuted,
                conError,
                proCompleted,
                proInProgress,
                proNotDone,
                proStopped,
                proNull,
                proError,
                maCompleted,
                maInProgress,
                maOnHold,
                maStopped,
                maNull,
                maNotDone,
                maError,
                msActive,
                msCompleted,
                msIntended,
                msStopped,
                msNull,
                msError,
                mrActive,
                mrCompleted,
                mrDraft,
                mrUnknown,
                mrNull,
                mrOnHold,
                mrCancelled,
                mrError,
                mrStopped)
            .inc(inc);
    ResultSet rs =
        q.executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/status.sql").execute();
    return rs.getSubjectIds();
  }
}
