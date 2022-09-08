package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class PhenotypeOutput {

  private String subject;
  private String phenotype;
  private String date;

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

  public String getPhenotype() {
    if (mapping == null) return phenotype;
    return QueryBuilder.replace(phenotype, mapping);
  }

  public void setPhenotype(String phenotype) {
    this.phenotype = phenotype;
  }

  public String getDate() {
    if (mapping == null) return date;
    return QueryBuilder.replace(date, mapping);
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
