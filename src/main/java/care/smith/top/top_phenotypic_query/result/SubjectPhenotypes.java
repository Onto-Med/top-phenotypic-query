package care.smith.top.top_phenotypic_query.result;

import java.util.Collection;
import java.util.HashMap;

public class SubjectPhenotypes extends HashMap<String, PhenotypeValues> {

  private static final long serialVersionUID = 1L;
  private String subjectId;

  public SubjectPhenotypes(String subjectId) {
    this.subjectId = subjectId;
  }

  public void addPhenotype(PhenotypeValues phenotype) {
    put(phenotype.getPhenotypeName(), phenotype);
  }

  public PhenotypeValues getPhenotype(String phenotypeName) {
    return get(phenotypeName);
  }

  public Collection<PhenotypeValues> getPhenotypes() {
    return values();
  }

  public String getSubjectId() {
    return subjectId;
  }
}
