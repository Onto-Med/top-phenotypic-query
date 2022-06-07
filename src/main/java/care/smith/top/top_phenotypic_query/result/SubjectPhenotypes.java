package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;

public class SubjectPhenotypes extends HashMap<String, Phenotype> {

  private static final long serialVersionUID = 1L;
  private String subject;

  public SubjectPhenotypes(String subject) {
    this.subject = subject;
  }

  public void addPhenotype(Phenotype phenotype) {
    put(phenotype.getName(), phenotype);
  }

  public Phenotype getPhenotype(String name) {
    return get(name);
  }

  public String getSubject() {
    return subject;
  }
}
