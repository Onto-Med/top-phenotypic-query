package care.smith.top.top_phenotypic_query.config;

import java.util.HashMap;
import java.util.Map;

public class PropertyQuery {

  private String type;
  private String allQuery;
  private String paramQueryBase;
  private String paramCode;
  private String paramValueQuantity;
  private String paramValueConcept;
  private String paramDate;
  private String paramSortInc;
  private String paramSortDec;
  private String paramPatientHasProp;
  private Map<String, String> output = new HashMap<>();

  private DataAdapterConfig conf;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

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

  public String getParamCode() {
    return paramCode;
  }

  public void setParamCode(String paramCode) {
    this.paramCode = paramCode;
  }

  public String getParamValueQuantity() {
    return paramValueQuantity;
  }

  public void setParamValueQuantity(String paramValueQuantity) {
    this.paramValueQuantity = paramValueQuantity;
  }

  public String getParamValueConcept() {
    return paramValueConcept;
  }

  public void setParamValueConcept(String paramValueConcept) {
    this.paramValueConcept = paramValueConcept;
  }

  public String getParamDate() {
    return paramDate;
  }

  public void setParamDate(String paramDate) {
    this.paramDate = paramDate;
  }

  public String getParamSortInc() {
    return paramSortInc;
  }

  public void setParamSortInc(String paramSortInc) {
    this.paramSortInc = paramSortInc;
  }

  public String getParamSortDec() {
    return paramSortDec;
  }

  public void setParamSortDec(String paramSortDec) {
    this.paramSortDec = paramSortDec;
  }

  public String getParamPatientHasProp() {
    return paramPatientHasProp;
  }

  public void setParamPatientHasProp(String paramPatientHasProp) {
    this.paramPatientHasProp = paramPatientHasProp;
  }

  public Map<String, String> getOutput() {
    return output;
  }

  public void setOutput(Map<String, String> output) {
    this.output = output;
  }

  public PropertyQueryBuilder getQueryBuilder() {
    return new PropertyQueryBuilder(conf, this);
  }

  protected PropertyQuery setDataAdapterConfig(DataAdapterConfig conf) {
    this.conf = conf;
    return this;
  }

  @Override
  public String toString() {
    return "PropertyQuery [type="
        + type
        + ", allQuery="
        + allQuery
        + ", paramQueryBase="
        + paramQueryBase
        + ", paramCode="
        + paramCode
        + ", paramValueQuantity="
        + paramValueQuantity
        + ", paramValueConcept="
        + paramValueConcept
        + ", paramDate="
        + paramDate
        + ", paramSortInc="
        + paramSortInc
        + ", paramSortDec="
        + paramSortDec
        + ", output="
        + output
        + "]";
  }
}
