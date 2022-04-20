package care.smith.top.top_phenotypic_query.config;

public class PatientQueryBuilder extends QueryBuilder {

  private PatientQuery query;

  public PatientQueryBuilder(DataAdapterConfig conf, PatientQuery query) {
    super(conf);
    this.query = query;
  }

  public PatientQueryBuilder onlyId() {
    addParamOnlyId();
    return this;
  }

  public PatientQueryBuilder onlyCount() {
    addParamOnlyCount();
    return this;
  }

  public PatientQueryBuilder onlySubject() {
    addParamOnlySubject();
    return this;
  }

  public PatientQueryBuilder limit(int limit) {
    addParamLimit(limit);
    return this;
  }

  public PatientQueryBuilder id(String... ids) {
    addParamId(ids);
    return this;
  }

  public PatientQueryBuilder subject(String... subjects) {
    addParamSubject(subjects);
    return this;
  }

  public PatientQueryBuilder gender(String... genders) {
    addParam(query.getParamGender().replace("{gender}", getValuesAsString(genders)));
    return this;
  }

  public PatientQueryBuilder birthdateEQ(String birthdate) {
    addParam(replaceEQ(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public PatientQueryBuilder birthdateGE(String birthdate) {
    addParam(replaceGE(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public PatientQueryBuilder birthdateGT(String birthdate) {
    addParam(replaceGT(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public PatientQueryBuilder birthdateLE(String birthdate) {
    addParam(replaceLE(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public PatientQueryBuilder birthdateLT(String birthdate) {
    addParam(replaceLT(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public PatientQueryBuilder hasProp(String queryType, String... codes) {
    addParam(
        conf.getPropertyQuery(queryType)
            .getParamPatientHasProp()
            .replace("{code}", getValuesAsString(codes)));
    return this;
  }

  public String build() {
    return build(query.getParamQueryBase());
  }
}
