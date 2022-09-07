package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;

public class Phenotypes extends HashMap<String, Values> {

  private static final long serialVersionUID = 1L;
  private String subjectId;

  private Logger log = LoggerFactory.getLogger(Phenotypes.class);

  public Phenotypes(String subjectId) {
    this.subjectId = subjectId;
  }

  public void setValues(Values values) {
    put(values.getPhenotypeName(), values);
  }

  public void setValues(Values... values) {
    for (Values vals : values) setValues(vals);
  }

  public void addValue(String phenotypeName, DateTimeRestriction dateRange, Value val) {
    Values values = get(phenotypeName);
    if (values == null) {
      values = new Values(phenotypeName);
      setValues(values);
    }
    values.addValue(dateRange, val);
    log.info(
        "value is added: {}::{}::{}::{}",
        subjectId,
        phenotypeName,
        RestrictionUtil.toString(dateRange),
        val.getRepresentation());
  }

  public void setValues(String phenotypeName, DateTimeRestriction dateRange, ValueList vals) {
    Values values = get(phenotypeName);
    if (values == null) {
      values = new Values(phenotypeName);
      setValues(values);
    }
    values.setValues(dateRange, vals);
    log.info(
        "values are set: {}::{}::{}::{}",
        subjectId,
        phenotypeName,
        RestrictionUtil.toString(dateRange),
        vals.getRepresentation());
  }

  public Values getValues(String phenotypeName) {
    return get(phenotypeName);
  }

  public ValueList getValues(String phenotypeName, DateTimeRestriction dateRange) {
    Values values = getValues(phenotypeName);
    if (values == null) return null;
    return values.getValues(dateRange);
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

  @Override
  public String toString() {
    StringBuffer sb =
        new StringBuffer("  Subject '")
            .append(subjectId)
            .append("':")
            .append(System.lineSeparator())
            .append(System.lineSeparator());
    for (care.smith.top.top_phenotypic_query.result.Values values : values())
      sb.append(values).append(System.lineSeparator());
    return sb.toString();
  }
}
