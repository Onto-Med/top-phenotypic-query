package care.smith.top.top_phenotypic_query.config;

import java.util.Collection;

public class QueryBuilder {

  protected DataAdapterConfig conf;
  private StringBuffer queryString = new StringBuffer();

  protected QueryBuilder(DataAdapterConfig conf) {
    this.conf = conf;
  }

  protected void add(String queryPart) {
    queryString.append(queryPart);
  }

  public String build() {
    return queryString.toString();
  }

  protected String getValuesAsString(String... values) {
    return String.join(conf.getValueSeparator(), values);
  }

  protected String getValuesAsString(Collection<String> values) {
    return String.join(conf.getValueSeparator(), values);
  }

  protected String replaceEQ(String query) {
    return query.replace("{operator}", conf.getOperEQ());
  }

  protected String replaceGE(String query) {
    return query.replace("{operator}", conf.getOperGE());
  }

  protected String replaceGT(String query) {
    return query.replace("{operator}", conf.getOperGT());
  }

  protected String replaceLE(String query) {
    return query.replace("{operator}", conf.getOperLE());
  }

  protected String replaceLT(String query) {
    return query.replace("{operator}", conf.getOperLT());
  }
}
