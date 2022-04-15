package care.smith.top.top_phenotypic_query.config;

public class PropertyQueryBuilder extends QueryBuilder {

  private PropertyQuery query;

  public PropertyQueryBuilder(DataAdapterConfig conf, PropertyQuery query) {
    super(conf);
    this.query = query;
  }

  public PropertyQueryBuilder onlyId() {
    addParamOnlyId();
    return this;
  }

  public PropertyQueryBuilder onlyCount() {
    addParamOnlyCount();
    return this;
  }

  public PropertyQueryBuilder onlySubject() {
    addParamOnlySubject();
    return this;
  }

  public PropertyQueryBuilder limit(int limit) {
    addParamLimit(limit);
    return this;
  }

  public PropertyQueryBuilder id(String... ids) {
    addParamId(ids);
    return this;
  }

  public PropertyQueryBuilder subject(String... subjects) {
    addParamSubject(subjects);
    return this;
  }

  public PropertyQueryBuilder code(String... codes) {
    addParam(query.getParamCode().replace("{code}", getValuesAsString(codes)));
    return this;
  }

  public PropertyQueryBuilder valueQuantityEQ(String value) {
    addParam(replaceEQ(query.getParamValueQuantity()).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueQuantityGE(String value) {
    addParam(replaceGE(query.getParamValueQuantity()).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueQuantityGT(String value) {
    addParam(replaceGT(query.getParamValueQuantity()).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueQuantityLE(String value) {
    addParam(replaceLE(query.getParamValueQuantity()).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueQuantityLT(String value) {
    addParam(replaceLT(query.getParamValueQuantity()).replace("{value}", value));
    return this;
  }

  public PropertyQueryBuilder valueConcept(String... values) {
    addParam(query.getParamValueConcept().replace("{value}", getValuesAsString(values)));
    return this;
  }

  public PropertyQueryBuilder dateEQ(String date) {
    addParam(replaceEQ(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder dateGE(String date) {
    addParam(replaceGE(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder dateGT(String date) {
    addParam(replaceGT(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder dateLE(String date) {
    addParam(replaceLE(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder dateLT(String date) {
    addParam(replaceLT(query.getParamDate()).replace("{date}", date));
    return this;
  }

  public PropertyQueryBuilder sortInc() {
    addParam(query.getParamSortInc());
    return this;
  }

  public PropertyQueryBuilder sortDec() {
    addParam(query.getParamSortDec());
    return this;
  }

  public String build() {
    return build(query.getParamQueryBase());
  }
}
