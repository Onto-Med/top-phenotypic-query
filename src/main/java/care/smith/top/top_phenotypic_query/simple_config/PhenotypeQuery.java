package care.smith.top.top_phenotypic_query.simple_config;

import java.util.Map;

public class PhenotypeQuery {

  private String type;
  private String baseQuery;
  private String valueRangePart;
  private String dateRangePart;
  private String subjectsPart;
  private PhenotypeOutput output;

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

  public String getValueRangePart() {
    return valueRangePart;
  }

  public void setValueRangePart(String valueRangePart) {
    this.valueRangePart = valueRangePart;
  }

  public String getDateRangePart() {
    return dateRangePart;
  }

  public void setDateRangePart(String dateRangePart) {
    this.dateRangePart = dateRangePart;
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
    return "PhenotypeQuery [type="
        + type
        + ", baseQuery="
        + baseQuery
        + ", valueRangePart="
        + valueRangePart
        + ", dateRangePart="
        + dateRangePart
        + ", subjectsPart="
        + subjectsPart
        + ", output="
        + output
        + "]";
  }
}
