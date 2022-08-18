package care.smith.top.top_phenotypic_query.result;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;

import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.util.ToString;

public class PhenotypeValues {

  private String phenotypeName;
  private ArrayListMultimap<DateTimeRestriction, Value> values = ArrayListMultimap.create();

  public PhenotypeValues(String phenotypeName) {
    this.phenotypeName = phenotypeName;
  }

  public String getPhenotypeName() {
    return phenotypeName;
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

  public Set<DateTimeRestriction> getDateRanges() {
    return values.keySet();
  }

  @Override
  public int hashCode() {
    return Objects.hash(phenotypeName, values);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PhenotypeValues other = (PhenotypeValues) obj;
    return Objects.equals(phenotypeName, other.phenotypeName)
        && Objects.equals(values, other.values);
  }

  @Override
  public String toString() {
    return ToString.get(this).add("name", phenotypeName).add("values", values).toString();
  }
}
