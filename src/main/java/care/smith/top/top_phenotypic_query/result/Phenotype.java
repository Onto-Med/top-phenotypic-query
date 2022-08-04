package care.smith.top.top_phenotypic_query.result;

import java.util.ArrayList;
import java.util.List;

import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.util.ToString;

public class Phenotype {

  private String name;
  private List<Value> values = new ArrayList<>();

  public Phenotype() {}

  public Phenotype(String name) {
    setName(name);
  }

  public Phenotype(String name, List<Value> values) {
    setName(name);
    setValues(values);
  }

  public Phenotype(String name, Value... values) {
    setName(name);
    setValues(values);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Value> getValues() {
    return values;
  }

  public void setValues(Value... values) {
    setValues(List.of(values));
  }

  public void setValues(List<Value> values) {
    this.values = values;
  }

  public void addValue(Value value) {
    this.values.add(value);
  }

  public boolean hasValues() {
    return !values.isEmpty();
  }

  @Override
  public String toString() {
    return ToString.get(this).add("name", name).add("values", values).toString();
  }
}
