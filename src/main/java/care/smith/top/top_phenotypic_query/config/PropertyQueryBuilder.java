package care.smith.top.top_phenotypic_query.config;

public class PropertyQueryBuilder extends QueryBuilder {

  private PropertyQuery query;
  private String prop;

  public PropertyQueryBuilder(DataAdapterConfig conf, PropertyQuery query, String... props) {
    super(conf);
    this.query = query;
    this.prop = getValuesAsString(props);
  }

  public PropertyQueryBuilder baseQuery() {
    add(query.getBaseQuery().replace("{prop}", prop));
    return this;
  }

  public PropertyQueryBuilder baseQueryOnlyCount() {
    add(query.getBaseQueryOnlyCount().replace("{prop}", prop));
    return this;
  }

  public PropertyQueryBuilder baseQueryOnlySubject() {
    add(query.getBaseQueryOnlySubject().replace("{prop}", prop));
    return this;
  }

  public PropertyQueryBuilder id(String... ids) {
    add(query.getParamId().replace("{id}", getValuesAsString(ids)));
    return this;
  }

  public PropertyQueryBuilder subject(String... subjects) {
    add(query.getParamSubject().replace("{subject}", getValuesAsString(subjects)));
    return this;
  }

  public PropertyQueryBuilder dateEQ(String date) {
    add(replaceEQ(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder dateGE(String date) {
    add(replaceGE(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder dateGT(String date) {
    add(replaceGT(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder dateLE(String date) {
    add(replaceLE(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder dateLT(String date) {
    add(replaceLT(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder firstRecord() {
    add(query.getParamFirstRecord());
    return this;
  }

  public PropertyQueryBuilder lastRecord() {
    add(query.getParamLastRecord());
    return this;
  }

  public PropertyQueryBuilder valueQuantityEQ(String value) {
    add(replaceEQ(query.getParamValueQuantity()).replace("{prop}", prop).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueQuantityGE(String value) {
    add(replaceGE(query.getParamValueQuantity()).replace("{prop}", prop).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueQuantityGT(String value) {
    add(replaceGT(query.getParamValueQuantity()).replace("{prop}", prop).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueQuantityLE(String value) {
    add(replaceLE(query.getParamValueQuantity()).replace("{prop}", prop).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueQuantityLT(String value) {
    add(replaceLT(query.getParamValueQuantity()).replace("{prop}", prop).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueConcept(String... values) {
    add(
        query
            .getParamValueConcept()
            .replace("{prop}", prop)
            .replace("{value}", getValuesAsString(values)));
    return this;
  }

  public PropertyQueryBuilder valueText(String... values) {
    add(
        query
            .getParamValueText()
            .replace("{prop}", prop)
            .replace("{value}", getValuesAsString(values)));
    return this;
  }
}
