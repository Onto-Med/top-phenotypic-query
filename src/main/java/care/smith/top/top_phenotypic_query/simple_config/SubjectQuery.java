package care.smith.top.top_phenotypic_query.simple_config;

public class SubjectQuery {

  private String baseQuery;
  private String sexRangePart;
  private String birthdateRangePart;
  private SubjectOutput output;

  public String getBaseQuery() {
    return baseQuery;
  }

  public void setBaseQuery(String baseQuery) {
    this.baseQuery = baseQuery;
  }

  public String getSexRangePart() {
    return sexRangePart;
  }

  public void setSexRangePart(String sexRangePart) {
    this.sexRangePart = sexRangePart;
  }

  public String getBirthdateRangePart() {
    return birthdateRangePart;
  }

  public void setBirthdateRangePart(String birthdateRangePart) {
    this.birthdateRangePart = birthdateRangePart;
  }

  public SubjectOutput getOutput() {
    return output;
  }

  public void setOutput(SubjectOutput output) {
    this.output = output;
  }

  public SubjectQueryBuilder getQueryBuilder() {
    return new SubjectQueryBuilder(this);
  }

  @Override
  public String toString() {
    return "SubjectQuery [baseQuery="
        + baseQuery
        + ", sexRangePart="
        + sexRangePart
        + ", birthdateRangePart="
        + birthdateRangePart
        + ", output="
        + output
        + "]";
  }
}
