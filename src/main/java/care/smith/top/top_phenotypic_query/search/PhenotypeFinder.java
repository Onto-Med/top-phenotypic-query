package care.smith.top.top_phenotypic_query.search;

import java.util.Map;

import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.Expression;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class PhenotypeFinder {

  private Query query;
  private Map<String, Phenotype> phenotypes;
  private DataAdapter adapter;

  public PhenotypeFinder(Query query, Map<String, Phenotype> phenotypes, DataAdapter adapter) {
    this.query = query;
    this.phenotypes = phenotypes;
    this.adapter = adapter;
  }

  public ResultSet execute() {
    // create list of single phenotype searches

    // create SubjectSearch and PhenotypeSearch objects (for each criterion)

    // call execute() methods

    // calculate and return ResultSet

    for (QueryCriterion cri : query.getCriteria()) {
      EntityType type = cri.getSubject().getEntityType();
      if (type == EntityType.SINGLE_PHENOTYPE) new SingleSearch(query, cri, adapter);
      // ...
    }

    return null;
  }

  private ResultSet executeSingleSearches() {
    QueryMan man = new QueryMan();
    for (QueryCriterion cri : query.getCriteria()) {
      EntityType type = cri.getSubject().getEntityType();
      if (type == EntityType.SINGLE_PHENOTYPE || type == EntityType.SINGLE_RESTRICTION)
        man.addCriterion(new SingleSearch(query, cri, adapter));
      else addVariables(cri.getSubject().getExpression(), cri, man);
    }
    return man.execute();
  }

  private void addVariables(Expression exp, QueryCriterion cri, QueryMan man) {
    if (exp.getId() != null)
      man.addVariable(new SingleSearch(query, cri, phenotypes.get(exp.getId()), adapter));
  }
}
