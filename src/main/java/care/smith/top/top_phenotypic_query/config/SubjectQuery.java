package care.smith.top.top_phenotypic_query.config;

import java.util.Map;

public class SubjectQuery {

  private String baseQuery;
  private String baseQueryOnlyId;
  private String baseQueryOnlyCount;
  private String paramId;
  private String paramGender;
  private String paramBirthdate;
  private Map<String, String> output;

  private DataAdapterConfig conf;

  public String getBaseQuery() {
    return baseQuery;
  }

  public void setBaseQuery(String baseQuery) {
    this.baseQuery = baseQuery;
  }

  public String getBaseQueryOnlyId() {
    return baseQueryOnlyId;
  }

  public void setBaseQueryOnlyId(String baseQueryOnlyId) {
    this.baseQueryOnlyId = baseQueryOnlyId;
  }

  public String getBaseQueryOnlyCount() {
    return baseQueryOnlyCount;
  }

  public void setBaseQueryOnlyCount(String baseQueryOnlyCount) {
    this.baseQueryOnlyCount = baseQueryOnlyCount;
  }

  public String getParamId() {
    return paramId;
  }

  public void setParamId(String paramId) {
    this.paramId = paramId;
  }

  public String getParamGender() {
    return paramGender;
  }

  public void setParamGender(String paramGender) {
    this.paramGender = paramGender;
  }

  public String getParamBirthdate() {
    return paramBirthdate;
  }

  public void setParamBirthdate(String paramBirthdate) {
    this.paramBirthdate = paramBirthdate;
  }

  public Map<String, String> getOutput() {
    return output;
  }

  public void setOutput(Map<String, String> output) {
    this.output = output;
  }

  public SubjectQueryBuilder getQueryBuilder() {
    return new SubjectQueryBuilder(conf, this);
  }

  protected SubjectQuery setDataAdapterConfig(DataAdapterConfig conf) {
    this.conf = conf;
    return this;
  }

  @Override
  public String toString() {
    return "SubjectQuery [baseQuery="
        + baseQuery
        + ", baseQueryOnlyId="
        + baseQueryOnlyId
        + ", baseQueryOnlyCount="
        + baseQueryOnlyCount
        + ", paramId="
        + paramId
        + ", paramGender="
        + paramGender
        + ", paramBirthdate="
        + paramBirthdate
        + ", output="
        + output
        + "]";
  }
}
