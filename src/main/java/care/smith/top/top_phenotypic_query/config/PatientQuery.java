package care.smith.top.top_phenotypic_query.config;

import java.util.Map;

public class PatientQuery {

  private String allQuery;
  private String paramQueryBase;
  private String paramGender;
  private String paramBirthdate;
  private Map<String, String> output;

  private DataAdapterConfig conf;

  public String getAllQuery() {
    return allQuery;
  }

  public void setAllQuery(String allQuery) {
    this.allQuery = allQuery;
  }

  public String getParamQueryBase() {
    return paramQueryBase;
  }

  public void setParamQueryBase(String paramQueryBase) {
    this.paramQueryBase = paramQueryBase;
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

  public PatientQueryBuilder getQueryBuilder() {
    return new PatientQueryBuilder(conf, this);
  }

  protected PatientQuery setDataAdapterConfig(DataAdapterConfig conf) {
    this.conf = conf;
    return this;
  }

  @Override
  public String toString() {
    return "PatientQuery [allQuery="
        + allQuery
        + ", paramQueryBase="
        + paramQueryBase
        + ", paramGender="
        + paramGender
        + ", paramBirthdate="
        + paramBirthdate
        + ", output="
        + output
        + "]";
  }
}
