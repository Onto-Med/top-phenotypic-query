package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class QueryBuilder {

  private static final String OPEN_BRACKET = "{";
  private static final String CLOSED_BRACKET = "}";
  private static final String VALUE = OPEN_BRACKET + "value" + CLOSED_BRACKET;
  private static final String VALUES = OPEN_BRACKET + "values" + CLOSED_BRACKET;
  private static final String OPERATOR = OPEN_BRACKET + "operator" + CLOSED_BRACKET;

  private StringBuffer queryString = new StringBuffer();

  protected QueryBuilder() {}

  protected void add(String queryPart) {
    queryString.append(queryPart);
  }

  protected void add(String queryPart, Map<String, String> map) {
    add(replace(queryPart, map));
  }

  protected void add(String queryPart, String values) {
    add(queryPart.replace(VALUES, values));
  }

  protected void add(String queryPart, String operator, String value) {
    add(queryPart.replace(OPERATOR, operator).replace(VALUE, value));
  }

  protected void add(String queryPart, String values, Map<String, String> map) {
    add(queryPart.replace(VALUES, values), map);
  }

  protected void add(String queryPart, String operator, String value, Map<String, String> map) {
    add(queryPart.replace(OPERATOR, operator).replace(VALUE, value), map);
  }

  public static String replace(String s, Map<String, String> map) {
    if (map == null) return s;
    for (String key : map.keySet())
      s = s.replace(OPEN_BRACKET + key + CLOSED_BRACKET, map.get(key));
    return s;
  }

  public String build() {
    return queryString.toString();
  }
}
