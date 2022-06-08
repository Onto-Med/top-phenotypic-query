package care.smith.top.top_phenotypic_query.search;

import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public abstract class PhenotypeSearch {

  private Query query;
  private QueryCriterion criterion;

  protected PhenotypeSearch(Query query, QueryCriterion criterion) {
    this.query = query;
    this.criterion = criterion;
  }

  public Query getQuery() {
    return query;
  }

  public QueryCriterion getCriterion() {
    return criterion;
  }

  public abstract ResultSet execute();
}
