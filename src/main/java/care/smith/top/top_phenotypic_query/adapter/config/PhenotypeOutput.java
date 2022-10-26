package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class PhenotypeOutput {

  private String subject;
  private String phenotype;
  private String stringPhenotype;
  private String numberPhenotype;
  private String conceptPhenotype;
  private String datePhenotype;
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
    if (phenotype == null) return null;
    if (mapping == null) return phenotype;
    return QueryBuilder.replace(phenotype, mapping);
  }

  public void setPhenotype(String phenotype) {
    this.phenotype = phenotype;
  }

  public String getStringPhenotype() {
    if (mapping == null) return stringPhenotype;
    return QueryBuilder.replace(stringPhenotype, mapping);
  }

  public void setStringPhenotype(String stringPhenotype) {
    this.stringPhenotype = stringPhenotype;
  }

  public String getNumberPhenotype() {
    if (mapping == null) return numberPhenotype;
    return QueryBuilder.replace(numberPhenotype, mapping);
  }

  public void setNumberPhenotype(String numberPhenotype) {
    this.numberPhenotype = numberPhenotype;
  }

  public String getConceptPhenotype() {
    if (mapping == null) return conceptPhenotype;
    return QueryBuilder.replace(conceptPhenotype, mapping);
  }

  public void setConceptPhenotype(String conceptPhenotype) {
    this.conceptPhenotype = conceptPhenotype;
  }

  public String getDatePhenotype() {
    if (mapping == null) return datePhenotype;
    return QueryBuilder.replace(datePhenotype, mapping);
  }

  public void setDatePhenotype(String datePhenotype) {
    this.datePhenotype = datePhenotype;
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
