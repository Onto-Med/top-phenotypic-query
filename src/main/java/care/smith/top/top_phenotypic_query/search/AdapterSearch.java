package care.smith.top.top_phenotypic_query.search;

import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;

public abstract class AdapterSearch extends PhenotypeSearch {

  protected DataAdapter adapter;

  protected AdapterSearch(Query query, QueryCriterion criterion, DataAdapter adapter) {
    super(query, criterion);
  }
}
