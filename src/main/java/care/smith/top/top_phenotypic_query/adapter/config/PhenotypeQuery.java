package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class PhenotypeQuery {

  private String baseQuery;
  private String valueRangePart;
  private String dateRangePart;
  private String subjectsPart;
  private PhenotypeOutput output;

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
    return "PhenotypeQuery [baseQuery="
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
