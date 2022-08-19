package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.Set;

import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;

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

  public ValueList getValues(String phenotypeName, DateTimeRestriction dateRange) {
    PhenotypeValues values = getPhenotype(phenotypeName);
    if (values == null) return null;
    return values.getValues(dateRange);
  }
}
