package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class QueryBuilder {

  private StringBuffer queryString = new StringBuffer();

  protected QueryBuilder() {}

  protected void add(String queryPart) {
    queryString.append(queryPart);
  }

  protected void add(String queryPart, Map<String, String> map) {
    add(replace(queryPart, map));
  }

  protected void add(String queryPart, String values) {
    add(queryPart.replace("{values}", values));
  }

  protected void add(String queryPart, String operator, String value) {
    add(queryPart.replace("{operator}", operator).replace("{value}", value));
  }

  protected void add(String queryPart, String values, Map<String, String> map) {
    add(queryPart.replace("{values}", values), map);
  }

  protected void add(String queryPart, String operator, String value, Map<String, String> map) {
    add(queryPart.replace("{operator}", operator).replace("{value}", value), map);
  }

  public static String replace(String s, Map<String, String> map) {
    if (map == null) return s;
    for (String key : map.keySet()) s = s.replace("{" + key + "}", map.get(key));
    return s;
  }

  public String build() {
    return queryString.toString();
  }
}
