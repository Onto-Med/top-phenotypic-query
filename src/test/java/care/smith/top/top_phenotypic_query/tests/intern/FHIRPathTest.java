package care.smith.top.top_phenotypic_query.tests.intern;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Enumeration;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.TerserUtilHelper;

public class FHIRPathTest {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    Observation observation = new Observation();
    observation.setStatus(Observation.ObservationStatus.FINAL);
    observation
        .getCode()
        .addCoding()
        .setSystem("http://loinc.org")
        .setCode("789-8")
        .setDisplay("Erythrocytes [#/volume] in Blood by Automated count");
    observation.setValue(
        new Quantity()
            .setValue(4.12)
            .setUnit("10 trillion/L")
            .setSystem("http://unitsofmeasure.org")
            .setCode("10*12/L"));
    observation.addComponent().setValue(new Quantity().setValue(123));

    TerserUtilHelper helper = TerserUtilHelper.newHelper(FhirContext.forR4(), observation);
    IBase val = helper.getFieldValueByFhirPath("valueQuantity.value");
    System.out.println(((DecimalType) val).getValue());
    IBase compVal = helper.getFieldValueByFhirPath("component.valueQuantity.value");
    System.out.println(((DecimalType) compVal).getValue());

    Patient patient = new Patient();
    patient.addIdentifier().setSystem("http://acme.org/mrns").setValue("12345");
    patient.addName().setFamily("Jameson").addGiven("J").addGiven("Jonah");
    patient.setGender(Enumerations.AdministrativeGender.MALE);
    patient.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1990, 1, 1, 0, 0)));

    helper = TerserUtilHelper.newHelper(FhirContext.forR4(), patient);
    IBase id = helper.getFieldValueByFhirPath("identifier");
    IBase gender = helper.getFieldValueByFhirPath("gender");
    IBase birthdate = helper.getFieldValueByFhirPath("birthDate");
    System.out.println(((Identifier) id).getValue());
    System.out.println(((Enumeration<Enumerations.AdministrativeGender>) gender).asStringValue());
    System.out.println(birthdate);
  }
}
