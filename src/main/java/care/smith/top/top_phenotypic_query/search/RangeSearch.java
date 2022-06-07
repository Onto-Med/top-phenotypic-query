package care.smith.top.top_phenotypic_query.search;

import care.smith.top.simple_onto_api.model.property.data.range.DataRange;
import care.smith.top.simple_onto_api.util.ToString;

public abstract class RangeSearch extends RestrictedSearch {

  private DataRange dataRange;

  public DataRange getDataRange() {
    return dataRange;
  }

  public void setDataRange(DataRange dataRange) {
    this.dataRange = dataRange;
  }

  @Override
  public String toString() {
    return super.toString() + "::" + ToString.get(this).add("dataRange", dataRange).toString();
  }
}
