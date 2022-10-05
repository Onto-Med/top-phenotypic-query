package care.smith.top.top_phenotypic_query.adapter.fhir;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Resource;

import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.NumberRestriction;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Restriction;
import care.smith.top.model.StringRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterFormat;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQueryBuilder;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.PhenotypeUtil;

public class FHIRAdapter extends DataAdapter {

  private FHIRClient client;

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
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.executeQuery(search.getQueryString());
    PhenotypeOutput out = search.getOutput();
    Phenotype phe = search.getPhenotype();
    DataType datatype = phe.getDataType();

    for (Resource res : resources) {
      String sbj = FHIRUtil.getString(client.evaluateFHIRPath(res, out.getSubject()));
      LocalDateTime date = FHIRUtil.getDate(client.evaluateFHIRPath(res, out.getDate()));
      Value val = null;
      if (datatype == DataType.DATE_TIME)
        val =
            new DateTimeValue(
                FHIRUtil.getDate(client.evaluateFHIRPath(res, out.getDatePhenotype())), date);
      else if (datatype == DataType.NUMBER)
        val =
            new DecimalValue(
                FHIRUtil.getNumber(client.evaluateFHIRPath(res, out.getNumberPhenotype())), date);
      else
        val =
            new StringValue(
                FHIRUtil.getString(client.evaluateFHIRPath(res, out.getStringPhenotype())), date);
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
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.executeQuery(search.getQueryString());
    SubjectOutput out = search.getOutput();
    Phenotype sex = search.getSex();
    Phenotype bd = search.getBirthdateDerived();
    Phenotype age = search.getAge();

    for (Resource res : resources) {
      String sbj = FHIRUtil.getId(res);
      if (bd != null) {
        LocalDateTime bdVal = FHIRUtil.getDate(client.evaluateFHIRPath(res, out.getBirthdate()));
        if (bdVal != null) {
          DateTimeValue val = new DateTimeValue(bdVal);
          if (search.getBirthdate() != null) rs.addValueWithRestriction(sbj, bd, null, val);
          else rs.addValue(sbj, bd, null, val);
          if (age != null) {
            Value ageVal = new DecimalValue(SubjectSearch.birthdateToAge(bdVal));
            rs.addValueWithRestriction(sbj, age, null, ageVal);
          }
        }
      }
      if (sex != null) {
        String sexVal = FHIRUtil.getString(client.evaluateFHIRPath(res, out.getSex()));
        if (sexVal != null) rs.addValueWithRestriction(sbj, sex, null, new StringValue(sexVal));
      }
    }

    return rs;
  }

  @Override
  public ResultSet executeAllSubjectsQuery() {
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.executeQuery(SubjectSearch.getBaseQuery(config));
    for (Resource res : resources) rs.addSubject(FHIRUtil.getId(res));
    return rs;
  }

  @Override
  public DataAdapterFormat getFormat() {
    return FHIRAdapterFormat.get();
  }

  @Override
  public void close() {}

  @Override
  public void addValueIntervalLimit(
      String operator, String value, PhenotypeQueryBuilder builder, Restriction restriction) {
    if (restriction instanceof NumberRestriction) builder.numberValueIntervalLimit(operator, value);
    else if (restriction instanceof DateTimeRestriction)
      builder.dateValueIntervalLimit(operator, value);
  }

  @Override
  public void addValueList(
      String valuesAsString, PhenotypeQueryBuilder builder, Restriction restriction) {
    if (restriction instanceof NumberRestriction) builder.numberValueList(valuesAsString);
    else if (restriction instanceof StringRestriction) {
      if (valuesAsString.startsWith("http")) builder.conceptValueList(valuesAsString);
      else builder.stringValueList(valuesAsString);
    }
  }

  @Override
  public Map<String, String> getPhenotypeMappings(Phenotype phenotype, DataAdapterConfig config) {
    CodeMapping codeMap = config.getCodeMapping(phenotype);
    if (codeMap == null) return null;
    Map<String, String> pheMap = codeMap.getPhenotypeMappings();
    if (pheMap != null) return pheMap;
    String codes = getFormat().formatList(PhenotypeUtil.getCodeUris(phenotype));
    return Collections.singletonMap("codes", codes);
  }
}
