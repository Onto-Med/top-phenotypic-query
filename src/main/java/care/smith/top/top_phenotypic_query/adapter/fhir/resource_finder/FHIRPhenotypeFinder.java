package care.smith.top.top_phenotypic_query.adapter.fhir.resource_finder;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.fhir.FHIRPath;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.hl7.fhir.r4.model.Resource;

public class FHIRPhenotypeFinder extends FHIRPathResourceFinder {

  private SingleSearch search;
  private PhenotypeOutput out;
  private Phenotype phe;
  private String phePathExp;

  public FHIRPhenotypeFinder(
      IGenericClient client, FHIRPath path, ResultSet rs, SingleSearch search) {
    super(client, path, rs);
    this.search = search;
    this.out = search.getOutput();
    this.phe = search.getPhenotype();
    this.phePathExp = out.getValue(Phenotypes.getDataType(phe));
  }

  @Override
  protected void addResource(Resource res) {
    String sbj = path.getString(res, out.getSubject());
    LocalDateTime date = path.getDateTime(res, out.getDateTime());
    LocalDateTime startDate = path.getDateTime(res, out.getStartDateTime());
    LocalDateTime endDate = path.getDateTime(res, out.getEndDateTime());

    if (Phenotypes.hasBooleanType(phe)) {
      Value val = Val.ofTrue(date, startDate, endDate);
      addFields(val, res, out.getFields());
      rs.addValue(sbj, phe, search.getDateTimeRestriction(), val);
      return;
    }

    Value val = null;

    if (Phenotypes.hasDateTimeType(phe)) {
      LocalDateTime pathVal = path.getDateTime(res, phePathExp);
      if (pathVal != null) val = Val.of(pathVal, date, startDate, endDate);
    } else if (Phenotypes.hasNumberType(phe)) {
      BigDecimal pathVal = path.getNumber(res, phePathExp);
      if (pathVal != null) val = Val.of(pathVal, date, startDate, endDate);
    } else {
      String pathVal = path.getString(res, phePathExp);
      if (pathVal != null) val = Val.of(pathVal, date, startDate, endDate);
    }
    if (val != null) {
      addFields(val, res, out.getFields());
      rs.addValueWithRestriction(
          sbj,
          phe,
          search.getDateTimeRestriction(),
          val,
          search.getSourceUnit(),
          search.getModelUnit());
    }
  }

  private void addFields(Value val, Resource res, Map<String, String> fields) {
    if (fields == null) return;
    for (String fieldName : fields.keySet()) {
      String v = path.getString(res, fields.get(fieldName));
      if (v != null) val.putFieldsItem(fieldName, Val.of(v));
    }
  }
}
