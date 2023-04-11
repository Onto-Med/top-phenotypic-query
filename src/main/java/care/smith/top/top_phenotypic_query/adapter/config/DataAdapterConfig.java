package care.smith.top.top_phenotypic_query.adapter.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import care.smith.top.model.Code;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.util.Phenotypes;

public class DataAdapterConfig {

  private String id;
  private String adapter;
  private Map<String, String> connection;
  private CSVSettings csvSettings;
  private SubjectQuery subjectQuery;
  private Map<String, PhenotypeQuery> phenotypeQueries = new HashMap<>();
  private CodeMapping birthdateMapping;
  private CodeMapping ageMapping;
  private CodeMapping sexMapping;
  private Map<String, CodeMapping> codeMappings = new HashMap<>();

  public static DataAdapterConfig getInstance(String yamlFilePath) {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    DataAdapterConfig config = null;
    try {
      config = mapper.readValue(new File(yamlFilePath), DataAdapterConfig.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return config;
  }

  public String getAdapter() {
    return adapter;
  }

  public void setAdapter(String adapter) {
    this.adapter = adapter;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, String> getConnection() {
    return connection;
  }

  public String getConnectionAttribute(String name) {
    return connection.get(name);
  }

  public void setConnection(Map<String, String> connection) {
    this.connection = connection;
  }

  public CSVSettings getCsvSettings() {
    return csvSettings;
  }

  public void setCsvSettings(CSVSettings csvSettings) {
    this.csvSettings = csvSettings;
  }

  public SubjectQuery getSubjectQuery() {
    return subjectQuery;
  }

  public void setSubjectQuery(SubjectQuery subjectQuery) {
    this.subjectQuery = subjectQuery;
  }

  public Map<String, PhenotypeQuery> getPhenotypeQueries() {
    return phenotypeQueries;
  }

  public PhenotypeQuery getPhenotypeQuery(String type) {
    return phenotypeQueries.get(type);
  }

  public void setPhenotypeQueries(Map<String, PhenotypeQuery> phenotypeQueries) {
    this.phenotypeQueries = phenotypeQueries;
  }

  public CodeMapping getBirthdateMapping() {
    return birthdateMapping;
  }

  public void setBirthdateMapping(CodeMapping birthdateMapping) {
    this.birthdateMapping = birthdateMapping;
  }

  public boolean isBirthdate(Phenotype p) {
    for (Code code : Phenotypes.getUnrestrictedPhenotypeCodes(p)) {
      if (birthdateMapping.getCode().equals(Phenotypes.getCodeUri(code))) return true;
    }
    return false;
  }

  public CodeMapping getAgeMapping() {
    return ageMapping;
  }

  public void setAgeMapping(CodeMapping ageMapping) {
    this.ageMapping = ageMapping;
  }

  public boolean isAge(Phenotype p) {
    for (Code code : Phenotypes.getUnrestrictedPhenotypeCodes(p)) {
      if (ageMapping.getCode().equals(Phenotypes.getCodeUri(code))) return true;
    }
    return false;
  }

  public CodeMapping getSexMapping() {
    return sexMapping;
  }

  public void setSexMapping(CodeMapping sexMapping) {
    this.sexMapping = sexMapping;
  }

  public boolean isSex(Phenotype p) {
    for (Code code : Phenotypes.getUnrestrictedPhenotypeCodes(p)) {
      if (sexMapping.getCode().equals(Phenotypes.getCodeUri(code))) return true;
    }
    return false;
  }

  public CodeMapping getCodeMapping(String code) {
    return codeMappings.get(code);
  }

  public CodeMapping getCodeMapping(Phenotype p) {
    for (Code code : Phenotypes.getUnrestrictedPhenotypeCodes(p)) {
      CodeMapping map = getCodeMapping(Phenotypes.getCodeUri(code));
      if (map != null) return map;
    }
    return null;
  }

  public CodeMapping getCodeMappingIncludingSubjectParameters(Phenotype p) {
    if (isAge(p)) return getAgeMapping();
    if (isBirthdate(p)) return getBirthdateMapping();
    if (isSex(p)) return getSexMapping();
    return getCodeMapping(p);
  }

  public void setCodeMappings(List<CodeMapping> codeMappings) {
    if (codeMappings != null)
      for (CodeMapping cm : codeMappings) this.codeMappings.put(cm.getCode(), cm);
  }

  @Override
  public String toString() {
    return "DataAdapterConfig [id="
        + id
        + ", adapter="
        + adapter
        + ", connection="
        + connection
        + ", csvSettings="
        + csvSettings
        + ", subjectQuery="
        + subjectQuery
        + ", phenotypeQueries="
        + phenotypeQueries
        + ", birthdateMapping="
        + birthdateMapping
        + ", ageMapping="
        + ageMapping
        + ", sexMapping="
        + sexMapping
        + ", codeMappings="
        + codeMappings
        + "]";
  }
}
