package care.smith.top.top_phenotypic_query.tests.intern.fhir;

import java.time.LocalDateTime;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Reference;

import care.smith.top.top_phenotypic_query.util.DateUtil;

public class Cond extends Condition {

  private static final long serialVersionUID = 1L;

  private CodeableConcept cc = new CodeableConcept();

  public Cond() {}

  public Cond(String identifier, Reference patRef) {
    addIdentifier().setSystem(Client.SYSTEM).setValue(identifier);
    setSubject(patRef);
    setCode(cc);
  }

  public Cond(String identifier) {
    addIdentifier().setSystem(Client.SYSTEM).setValue(identifier);
    setCode(cc);
  }

  public Cond enc(Reference encRef) {
    setEncounter(encRef);
    return this;
  }

  public Cond code(String system, String code) {
    cc.addCoding(new Coding().setSystem(system).setCode(code));
    return this;
  }

  public Cond date(LocalDateTime date) {
    setRecordedDate(DateUtil.toDate(date));
    return this;
  }

  public Cond date(String date) {
    return date(DateUtil.parse(date));
  }
}
