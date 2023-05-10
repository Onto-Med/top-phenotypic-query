package care.smith.top.top_phenotypic_query.adapter.fhir;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.utils.FHIRPathEngine;
import org.springframework.util.CollectionUtils;

import ca.uhn.fhir.context.FhirContext;

public class FHIRPath {

  private FHIRPathEngine engine;

  public FHIRPath(FhirContext ctx) {
    this.engine = new FHIRPathEngine(new HapiWorkerContext(ctx, ctx.getValidationSupport()));
  }

  public List<Base> evaluate(Resource res, String path) {
    return engine.evaluate(res, path);
  }

  public String getString(Resource res, String path) {
    return FHIRUtil.getString(getValue(res, path));
  }

  public BigDecimal getNumber(Resource res, String path) {
    return FHIRUtil.getNumber(getValue(res, path));
  }

  public LocalDateTime getDateTime(Resource res, String path) {
    return FHIRUtil.getDateTime(getValue(res, path));
  }

  public Boolean getBoolean(Resource res, String path) {
    return FHIRUtil.getBoolean(getValue(res, path));
  }

  public Base getValue(Resource res, String path) {
    List<Base> values = evaluate(res, path);
    if (CollectionUtils.isEmpty(values)) return null;
    return values.get(0);
  }
}
