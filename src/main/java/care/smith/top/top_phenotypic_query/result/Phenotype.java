package care.smith.top.top_phenotypic_query.result;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;

import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.util.ToString;

public class Phenotype {

  private String name;
  private ArrayListMultimap<DateTimeRestriction, Value> values = ArrayListMultimap.create();

  public Phenotype(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addValues(DateTimeRestriction dateRange, Value... values) {
    addValues(dateRange, List.of(values));
  }

  public void addValues(DateTimeRestriction dateRange, List<Value> values) {
    this.values.putAll(dateRange, values);
  }

  public void addValue(DateTimeRestriction dateRange, Value value) {
    this.values.put(dateRange, value);
  }

  public List<Value> getValues(DateTimeRestriction dateRange) {
    return values.get(dateRange);
  }

  public boolean hasValues() {
    return !values.isEmpty();
  }

  public boolean hasValues(DateTimeRestriction dateRange) {
    return values.containsKey(dateRange);
  }

  @Override
  public String toString() {
    return ToString.get(this).add("name", name).add("values", values).toString();
  }
}
