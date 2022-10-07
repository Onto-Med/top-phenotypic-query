package care.smith.top.top_phenotypic_query.tests.intern;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.context.IWorkerContext;
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.utils.FHIRPathEngine;

import ca.uhn.fhir.context.FhirContext;

public class FHIRPathTestIntern {

  public static void main(String[] args) {
    IWorkerContext worker =
        new HapiWorkerContext(FhirContext.forR4(), FhirContext.forR4().getValidationSupport());
    FHIRPathEngine engine = new FHIRPathEngine(worker);

    Condition condition = new Condition().setRecordedDate(new Date(99999999));
    List<Base> res = engine.evaluate(condition, "recordedDate");
    System.out.println(res);

    Observation observation =
        new Observation()
            .setValue(
                new Quantity()
                    .setValue(4.12)
                    .setUnit("10 trillion/L")
                    .setSystem("http://unitsofmeasure.org")
                    .setCode("10*12/L"))
            .setSubject(new Reference("Patient/123"))
            .setEffective(new DateTimeType("2000-01-01"));
    res = engine.evaluate(observation, "value.value");
    System.out.println(res);
    res = engine.evaluate(observation, "subject.reference.value");
    System.out.println(res.get(0).toString());
    res = engine.evaluate(observation, "effective");
    System.out.println("Date:" + res);

    observation = new Observation().setValue(new StringType("abc"));
    res = engine.evaluate(observation, "value");
    System.out.println(res);

    observation = new Observation().setValue(new DateTimeType("2000-01-01"));
    res = engine.evaluate(observation, "value");
    System.out.println(res);

    observation =
        new Observation()
            .setValue(
                new CodeableConcept(new Coding().setSystem("http://system.com").setCode("1234")));
    res = engine.evaluate(observation, "value.coding.select(system.value + '|' + code)");
    System.out.println(res);

    observation
        .addComponent()
        .setCode(new CodeableConcept(new Coding().setSystem("http://system.com").setCode("xxx")))
        .setValue(new Quantity().setValue(111));
    observation
        .addComponent()
        .setCode(new CodeableConcept(new Coding().setSystem("http://system2.com").setCode("bbb")))
        .setValue(
            new CodeableConcept(new Coding().setSystem("http://value.system.com").setCode("ccc")));

    res =
        engine.evaluate(
            observation,
            "component.where(code.coding.system.value + '|' + code.coding.code = 'http://system.com|xxx').value.value");
    System.out.println(res);
    res =
        engine.evaluate(
            observation,
            "component.where(code.coding.system.value + '|' + code.coding.code = 'http://system2.com|bbb').value.coding.select(system.value + '|' + code)");
    System.out.println(res);

    Patient patient = new Patient();
    patient.addIdentifier().setSystem("http://acme.org/mrns").setValue("12345");
    patient.addName().setFamily("Jameson").addGiven("J").addGiven("Jonah");
    patient.setGender(Enumerations.AdministrativeGender.MALE);
    patient.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1990, 1, 1, 0, 0)));

    res = engine.evaluate(patient, "identifier.system.value");
    System.out.println(res);
    res = engine.evaluate(patient, "gender.value");
    System.out.println(res);
    res = engine.evaluate(patient, "birthDate");
    System.out.println(res);
  }
}
