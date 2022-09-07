package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class QueryBuilder {

  private StringBuffer queryString = new StringBuffer();

  protected QueryBuilder() {}

  protected void add(String queryPart) {
    queryString.append(queryPart);
  }

  protected void add(String queryPart, Map<String, String> map) {
    queryString.append(replace(queryPart, map));
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
