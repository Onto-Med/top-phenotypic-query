package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.model.property.data.value.list.BooleanValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.DateTimeValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.DecimalValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.StringValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;

public class Values extends HashMap<DateTimeRestriction, ValueList> {

  private static final long serialVersionUID = 1L;
  private String phenotypeName;

  public Values(String phenotypeName) {
    this.phenotypeName = phenotypeName;
  }

  public String getPhenotypeName() {
    return phenotypeName;
  }

  public void setValues(DateTimeRestriction dateRange, ValueList values) {
    put(dateRange, values);
  }

  public void addValue(DateTimeRestriction dateRange, Value value) {
    ValueList vals = get(dateRange);
    if (vals == null) put(dateRange, ValueList.get(value.getDatatype(), value));
    else vals.addValueCheked(value);
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
    Values other = (Values) obj;
    return Objects.equals(phenotypeName, other.phenotypeName)
        && Objects.equals(entrySet(), other.entrySet());
  }

  @Override
  public String toString() {
    StringBuffer sb =
        new StringBuffer("    Phenotype '")
            .append(phenotypeName)
            .append("':")
            .append(System.lineSeparator());
    for (Entry<DateTimeRestriction, ValueList> entry : entrySet())
      sb.append("    ")
          .append(RestrictionUtil.toString(entry.getKey()))
          .append("::")
          .append(entry.getValue().getRepresentation())
          .append(System.lineSeparator());
    return sb.toString();
  }
}
