package care.smith.top.top_phenotypic_query.search;

import care.smith.top.simple_onto_api.model.ClassDef;

public class PropertySearch {

  private ClassDef property;
  private ClassDef[] restriction;
  private String[] id;
  private String[] subjectId;

  public ClassDef getProperty() {
    return property;
  }

  public PropertySearch property(ClassDef property) {
    this.property = property;
    return this;
  }

  public ClassDef[] getRestriction() {
    return restriction;
  }

  public PropertySearch restriction(ClassDef... restriction) {
    this.restriction = restriction;
    return this;
  }

  public String[] getId() {
    return id;
  }

  public PropertySearch id(String... id) {
    this.id = id;
    return this;
  }

  public String[] getSubjectId() {
    return subjectId;
  }

  public PropertySearch subjectId(String... subjectId) {
    this.subjectId = subjectId;
    return this;
  }
}
