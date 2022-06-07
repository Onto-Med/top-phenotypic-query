package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.Map;

public class ResultSet {

  private Map<String, SubjectPhenotypes> rs = new HashMap<>();

  public void setPhenotypes(String subject, SubjectPhenotypes phenotypes) {
    rs.put(subject, phenotypes);
  }

  public void addPhenotype(String subject, Phenotype phenotype) {
    SubjectPhenotypes subPhens = rs.get(subject);
    if (subPhens == null) {
      subPhens = new SubjectPhenotypes(subject);
      rs.put(subject, subPhens);
    }
    subPhens.addPhenotype(phenotype);
  }

  public SubjectPhenotypes getPhenotypes(String subject) {
    return rs.get(subject);
  }

  public Phenotype getPhenotype(String subject, String phenotype) {
    SubjectPhenotypes subPhens = rs.get(subject);
    if (subPhens == null) return null;
    return subPhens.getPhenotype(phenotype);
  }

  public Map<String, SubjectPhenotypes> getResultSet() {
    return rs;
  }
}
