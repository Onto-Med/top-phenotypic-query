package care.smith.top.top_phenotypic_query.tests.intern;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.fhir.FHIRClient;
import care.smith.top.top_phenotypic_query.adapter.sql.SQLAdapter;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Disabled;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Disabled
public class DB2FHIRTestIntern {

  private static final String SYSTEM = "https://www.top-test.de/system";
  private static FHIRClient client;

  public static void main(String[] args) throws SQLException {
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/FHIR_Adapter_Test.yml");
    DataAdapterConfig conf = DataAdapterConfig.getInstance(configFile.getPath());
    client = new FHIRClient(conf);
    client.deleteAllResources(SYSTEM);

    convert();
  }

  private static void convert() throws SQLException {
    URL configFile =
        Thread.currentThread()
            .getContextClassLoader()
            .getResource("config/Delir_SQL_Adapter_Test.yml");
    DataAdapterConfig sqlConfig = DataAdapterConfig.getInstance(configFile.getPath());
    SQLAdapter sqlAdapter = new SQLAdapter(sqlConfig);

    var subjects = sqlAdapter.executeQuery("SELECT subject_id, birth_date, sex FROM subject");

    Map<String, String> patIds = new HashMap<>();

   for (Map<String, Object> subject : subjects) {
      String patId = (String) subject.get("subject_id");
      LocalDateTime bd = ((Timestamp)subject.get("birth_date")).toLocalDateTime();
      String sex = (String) subject.get("sex");

      String fhirPatId = patIds.get(patId);
      if (fhirPatId == null) {
        fhirPatId = createPatient(patId, bd, sex);
        patIds.put(patId, fhirPatId);
      }
    }

    var observations =
        sqlAdapter.executeQuery(
            "SELECT phenotype_id, subject_id, created_at, code_system, code, unit, number_value FROM observation");
    for (Map<String, Object> observation : observations) {
      Phenotype p = new Phenotype(observation, patIds, true);
      if (p.getValue() != null) createObservation(p);
    }

    var conditions =
        sqlAdapter.executeQuery(
            "SELECT phenotype_id, subject_id, created_at, code_system, code FROM condition");
    for (Map<String, Object> condition: conditions) createCondition(new Phenotype(condition, patIds, false));

    var medications =
        sqlAdapter.executeQuery(
            "SELECT phenotype_id, subject_id, created_at, code_system, code FROM medication");
    for (Map<String, Object> medication: medications) createMedication(new Phenotype(medication, patIds, false));
    
    var procedures =
        sqlAdapter.executeQuery(
            "SELECT phenotype_id, subject_id, created_at, code_system, code FROM procedure");
    for (Map<String, Object> procedure: procedures) createProcedure(new Phenotype(procedure, patIds, false));
  }

  private static String createPatient(String id, LocalDateTime bd, String sex) {
    Patient patient = new Patient();
    patient.addIdentifier().setSystem(SYSTEM).setValue(id);
    patient.setGender(Enumerations.AdministrativeGender.valueOf(sex.toUpperCase()));
    patient.setBirthDate(Timestamp.valueOf(bd));
    return client.createResource(patient).get();
  }

  private static void createObservation(Phenotype p) {
    Quantity v =
        new Quantity()
            .setValue(p.getValue())
            .setUnit(p.getUnit())
            .setSystem("http://unitsofmeasure.org")
            .setCode(p.getUnit());
    Observation observation =
        new Observation().setValue(v).setSubject(new Reference("Patient/" + p.getPatientId()));
    observation.addIdentifier().setSystem(SYSTEM).setValue(p.getId());
    observation.setEffective(new DateTimeType(p.getDate()));
    observation.setCode(
        new CodeableConcept(new Coding().setSystem(p.getSystem()).setCode(p.getCode())));
    client.createResource(observation).get();
  }

  private static void createCondition(Phenotype p) {
    Condition condition = new Condition().setSubject(new Reference("Patient/" + p.getPatientId()));
    condition.addIdentifier().setSystem(SYSTEM).setValue(p.getId());
    condition.setRecordedDate(p.getDate());
    condition.setCode(
        new CodeableConcept(new Coding().setSystem(p.getSystem()).setCode(p.getCode())));
    client.createResource(condition).get();
  }

  private static void createProcedure(Phenotype p) {
    Procedure procedure = new Procedure().setSubject(new Reference("Patient/" + p.getPatientId()));
    procedure.addIdentifier().setSystem(SYSTEM).setValue(p.getId());
    procedure.setPerformed(new DateTimeType(p.getDate()));
    procedure.setCode(
        new CodeableConcept(new Coding().setSystem(p.getSystem()).setCode(p.getCode())));
    client.createResource(procedure).get();
  }

  private static void createMedication(Phenotype p) {
    MedicationAdministration med =
        new MedicationAdministration().setSubject(new Reference("Patient/" + p.getPatientId()));
    med.addIdentifier().setSystem(SYSTEM).setValue(p.getId());
    med.setEffective(new DateTimeType(p.getDate()));
    med.setMedication(
        new CodeableConcept(new Coding().setSystem(p.getSystem()).setCode(p.getCode())));
    client.createResource(med).get();
  }

  static class Phenotype {
    String id;
    String patientId;
    Date date;
    String system;
    String code;
    String unit;
    BigDecimal value;

    Phenotype(Map<String, Object> row, Map<String, String> patIds, boolean withValue) {
      id = (String) row.get("phenotype_id");
      patientId = patIds.get(row.get("subject_id"));
      date = (Date) row.get("created_at");
      system = (String) row.get("code_system");
      code = (String) row.get("code");
      if (withValue) {
        unit = (String) row.get("unit");
        value = (BigDecimal) row.get("number_value");
      }
    }

    String getId() {
      return id;
    }

    String getPatientId() {
      return patientId;
    }

    Date getDate() {
      return date;
    }

    String getSystem() {
      return system;
    }

    String getCode() {
      return code;
    }

    String getUnit() {
      return unit;
    }

    BigDecimal getValue() {
      return value;
    }
  }
}
