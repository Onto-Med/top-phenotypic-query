package care.smith.top.top_phenotypic_query.adapter.mapping;

import java.util.ArrayList;
import java.util.List;

import care.smith.top.backend.model.Restriction;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;

public class RestrictionMapping {

  private List<String> model = new ArrayList<>();
  private List<String> source = new ArrayList<>();

  public List<String> getModel() {
    return model;
  }

  public void setModel(List<String> model) {
    this.model = model;
  }

  public List<String> getSource() {
    return source;
  }

  public void setSource(List<String> source) {
    this.source = source;
  }

  public Restriction getModelRestriction() {
    return RestrictionUtil.getRestriction(model);
  }

  public Restriction getSourceRestriction() {
    return RestrictionUtil.getRestriction(source);
  }

  @Override
  public String toString() {
    return "RestrictionMapping [model=" + model + ", source=" + source + "]";
  }
}
