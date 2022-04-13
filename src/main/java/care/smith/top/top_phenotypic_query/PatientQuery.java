package care.smith.top.top_phenotypic_query;

import java.util.Map;

public class PatientQuery {

  private String allQuery;
  private String parameterQueryBase;
  private PatientQueryParameters parameters;
  private Map<String, String> output;

  public String getAllQuery() {
    return allQuery;
  }

  public void setAllQuery(String allQuery) {
    this.allQuery = allQuery;
  }

  public String getParameterQueryBase() {
    return parameterQueryBase;
  }

  public void setParameterQueryBase(String parameterQueryBase) {
    this.parameterQueryBase = parameterQueryBase;
  }

  public PatientQueryParameters getParameters() {
    return parameters;
  }

  public void setParameters(PatientQueryParameters parameters) {
    this.parameters = parameters;
  }

  public Map<String, String> getOutput() {
    return output;
  }

  public void setOutput(Map<String, String> output) {
    this.output = output;
  }

  public String getParameterQuery(DataAdapterConfig conf, String... params) {
    return getParameterQueryBase() + conf.getParametersAsString(params);
  }

  @Override
  public String toString() {
    return "PatientQuery [allQuery="
        + allQuery
        + ", parameterQueryBase="
        + parameterQueryBase
        + ", parameters="
        + parameters
        + ", output="
        + output
        + "]";
  }
}
