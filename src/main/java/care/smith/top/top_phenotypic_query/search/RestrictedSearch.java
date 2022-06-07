package care.smith.top.top_phenotypic_query.search;

import care.smith.top.simple_onto_api.util.ToString;

public abstract class RestrictedSearch extends PhenotypeSearch {

  private String superClassName;

  public String getSuperClassName() {
    return superClassName;
  }

  public void setSuperClassName(String superClassName) {
    this.superClassName = superClassName;
  }

  @Override
  public String toString() {
    return super.toString()
        + "::"
        + ToString.get(this).add("superClassName", superClassName).toString();
  }
}
