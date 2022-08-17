package care.smith.top.top_phenotypic_query.result;

import java.util.Collection;
import java.util.HashMap;

public class SubjectPhenotypes extends HashMap<String, Phenotype> {

  private static final long serialVersionUID = 1L;
  private String subjectId;

  public SubjectPhenotypes(String subjectId) {
    this.subjectId = subjectId;
  }

  public void addPhenotype(Phenotype phenotype) {
    put(phenotype.getName(), phenotype);
  }

  public Phenotype getPhenotype(String phenotypeName) {
    return get(phenotypeName);
  }

  public Collection<Phenotype> getPhenotypes() {
    return values();
  }

  public String getSubjectId() {
    return subjectId;
  }
}
