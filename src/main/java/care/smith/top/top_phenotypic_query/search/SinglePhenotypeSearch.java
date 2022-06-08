package care.smith.top.top_phenotypic_query.search;

import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class SinglePhenotypeSearch extends AdapterSearch {

  public SinglePhenotypeSearch(Query query, QueryCriterion criterion, DataAdapter adapter) {
    super(query, criterion, adapter);
  }

  @Override
  public ResultSet execute() {
    return adapter.execute(this);
  }
}
