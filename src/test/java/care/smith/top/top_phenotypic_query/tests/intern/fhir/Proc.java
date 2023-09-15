package care.smith.top.top_phenotypic_query.tests.intern.fhir;

import care.smith.top.top_phenotypic_query.util.DateUtil;
import java.time.LocalDateTime;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;

public class Proc extends Procedure {

  private static final long serialVersionUID = 1L;

  private CodeableConcept cc = new CodeableConcept();

  public Proc() {}

  public Proc(String identifier, Reference patRef) {
    addIdentifier().setSystem(Client.SYSTEM).setValue(identifier);
    setSubject(patRef);
    setCode(cc);
  }

  public Proc(String identifier) {
    addIdentifier().setSystem(Client.SYSTEM).setValue(identifier);
    setCode(cc);
  }

  public Proc enc(Reference encRef) {
    setEncounter(encRef);
    return this;
  }

  public Proc code(String system, String code) {
    cc.addCoding(new Coding().setSystem(system).setCode(code));
    return this;
  }

  public Proc date(LocalDateTime date) {
    setPerformed(new DateTimeType(DateUtil.toDate(date)));
    return this;
  }

  public Proc date(String date) {
    return date(DateUtil.parse(date));
  }
}
