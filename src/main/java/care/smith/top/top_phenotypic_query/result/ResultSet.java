package care.smith.top.top_phenotypic_query.result;

import java.util.Collection;
import java.util.HashMap;

public class ResultSet extends HashMap<String, SubjectPhenotypes> {

  private static final long serialVersionUID = 1L;

  public void setPhenotypes(SubjectPhenotypes phenotypes) {
    put(phenotypes.getSubjectId(), phenotypes);
  }

  public void addPhenotype(String subjectId, Phenotype phenotype) {
    SubjectPhenotypes subPhens = get(subjectId);
    if (subPhens == null) {
      subPhens = new SubjectPhenotypes(subjectId);
      put(subjectId, subPhens);
    }
    subPhens.addPhenotype(phenotype);
  }

  public Collection<SubjectPhenotypes> getPhenotypes() {
    return values();
  }

  public SubjectPhenotypes getPhenotypes(String subjectId) {
    return get(subjectId);
  }

  public Phenotype getPhenotype(String subjectId, String phenotypeName) {
    SubjectPhenotypes subPhens = get(subjectId);
    if (subPhens == null) return null;
    return subPhens.getPhenotype(phenotypeName);
  }
}
