package care.smith.top.top_phenotypic_query.tests.intern.fhir;

import java.time.LocalDateTime;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;

import care.smith.top.top_phenotypic_query.util.DateUtil;

public class Enc extends Encounter {

  private static final long serialVersionUID = 1L;

  public Enc() {}

  public Enc(String identifier, Reference patRef) {
    addIdentifier().setSystem(Client.SYSTEM).setValue(identifier);
    setSubject(patRef);
  }

  public Enc cls(String system, String code) {
    setClass_(new Coding().setSystem(system).setCode(code));
    return this;
  }

  public Enc period(LocalDateTime start, LocalDateTime end) {
    setPeriod(new Period().setStart(DateUtil.toDate(start)).setEnd(DateUtil.toDate(end)));
    return this;
  }

  public Enc period(String start, String end) {
    return period(DateUtil.parse(start), DateUtil.parse(end));
  }

  public Enc start(String start) {
    setPeriod(new Period().setStart(DateUtil.parseToDate(start)));
    return this;
  }

  public Enc partOf(Reference encRef) {
    setPartOf(encRef);
    return this;
  }
}
