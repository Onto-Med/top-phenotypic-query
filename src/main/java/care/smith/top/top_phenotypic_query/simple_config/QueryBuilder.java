package care.smith.top.top_phenotypic_query.simple_config;

import java.util.Map;
import java.util.Map.Entry;

public class QueryBuilder {

  private StringBuffer queryString = new StringBuffer();

  protected QueryBuilder() {}

  protected void add(String queryPart) {
    queryString.append(queryPart);
  }

  protected void add(String queryPart, Map<String, String> mappings) {
    for (Entry<String, String> m : mappings.entrySet())
      queryPart = queryPart.replace("{" + m.getKey() + "}", m.getValue());
    queryString.append(queryPart);
  }

  public String build() {
    return queryString.toString();
  }
}
