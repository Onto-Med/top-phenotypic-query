package care.smith.top.top_phenotypic_query.search;

import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class UDePSearch extends PhenotypeSearch {

  public UDePSearch(Query query, QueryCriterion criterion) {
    super(query, criterion);
  }

  @Override
  public ResultSet execute() {
    return null;
  }
}
