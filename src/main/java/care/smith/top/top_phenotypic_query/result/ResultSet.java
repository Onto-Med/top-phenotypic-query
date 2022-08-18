package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.Set;

import care.smith.top.backend.model.DateTimeRestriction;

public class ResultSet extends HashMap<String, SubjectPhenotypes> {

  private static final long serialVersionUID = 1L;

  public ResultSet() {}

  private ResultSet(ResultSet rs2) {
    super(rs2);
  }

  public void setPhenotypes(SubjectPhenotypes phenotypes) {
    put(phenotypes.getSubjectId(), phenotypes);
  }

  public void setPhenotypes(SubjectPhenotypes... phenotypes) {
    for (SubjectPhenotypes p : phenotypes) setPhenotypes(p);
  }

  public void addPhenotype(String subjectId, PhenotypeValues phenotype) {
    SubjectPhenotypes subPhens = get(subjectId);
    if (subPhens == null) {
      subPhens = new SubjectPhenotypes(subjectId);
      put(subjectId, subPhens);
    }
    subPhens.addPhenotype(phenotype);
  }

  public Set<String> getSubjectIds() {
    return keySet();
  }

  public SubjectPhenotypes getPhenotypes(String subjectId) {
    return get(subjectId);
  }

  public PhenotypeValues getPhenotype(String subjectId, String phenotypeName) {
    SubjectPhenotypes subPhens = get(subjectId);
    if (subPhens == null) return null;
    return subPhens.getPhenotype(phenotypeName);
  }

  public ResultSet intersect(ResultSet rs2) {
    ResultSet intersection = new ResultSet(this);
    intersection.keySet().retainAll(rs2.keySet());

    for (String sbjId : intersection.getSubjectIds()) {
      SubjectPhenotypes phenotypes1 = intersection.getPhenotypes(sbjId);
      SubjectPhenotypes phenotypes2 = rs2.getPhenotypes(sbjId);
      for (String pheName : phenotypes2.getPhenotypeNames()) {
        if (!phenotypes1.hasPhenotype(pheName))
          phenotypes1.addPhenotype(phenotypes2.getPhenotype(pheName));
        else {
          PhenotypeValues values1 = phenotypes1.getPhenotype(pheName);
          PhenotypeValues values2 = phenotypes2.getPhenotype(pheName);
          for (DateTimeRestriction dateRange : values2.getDateRanges()) {
            if (!values1.hasValues(dateRange))
              values1.addValues(dateRange, values2.getValues(dateRange));
          }
        }
      }
    }

    return intersection;
  }

  public ResultSet subtract(ResultSet rs2) {
    ResultSet difference = new ResultSet(this);
    difference.keySet().removeAll(rs2.keySet());
    return difference;
  }
}
