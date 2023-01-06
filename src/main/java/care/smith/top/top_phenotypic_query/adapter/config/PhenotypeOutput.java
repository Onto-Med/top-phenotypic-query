package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class PhenotypeOutput {

  private String subject;
  private String dateTime;
  private String value;
  private String textValue;
  private String numberValue;
  private String dateTimeValue;
  private String booleanValue;
  private String conceptValue;

  private Map<String, String> mapping;

  public PhenotypeOutput mapping(Map<String, String> mapping) {
    this.mapping = mapping;
    return this;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getDateTime() {
    if (mapping == null) return dateTime;
    return QueryBuilder.replace(dateTime, mapping);
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public String getValue() {
    if (value == null) return null;
    if (mapping == null) return value;
    return QueryBuilder.replace(value, mapping);
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getTextValue() {
    if (mapping == null) return textValue;
    return QueryBuilder.replace(textValue, mapping);
  }

  public void setTextValue(String textValue) {
    this.textValue = textValue;
  }

  public String getNumberValue() {
    if (mapping == null) return numberValue;
    return QueryBuilder.replace(numberValue, mapping);
  }

  public void setNumberValue(String numberValue) {
    this.numberValue = numberValue;
  }

  public String getDateTimeValue() {
    if (mapping == null) return dateTimeValue;
    return QueryBuilder.replace(dateTimeValue, mapping);
  }

  public void setDateTimeValue(String dateTimeValue) {
    this.dateTimeValue = dateTimeValue;
  }

  public String getBooleanValue() {
    if (mapping == null) return booleanValue;
    return QueryBuilder.replace(booleanValue, mapping);
  }

  public void setBooleanValue(String booleanValue) {
    this.booleanValue = booleanValue;
  }

  public String getConceptValue() {
    if (mapping == null) return conceptValue;
    return QueryBuilder.replace(conceptValue, mapping);
  }

  public void setConceptValue(String conceptValue) {
    this.conceptValue = conceptValue;
  }

  @Override
  public String toString() {
    return "PhenotypeOutput [subject="
        + subject
        + ", dateTime="
        + dateTime
        + ", value="
        + value
        + ", textValue="
        + textValue
        + ", numberValue="
        + numberValue
        + ", dateTimeValue="
        + dateTimeValue
        + ", booleanValue="
        + booleanValue
        + ", conceptValue="
        + conceptValue
        + ", mapping="
        + mapping
        + "]";
  }
}
