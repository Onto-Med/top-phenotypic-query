package care.smith.top.top_phenotypic_query;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class FHIRDataConfig {

  private String id;
  private String base;
  private String valueSeparator;
  private String parameterSeparator;
  private Operators operators;
  private GlobalParameters globalParameters;
  private PatientQuery patientQuery;
  private Map<String, PropertyQuery> propertyQueries = new HashMap<>();

  public static FHIRDataConfig getInstance(String yamlFilePath) {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    FHIRDataConfig config = null;
    try {
      config = mapper.readValue(new File(yamlFilePath), FHIRDataConfig.class);
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

  public String getBase() {
    return base;
  }

  public void setBase(String base) {
    this.base = base;
  }

  public String getValueSeparator() {
    return valueSeparator;
  }

  public void setValueSeparator(String valueSeparator) {
    this.valueSeparator = valueSeparator;
  }

  public String getParameterSeparator() {
    return parameterSeparator;
  }

  public void setParameterSeparator(String parameterSeparator) {
    this.parameterSeparator = parameterSeparator;
  }

  public Operators getOperators() {
    return operators;
  }

  public void setOperators(Operators operators) {
    this.operators = operators;
  }

  public GlobalParameters getGlobalParameters() {
    return globalParameters;
  }

  public void setGlobalParameters(GlobalParameters globalParameters) {
    this.globalParameters = globalParameters;
  }

  public PatientQuery getPatientQuery() {
    return patientQuery;
  }

  public void setPatientQuery(PatientQuery patientQuery) {
    this.patientQuery = patientQuery;
  }

  public Map<String, PropertyQuery> getPropertyQueries() {
    return propertyQueries;
  }

  public PropertyQuery getPropertyQuery(String type) {
    return propertyQueries.get(type);
  }

  public void setPropertyQueries(List<PropertyQuery> propertyQueries) {
    for (PropertyQuery propertyQuery : propertyQueries)
      this.propertyQueries.put(propertyQuery.getType(), propertyQuery);
  }

  public String getValuesAsString(String... values) {
    return String.join(getValueSeparator(), values);
  }

  public String getParametersAsString(String... params) {
    return String.join(getParameterSeparator(), params);
  }

  @Override
  public String toString() {
    return "FHIRDataAdapter [id="
        + id
        + ", base="
        + base
        + ", valueSeparator="
        + valueSeparator
        + ", parameterSeparator="
        + parameterSeparator
        + ", operators="
        + operators
        + ", globalParameters="
        + globalParameters
        + ", patientQuery="
        + patientQuery
        + ", propertyQueries="
        + propertyQueries
        + "]";
  }
}
