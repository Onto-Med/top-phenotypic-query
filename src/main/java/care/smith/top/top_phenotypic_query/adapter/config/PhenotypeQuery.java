package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class PhenotypeQuery {

  private String baseQuery;
  private String valueIntervalPart;
  private String valueListPart;
  private String dateValueIntervalPart;
  private String dateIntervalPart;
  private String subjectsPart;
  private PhenotypeOutput output;

  public String getBaseQuery() {
    return baseQuery;
  }

  public void setBaseQuery(String baseQuery) {
    this.baseQuery = baseQuery;
  }

  public String getValueIntervalPart() {
    return valueIntervalPart;
  }

  public void setValueIntervalPart(String valueIntervalPart) {
    this.valueIntervalPart = valueIntervalPart;
  }

  public String getValueListPart() {
    return valueListPart;
  }

  public void setValueListPart(String valueListPart) {
    this.valueListPart = valueListPart;
  }

  public String getDateValueIntervalPart() {
    return dateValueIntervalPart;
  }

  public void setDateValueIntervalPart(String dateValueIntervalPart) {
    this.dateValueIntervalPart = dateValueIntervalPart;
  }

  public String getDateIntervalPart() {
    return dateIntervalPart;
  }

  public void setDateIntervalPart(String dateIntervalPart) {
    this.dateIntervalPart = dateIntervalPart;
  }

  public String getSubjectsPart() {
    return subjectsPart;
  }

  public void setSubjectsPart(String subjectsPart) {
    this.subjectsPart = subjectsPart;
  }

  public PhenotypeOutput getOutput() {
    return output;
  }

  public void setOutput(PhenotypeOutput output) {
    this.output = output;
  }

  public PhenotypeQueryBuilder getQueryBuilder(Map<String, String> mappings) {
    return new PhenotypeQueryBuilder(this, mappings);
  }

  @Override
  public String toString() {
    return "PhenotypeQuery [baseQuery="
        + baseQuery
        + ", valueIntervalPart="
        + valueIntervalPart
        + ", valueListPart="
        + valueListPart
        + ", dateValueIntervalPart="
        + dateValueIntervalPart
        + ", dateIntervalPart="
        + dateIntervalPart
        + ", subjectsPart="
        + subjectsPart
        + ", output="
        + output
        + "]";
  }
}
