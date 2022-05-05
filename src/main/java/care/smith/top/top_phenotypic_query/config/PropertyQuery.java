package care.smith.top.top_phenotypic_query.config;

import java.util.HashMap;
import java.util.Map;

public class PropertyQuery {

  private String type;
  private String baseQuery;
  private String baseQueryOnlyCount;
  private String baseQueryOnlySubject;
  private String paramId;
  private String paramSubject;
  private String paramDate;
  private String paramValueQuantity;
  private String paramValueConcept;
  private String paramValueText;
  private String paramFirstRecord;
  private String paramLastRecord;
  private String paramSubjectHasProp;
  private Map<String, String> output = new HashMap<>();

  private DataAdapterConfig conf;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getBaseQuery() {
    return baseQuery;
  }

  public void setBaseQuery(String baseQuery) {
    this.baseQuery = baseQuery;
  }

  public String getBaseQueryOnlyCount() {
    return baseQueryOnlyCount;
  }

  public void setBaseQueryOnlyCount(String baseQueryOnlyCount) {
    this.baseQueryOnlyCount = baseQueryOnlyCount;
  }

  public String getBaseQueryOnlySubject() {
    return baseQueryOnlySubject;
  }

  public void setBaseQueryOnlySubject(String baseQueryOnlySubject) {
    this.baseQueryOnlySubject = baseQueryOnlySubject;
  }

  public String getParamId() {
    return paramId;
  }

  public void setParamId(String paramId) {
    this.paramId = paramId;
  }

  public String getParamSubject() {
    return paramSubject;
  }

  public void setParamSubject(String paramSubject) {
    this.paramSubject = paramSubject;
  }

  public String getParamDate() {
    return paramDate;
  }

  public void setParamDate(String paramDate) {
    this.paramDate = paramDate;
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

  public String getParamValueText() {
    return paramValueText;
  }

  public void setParamValueText(String paramValueText) {
    this.paramValueText = paramValueText;
  }

  public String getParamFirstRecord() {
    return paramFirstRecord;
  }

  public void setParamFirstRecord(String paramFirstRecord) {
    this.paramFirstRecord = paramFirstRecord;
  }

  public String getParamLastRecord() {
    return paramLastRecord;
  }

  public void setParamLastRecord(String paramLastRecord) {
    this.paramLastRecord = paramLastRecord;
  }

  public String getParamSubjectHasProp() {
    return paramSubjectHasProp;
  }

  public void setParamSubjectHasProp(String paramSubjectHasProp) {
    this.paramSubjectHasProp = paramSubjectHasProp;
  }

  public Map<String, String> getOutput() {
    return output;
  }

  public void setOutput(Map<String, String> output) {
    this.output = output;
  }

  public PropertyQueryBuilder getQueryBuilder(String... props) {
    return new PropertyQueryBuilder(conf, this, props);
  }

  protected PropertyQuery setDataAdapterConfig(DataAdapterConfig conf) {
    this.conf = conf;
    return this;
  }
}
