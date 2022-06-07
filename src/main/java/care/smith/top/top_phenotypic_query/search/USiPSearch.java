package care.smith.top.top_phenotypic_query.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import care.smith.top.simple_onto_api.c2reasoner.function.Function;
import care.smith.top.simple_onto_api.util.ToString;

public class USiPSearch extends PhenotypeSearch {

  private String type;
  private Function function;
  private String unit;
  private List<String> ids = new ArrayList<>();
  private List<String> subjectIds = new ArrayList<>();

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Function getFunction() {
    return function;
  }

  public void setFunction(Function function) {
    this.function = function;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public List<String> getIds() {
    return ids;
  }

  public void addId(String id) {
    this.ids.add(id);
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }

  public void setIds(String... ids) {
    setIds(Arrays.asList(ids));
  }

  public List<String> getSubjectIds() {
    return subjectIds;
  }

  public void addSubjectId(String subjectId) {
    this.subjectIds.add(subjectId);
  }

  public void setSubjectIds(List<String> subjectIds) {
    this.subjectIds = subjectIds;
  }

  public void setSubjectIds(String... subjectIds) {
    setSubjectIds(Arrays.asList(subjectIds));
  }

  @Override
  public String toString() {
    return super.toString()
        + "::"
        + ToString.get(this)
            .add("type", type)
            .add("function", function)
            .add("unit", unit)
            .add("ids", ids)
            .add("subjectIds", subjectIds)
            .toString();
  }
}
