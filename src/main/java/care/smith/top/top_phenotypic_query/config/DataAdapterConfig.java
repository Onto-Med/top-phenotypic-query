package care.smith.top.top_phenotypic_query.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DataAdapterConfig {

  private String id;
  private Map<String, String> connection;
  private String valueSeparator;
  private String paramSeparator;
  private String operEQ;
  private String operGT;
  private String operGE;
  private String operLT;
  private String operLE;
  private String paramOnlyId;
  private String paramOnlyCount;
  private String paramOnlySubject;
  private String paramLimit;
  private String paramId;
  private String paramSubject;
  private PatientQuery patientQuery;
  private Map<String, PropertyQuery> propertyQueries = new HashMap<>();

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

  public String getValueSeparator() {
    return valueSeparator;
  }

  public void setValueSeparator(String valueSeparator) {
    this.valueSeparator = valueSeparator;
  }

  public String getParamSeparator() {
    return paramSeparator;
  }

  public void setParamSeparator(String paramSeparator) {
    this.paramSeparator = paramSeparator;
  }

  public String getOperEQ() {
    return operEQ;
  }

  public void setOperEQ(String operEQ) {
    this.operEQ = operEQ;
  }

  public String getOperGT() {
    return operGT;
  }

  public void setOperGT(String operGT) {
    this.operGT = operGT;
  }

  public String getOperGE() {
    return operGE;
  }

  public void setOperGE(String operGE) {
    this.operGE = operGE;
  }

  public String getOperLT() {
    return operLT;
  }

  public void setOperLT(String operLT) {
    this.operLT = operLT;
  }

  public String getOperLE() {
    return operLE;
  }

  public void setOperLE(String operLE) {
    this.operLE = operLE;
  }

  public String getParamOnlyId() {
    return paramOnlyId;
  }

  public void setParamOnlyId(String paramOnlyId) {
    this.paramOnlyId = paramOnlyId;
  }

  public String getParamOnlyCount() {
    return paramOnlyCount;
  }

  public void setParamOnlyCount(String paramOnlyCount) {
    this.paramOnlyCount = paramOnlyCount;
  }

  public String getParamOnlySubject() {
    return paramOnlySubject;
  }

  public void setParamOnlySubject(String paramOnlySubject) {
    this.paramOnlySubject = paramOnlySubject;
  }

  public String getParamLimit() {
    return paramLimit;
  }

  public void setParamLimit(String paramLimit) {
    this.paramLimit = paramLimit;
  }

  public String getParamId() {
    return paramId;
  }

  public void setParamId(String paramId) {
    this.paramId = paramId;
  }

  public String getParamSubject() {
    return paramSubject;
  }

  public void setParamSubject(String paramSubject) {
    this.paramSubject = paramSubject;
  }

  public PatientQuery getPatientQuery() {
    return patientQuery.setDataAdapterConfig(this);
  }

  public void setPatientQuery(PatientQuery patientQuery) {
    this.patientQuery = patientQuery;
  }

  public Map<String, PropertyQuery> getPropertyQueries() {
    return propertyQueries;
  }

  public PropertyQuery getPropertyQuery(String type) {
    return propertyQueries.get(type).setDataAdapterConfig(this);
  }

  public void setPropertyQueries(List<PropertyQuery> propertyQueries) {
    for (PropertyQuery propertyQuery : propertyQueries)
      this.propertyQueries.put(propertyQuery.getType(), propertyQuery);
  }

  @Override
  public String toString() {
    return "DataAdapterConfig [id="
        + id
        + ", connection="
        + connection
        + ", valueSeparator="
        + valueSeparator
        + ", paramSeparator="
        + paramSeparator
        + ", operEQ="
        + operEQ
        + ", operGT="
        + operGT
        + ", operGE="
        + operGE
        + ", operLT="
        + operLT
        + ", operLE="
        + operLE
        + ", paramOnlyId="
        + paramOnlyId
        + ", paramOnlyCount="
        + paramOnlyCount
        + ", paramOnlySubject="
        + paramOnlySubject
        + ", paramLimit="
        + paramLimit
        + ", paramId="
        + paramId
        + ", paramSubject="
        + paramSubject
        + ", patientQuery="
        + patientQuery
        + ", propertyQueries="
        + propertyQueries
        + "]";
  }
}
