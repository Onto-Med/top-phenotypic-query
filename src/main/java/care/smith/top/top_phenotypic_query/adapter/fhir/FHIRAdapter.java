package care.smith.top.top_phenotypic_query.adapter.fhir;

import java.time.LocalDateTime;
import java.util.List;

import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterSettings;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class FHIRAdapter extends DataAdapter {

  private FHIRClient client;
  private static final Logger log = LoggerFactory.getLogger(FHIRAdapter.class);

  public FHIRAdapter(DataAdapterConfig config) {
    super(config);
    this.client = new FHIRClient(config);
  }

  public FHIRAdapter(String configFilePath) {
    super(configFilePath);
    this.client = new FHIRClient(config);
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    String query = getSettings().createSinglePreparedQuery(search);
    log.debug("Execute FHIR query: {}", query);
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.executeQuery(query);
    PhenotypeOutput out = search.getOutput();
    Phenotype phe = search.getPhenotype();
    String pheCol = out.getValue(phe.getDataType());

    for (Resource res : resources) {
      String sbj = FHIRUtil.getString(client.evaluateFHIRPath(res, out.getSubject()));
      LocalDateTime date = FHIRUtil.getDate(client.evaluateFHIRPath(res, out.getDateTime()));
      Value val = null;
      if (Phenotypes.hasDateTimeType(phe))
        val = Val.of(FHIRUtil.getDate(client.evaluateFHIRPath(res, pheCol)), date);
      else if (Phenotypes.hasNumberType(phe))
        val = Val.of(FHIRUtil.getNumber(client.evaluateFHIRPath(res, pheCol)), date);
      else if (Phenotypes.hasBooleanType(phe)) {
        rs.addValue(sbj, phe, search.getDateTimeRestriction(), Val.ofTrue());
        continue;
      } else val = Val.of(FHIRUtil.getString(client.evaluateFHIRPath(res, pheCol)), date);
      if (val != null)
        rs.addValueWithRestriction(
            sbj,
            phe,
            search.getDateTimeRestriction(),
            val,
            search.getSourceUnit(),
            search.getModelUnit());
    }

    return rs;
  }

  @Override
  public ResultSet execute(SubjectSearch search) {
    String query = getSettings().createSubjectPreparedQuery(search);
    log.debug("Execute FHIR query: {}", query);
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.executeQuery(query);
    SubjectOutput out = search.getOutput();
    Phenotype sex = search.getSex();
    Phenotype bd = search.getBirthdateDerived();
    Phenotype age = search.getAge();

    for (Resource res : resources) {
      String sbj = FHIRUtil.getId(res);
      if (bd != null) {
        LocalDateTime bdVal = FHIRUtil.getDate(client.evaluateFHIRPath(res, out.getBirthdate()));
        if (bdVal != null) {
          Value val = Val.of(bdVal);
          if (search.getBirthdate() != null) rs.addValueWithRestriction(sbj, bd, null, val);
          else rs.addValue(sbj, bd, null, val);
          if (age != null) {
            Value ageVal = Val.of(SubjectSearch.birthdateToAge(bdVal));
            rs.addValueWithRestriction(sbj, age, null, ageVal);
          }
        }
      }
      if (sex != null) {
        String sexVal = FHIRUtil.getString(client.evaluateFHIRPath(res, out.getSex()));
        if (sexVal != null) rs.addValueWithRestriction(sbj, sex, null, Val.of(sexVal));
      }
    }

    return rs;
  }

  @Override
  public ResultSet executeAllSubjectsQuery() {
    String query = SubjectSearch.getBaseQuery(config);
    log.debug("Execute FHIR query: {}", query);
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.executeQuery(query);
    for (Resource res : resources) rs.addSubject(FHIRUtil.getId(res));
    return rs;
  }

  @Override
  public DataAdapterSettings getSettings() {
    return FHIRAdapterSettings.get();
  }

  @Override
  public void close() {}
}
