package care.smith.top.top_phenotypic_query.simple_config;

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

  public String getPhenotype() {
    return phenotype;
  }

  public void setPhenotype(String phenotype) {
    this.phenotype = phenotype;
  }

  public String getDate() {
    return date;
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
