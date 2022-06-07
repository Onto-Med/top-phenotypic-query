package care.smith.top.top_phenotypic_query.result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.util.StringUtil;
import care.smith.top.simple_onto_api.util.ToString;

public class Phenotype {

  private String name;
  private List<Value> values = new ArrayList<>();
  private BigDecimal score;
  private String restriction;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Value> getValues() {
    return values;
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

  public BigDecimal getScore() {
    return score;
  }

  public void setScore(BigDecimal score) {
    this.score = score;
  }

  public boolean hasScore() {
    return score != null;
  }

  public String getRestriction() {
    return restriction;
  }

  public void setRestriction(String restriction) {
    this.restriction = restriction;
  }

  public boolean hasRestriction() {
    return StringUtil.notEmpty(restriction);
  }

  @Override
  public String toString() {
    return ToString.get(this)
        .add("name", name)
        .add("values", values)
        .add("score", score)
        .add("restriction", restriction)
        .toString();
  }
}
