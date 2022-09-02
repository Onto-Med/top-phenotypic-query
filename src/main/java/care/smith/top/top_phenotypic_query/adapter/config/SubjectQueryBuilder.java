package care.smith.top.top_phenotypic_query.adapter.config;

public class SubjectQueryBuilder extends QueryBuilder {

  private SubjectQuery query;

  public SubjectQueryBuilder(SubjectQuery query) {
    this.query = query;
  }

  public SubjectQueryBuilder baseQuery() {
    add(query.getBaseQuery());
    return this;
  }

  public SubjectQueryBuilder sexRange(String sexes) {
    add(query.getSexRangePart().replace("{sexes}", sexes));
    return this;
  }

  public SubjectQueryBuilder birthdateRangeLimit(String operator, String birthdate) {
    add(
        query
            .getBirthdateRangePart()
            .replace("{operator}", operator)
            .replace("{value}", birthdate));
    return this;
  }
}
