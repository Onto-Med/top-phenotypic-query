package care.smith.top.top_phenotypic_query.adapter.mapping;

import java.util.HashMap;
import java.util.Map;

public class CodeMapping {

  private String type;
  private Map<String, String> mapping = new HashMap<>();

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<String, String> getMapping() {
    return mapping;
  }

  public void setMapping(Map<String, String> mapping) {
    this.mapping = mapping;
  }

  public String getValue(String key) {
    return mapping.get(key);
  }

  @Override
  public String toString() {
    return "Mapping [type=" + type + ", mapping=" + mapping + "]";
  }
}
