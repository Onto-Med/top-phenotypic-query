package care.smith.top.top_phenotypic_query.tests.default_sql_writer;

import care.smith.top.model.Code;
import care.smith.top.model.Phenotype;
import java.util.Random;

public class PheDao {
  private String phenotypeId;
  private String codeSystem;
  private String code;
  private String date;
  private String startDate;
  private String endDate;
  private String unit;
  private Number numberValue;
  private String textValue;
  private String dateValue;
  private Boolean booleanValue;

  public static PheDao get(Phenotype phe, String date, Number numberValue) {
    return new PheDao().phenotype(phe).date(date).numberValue(numberValue);
  }

  public PheDao phenotype(Phenotype phe) {
    Code code = phe.getCodes().get(new Random().nextInt(phe.getCodes().size()));
    code(code.getCode());
    codeSystem(code.getCodeSystem().getUri().toString());
    return unit(phe.getUnit());
  }

  public String getPhenotypeId() {
    return phenotypeId;
  }

  public PheDao phenotypeId(String phenotypeId) {
    this.phenotypeId = phenotypeId;
    return this;
  }

  public String getCodeSystem() {
    return codeSystem;
  }

  public PheDao codeSystem(String codeSystem) {
    this.codeSystem = codeSystem;
    return this;
  }

  public String getCode() {
    return code;
  }

  public PheDao code(String code) {
    this.code = code;
    return this;
  }

  public String getDate() {
    return date;
  }

  public PheDao date(String date) {
    this.date = date;
    return this;
  }

  public String getStartDate() {
    return startDate;
  }

  public PheDao startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  public String getEndDate() {
    return endDate;
  }

  public PheDao endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  public String getUnit() {
    return unit;
  }

  public PheDao unit(String unit) {
    this.unit = unit;
    return this;
  }

  public Number getNumberValue() {
    return numberValue;
  }

  public PheDao numberValue(Number numberValue) {
    this.numberValue = numberValue;
    return this;
  }

  public String getTextValue() {
    return textValue;
  }

  public PheDao textValue(String textValue) {
    this.textValue = textValue;
    return this;
  }

  public String getDateValue() {
    return dateValue;
  }

  public PheDao dateValue(String dateValue) {
    this.dateValue = dateValue;
    return this;
  }

  public Boolean getBooleanValue() {
    return booleanValue;
  }

  public PheDao booleanValue(Boolean booleanValue) {
    this.booleanValue = booleanValue;
    return this;
  }
}
