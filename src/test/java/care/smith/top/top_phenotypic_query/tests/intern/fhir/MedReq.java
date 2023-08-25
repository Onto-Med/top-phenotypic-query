package care.smith.top.top_phenotypic_query.tests.intern.fhir;

import java.time.LocalDateTime;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Timing;
import org.hl7.fhir.r4.model.Timing.TimingRepeatComponent;

import care.smith.top.top_phenotypic_query.util.DateUtil;

public class MedReq extends MedicationRequest {

  private static final long serialVersionUID = 1L;

  public MedReq() {}

  public MedReq(String identifier, Reference patRef, Reference medRef) {
    addIdentifier().setSystem(Client.SYSTEM).setValue(identifier);
    setSubject(patRef);
    setMedication(medRef);
  }

  public MedReq enc(Reference encRef) {
    setEncounter(encRef);
    return this;
  }

  public MedReq date(LocalDateTime date) {
    setAuthoredOnElement(new DateTimeType(DateUtil.toDate(date)));
    return this;
  }

  public MedReq date(String date) {
    return date(DateUtil.parse(date));
  }

  public MedReq dosageInstructionDate(LocalDateTime start, LocalDateTime end) {
    Period p = new Period().setStartElement(new DateTimeType(DateUtil.toDate(start)));
    if (end != null) p.setEndElement(new DateTimeType(DateUtil.toDate(end)));
    addDosageInstruction(
        new Dosage().setTiming(new Timing().setRepeat(new TimingRepeatComponent().setBounds(p))));
    return this;
  }

  public MedReq dosageInstructionDate(String start, String end) {
    return dosageInstructionDate(DateUtil.parse(start), DateUtil.parse(end));
  }

  public MedReq dosageInstructionStartDate(String start) {
    return dosageInstructionDate(DateUtil.parse(start), null);
  }
}
