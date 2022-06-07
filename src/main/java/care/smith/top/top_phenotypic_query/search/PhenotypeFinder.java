package care.smith.top.top_phenotypic_query.search;

import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class PhenotypeFinder {

  private Query query;
  private DataAdapter adapter;

  public PhenotypeFinder(Query query, DataAdapter adapter) {
    this.query = query;
    this.adapter = adapter;
  }

  public ResultSet execute() {
    // create SubjectSearch and PhenotypeSearch objects (for each criterion)

    // call execute() methods

    // calculate and return ResultSet

    for (QueryCriterion cri : query.getCriteria()) {
      EntityType type = cri.getSubject().getEntityType();
      if (type == EntityType.SINGLE_PHENOTYPE) new USiPSearch(query, cri, adapter);
      // ...
    }

    return null;
  }
}
