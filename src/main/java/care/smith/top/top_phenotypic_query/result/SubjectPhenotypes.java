package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.List;

import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class SubjectPhenotypes extends HashMap<String, Phenotype> {

  private static final long serialVersionUID = 1L;
  private String subject;

  public SubjectPhenotypes(String subject) {
    this.subject = subject;
  }

  public void addPhenotype(Phenotype phenotype) {
    put(phenotype.getName(), phenotype);
  }

  public void addPhenotype(String name, List<Value> values) {
    addPhenotype(new Phenotype(name, values));
  }

  public void addPhenotype(String name, Value... values) {
    addPhenotype(new Phenotype(name, values));
  }

  public Phenotype getPhenotype(String name) {
    return get(name);
  }

  public String getSubject() {
    return subject;
  }
}
