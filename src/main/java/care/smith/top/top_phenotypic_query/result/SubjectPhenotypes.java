package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.Set;

public class SubjectPhenotypes extends HashMap<String, PhenotypeValues> {

  private static final long serialVersionUID = 1L;
  private String subjectId;

  public SubjectPhenotypes(String subjectId) {
    this.subjectId = subjectId;
  }

  public void addPhenotype(PhenotypeValues phenotype) {
    put(phenotype.getPhenotypeName(), phenotype);
  }

  public void addPhenotypes(PhenotypeValues... phenotypes) {
    for (PhenotypeValues phenotype : phenotypes) addPhenotype(phenotype);
  }

  public PhenotypeValues getPhenotype(String phenotypeName) {
    return get(phenotypeName);
  }

  public Set<String> getPhenotypeNames() {
    return keySet();
  }

  public boolean hasPhenotype(String phenotypeName) {
    return containsKey(phenotypeName);
  }

  public String getSubjectId() {
    return subjectId;
  }
}
