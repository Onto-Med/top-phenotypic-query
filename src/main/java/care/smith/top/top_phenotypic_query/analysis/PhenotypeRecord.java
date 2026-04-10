package care.smith.top.top_phenotypic_query.analysis;

import care.smith.top.top_phenotypic_query.util.DateUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;

public class PhenotypeRecord {
  private String subject;
  private String phenotype;
  private String title;
  private LocalDateTime dateTime;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private BigDecimal numberValue;
  private String stringValue;
  private LocalDateTime dateTimeValue;
  private Boolean booleanValue;

  public PhenotypeRecord(Map<String, String> r) {
    setSubject(r.get("subject"));
    setPhenotype(r.get("phenotype"));
    setTitle(r.get("title"));
    setDateTime(r.get("date_time"));
    setStartDateTime(r.get("start_date_time"));
    setEndDateTime(r.get("end_date_time"));
    setNumberValue(r.get("number_value"));
    setStringValue(r.get("string_value"));
    setDateTimeValue(r.get("date_time_value"));
    setBooleanValue(r.get("boolean_value"));
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    if (!Strings.isBlank(subject)) this.subject = subject;
  }

  public String getPhenotype() {
    return phenotype;
  }

  public void setPhenotype(String phenotype) {
    if (!Strings.isBlank(phenotype)) this.phenotype = phenotype;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    if (!Strings.isBlank(title)) this.title = title;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    if (!Strings.isBlank(dateTime)) this.dateTime = DateUtil.parse(dateTime);
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(String startDateTime) {
    if (!Strings.isBlank(startDateTime)) this.startDateTime = DateUtil.parse(startDateTime);
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public void setEndDateTime(String endDateTime) {
    if (!Strings.isBlank(endDateTime)) this.endDateTime = DateUtil.parse(endDateTime);
  }

  public BigDecimal getNumberValue() {
    return numberValue;
  }

  public void setNumberValue(String numberValue) {
    if (!Strings.isBlank(numberValue)) this.numberValue = new BigDecimal(numberValue);
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    if (!Strings.isBlank(stringValue)) this.stringValue = stringValue;
  }

  public LocalDateTime getDateTimeValue() {
    return dateTimeValue;
  }

  public void setDateTimeValue(String dateTimeValue) {
    if (!Strings.isBlank(dateTimeValue)) this.dateTimeValue = DateUtil.parse(dateTimeValue);
  }

  public Boolean getBooleanValue() {
    return booleanValue;
  }

  public void setBooleanValue(String booleanValue) {
    if (!Strings.isBlank(booleanValue)) this.booleanValue = Boolean.valueOf(booleanValue);
  }

  @Override
  public String toString() {
    StringBuffer sb =
        new StringBuffer(subject)
            .append(":")
            .append(phenotype)
            .append(":")
            .append(title)
            .append("|");

    if (dateTime != null) sb.append("dateTime=").append(DateUtil.format(dateTime));
    if (startDateTime != null) sb.append("startDateTime=").append(DateUtil.format(startDateTime));
    if (endDateTime != null)
      sb.append(":").append("endDateTime=").append(DateUtil.format(endDateTime));
    sb.append("|");

    if (numberValue != null) sb.append(numberValue);
    if (stringValue != null) sb.append(stringValue);
    if (dateTimeValue != null) sb.append(DateUtil.format(dateTimeValue));
    if (booleanValue != null) sb.append(booleanValue);

    return sb.toString();
  }
}
