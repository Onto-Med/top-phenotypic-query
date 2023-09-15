package care.smith.top.top_phenotypic_query.tests.intern.fhir;

import care.smith.top.top_phenotypic_query.util.DateUtil;
import java.time.LocalDateTime;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;

public class MedAdm extends MedicationAdministration {

  private static final long serialVersionUID = 1L;

  public MedAdm() {}

  public MedAdm(String identifier, Reference patRef, Reference medRef) {
    addIdentifier().setSystem(Client.SYSTEM).setValue(identifier);
    setSubject(patRef);
    setMedication(medRef);
  }

  public MedAdm(String identifier, Reference medRef) {
    addIdentifier().setSystem(Client.SYSTEM).setValue(identifier);
    setMedication(medRef);
  }

  public MedAdm enc(Reference encRef) {
    setContext(encRef);
    return this;
  }

  public MedAdm date(LocalDateTime date) {
    setEffective(new DateTimeType(DateUtil.toDate(date)));
    return this;
  }

  public MedAdm date(String date) {
    return date(DateUtil.parse(date));
  }

  public MedAdm date(LocalDateTime start, LocalDateTime end) {
    setEffective(new Period().setStart(DateUtil.toDate(start)).setEnd(DateUtil.toDate(end)));
    return this;
  }

  public MedAdm date(String start, String end) {
    return date(DateUtil.parse(start), DateUtil.parse(end));
  }
}
