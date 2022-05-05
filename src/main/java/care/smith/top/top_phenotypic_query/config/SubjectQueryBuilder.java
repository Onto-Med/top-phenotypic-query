package care.smith.top.top_phenotypic_query.config;

public class SubjectQueryBuilder extends QueryBuilder {

  private SubjectQuery query;

  public SubjectQueryBuilder(DataAdapterConfig conf, SubjectQuery query) {
    super(conf);
    this.query = query;
  }

  public SubjectQueryBuilder baseQuery() {
    add(query.getBaseQuery());
    return this;
  }

  public SubjectQueryBuilder baseQueryOnlyId() {
    add(query.getBaseQueryOnlyId());
    return this;
  }

  public SubjectQueryBuilder baseQueryOnlyCount() {
    add(query.getBaseQueryOnlyCount());
    return this;
  }

  public SubjectQueryBuilder id(String... ids) {
    add(query.getParamId().replace("{id}", getValuesAsString(ids)));
    return this;
  }

  public SubjectQueryBuilder gender(String... genders) {
    add(query.getParamGender().replace("{gender}", getValuesAsString(genders)));
    return this;
  }

  public SubjectQueryBuilder birthdateEQ(String birthdate) {
    add(replaceEQ(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public SubjectQueryBuilder birthdateGE(String birthdate) {
    add(replaceGE(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public SubjectQueryBuilder birthdateGT(String birthdate) {
    add(replaceGT(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public SubjectQueryBuilder birthdateLE(String birthdate) {
    add(replaceLE(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public SubjectQueryBuilder birthdateLT(String birthdate) {
    add(replaceLT(query.getParamBirthdate()).replace("{birthdate}", birthdate));
    return this;
  }

  public SubjectQueryBuilder hasProp(String queryType, String... props) {
    add(
        conf.getPropertyQuery(queryType)
            .getParamSubjectHasProp()
            .replace("{prop}", getValuesAsString(props)));
    return this;
  }
}
