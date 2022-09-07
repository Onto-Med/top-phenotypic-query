package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class PhenotypeOutput {

  private String subject;
  private String phenotype;
  private String date;

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getPhenotype(Map<String, String> map) {
    return QueryBuilder.replace(phenotype, map);
  }

  public void setPhenotype(String phenotype) {
    this.phenotype = phenotype;
  }

  public String getDate(Map<String, String> map) {
    return QueryBuilder.replace(date, map);
  }

  public void setDate(String date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "PhenotypeOutput [subject="
        + subject
        + ", phenotype="
        + phenotype
        + ", date="
        + date
        + "]";
  }
}
