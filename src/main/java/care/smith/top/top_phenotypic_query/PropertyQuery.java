package care.smith.top.top_phenotypic_query;

import java.util.HashMap;
import java.util.Map;

public class PropertyQuery {

  private String type;
  private String allQuery;
  private String parameterQueryBase;
  private PropertyQueryParameters parameters;
  private Map<String, String> output = new HashMap<>();

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

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

  public PropertyQueryParameters getParameters() {
    return parameters;
  }

  public void setParameters(PropertyQueryParameters parameters) {
    this.parameters = parameters;
  }

  public Map<String, String> getOutput() {
    return output;
  }

  public void setOutput(Map<String, String> output) {
    this.output = output;
  }

  @Override
  public String toString() {
    return "PropertyQuery [type="
        + type
        + ", allQuery="
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
