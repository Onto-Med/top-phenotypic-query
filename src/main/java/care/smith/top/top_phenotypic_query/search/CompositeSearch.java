package care.smith.top.top_phenotypic_query.search;

import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class CompositeSearch extends PhenotypeSearch {

  private QueryCriterion criterion;

  public CompositeSearch(Query query, QueryCriterion criterion) {
    super(query);
    this.criterion = criterion;
  }

  @Override
  public ResultSet execute() {
    return null;
  }
}
