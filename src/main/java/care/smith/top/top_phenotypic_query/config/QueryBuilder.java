package care.smith.top.top_phenotypic_query.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueryBuilder {

  protected DataAdapterConfig conf;
  private List<String> params = new ArrayList<>();

  protected QueryBuilder(DataAdapterConfig conf) {
    this.conf = conf;
  }

  protected void addParam(String param) {
    params.add(param);
  }

  protected void addParamOnlyId() {
    addParam(conf.getParamOnlyId());
  }

  protected void addParamOnlyCount() {
    addParam(conf.getParamOnlyCount());
  }

  protected void addParamOnlySubject() {
    addParam(conf.getParamOnlySubject());
  }

  protected void addParamLimit(int limit) {
    addParam(conf.getParamLimit().replace("{limit}", Integer.valueOf(limit).toString()));
  }

  protected void addParamId(String... ids) {
    addParam(conf.getParamId().replace("{id}", getValuesAsString(ids)));
  }

  protected void addParamSubject(String... subjects) {
    addParam(conf.getParamSubject().replace("{subject}", getValuesAsString(subjects)));
  }

  protected String build(String paramQueryBase) {
    return paramQueryBase + String.join(conf.getParamSeparator(), params);
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
