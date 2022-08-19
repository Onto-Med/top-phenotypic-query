package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.list.BooleanValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.DateTimeValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.DecimalValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.StringValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;
import care.smith.top.simple_onto_api.util.ToString;

public class PhenotypeValues extends HashMap<DateTimeRestriction, ValueList> {

  private static final long serialVersionUID = 1L;
  private String phenotypeName;

  public PhenotypeValues(String phenotypeName) {
    this.phenotypeName = phenotypeName;
  }

  public String getPhenotypeName() {
    return phenotypeName;
  }

  public void setValues(DateTimeRestriction dateRange, ValueList values) {
    put(dateRange, values);
  }

  public void setStringValues(DateTimeRestriction dateRange, StringValue... values) {
    put(dateRange, new StringValueList(values));
  }

  public void setDecimalValues(DateTimeRestriction dateRange, DecimalValue... values) {
    put(dateRange, new DecimalValueList(values));
  }

  public void setDateTimeValues(DateTimeRestriction dateRange, DateTimeValue... values) {
    put(dateRange, new DateTimeValueList(values));
  }

  public void setBooleanValues(DateTimeRestriction dateRange, BooleanValue... values) {
    put(dateRange, new BooleanValueList(values));
  }

  public ValueList getValues(DateTimeRestriction dateRange) {
    return get(dateRange);
  }

  public boolean hasDateTimeRestriction(DateTimeRestriction dateRange) {
    return containsKey(dateRange);
  }

  public Set<DateTimeRestriction> getDateTimeRestrictions() {
    return keySet();
  }

  @Override
  public int hashCode() {
    return Objects.hash(phenotypeName, entrySet());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PhenotypeValues other = (PhenotypeValues) obj;
    return Objects.equals(phenotypeName, other.phenotypeName)
        && Objects.equals(entrySet(), other.entrySet());
  }

  @Override
  public String toString() {
    return ToString.get(this).add("name", phenotypeName).add("values", super.toString()).toString();
  }
}
