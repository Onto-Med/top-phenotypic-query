package care.smith.top.top_phenotypic_query.search;

import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class USiPSearch extends PhenotypeSearch {

  private DataAdapter adapter;

  public USiPSearch(Query query, QueryCriterion criterion, DataAdapter adapter) {
    super(query, criterion);
    this.adapter = adapter;
  }

  @Override
  public ResultSet execute() {
    return adapter.findPhenotypes(this);
  }
}
