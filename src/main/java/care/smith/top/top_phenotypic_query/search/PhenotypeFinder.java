package care.smith.top.top_phenotypic_query.search;

import java.util.Map;

import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;

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
    return executeCompositeSearches(executeSingleSearches());
  }

  private ResultSet executeCompositeSearches(ResultSet rs) {
    for (QueryCriterion cri : query.getCriteria()) {
      EntityType type = cri.getSubject().getEntityType();
      if (type == EntityType.COMBINED_PHENOTYPE
          || type == EntityType.COMBINED_RESTRICTION
          || type == EntityType.DERIVED_PHENOTYPE
          || type == EntityType.DERIVED_RESTRICTION)
        new CompositeSearch(query, cri, rs, phenotypes).execute();
    }
    return rs;
  }

  private ResultSet executeSingleSearches() {
    SingleQueryMan man = new SingleQueryMan(adapter);
    for (QueryCriterion cri : query.getCriteria()) {
      EntityType type = cri.getSubject().getEntityType();
      if (type == EntityType.SINGLE_PHENOTYPE || type == EntityType.SINGLE_RESTRICTION)
        man.addCriterion(new SingleSearch(query, cri, adapter));
      else {
        for (String var : ExpressionUtil.getVariables(cri.getSubject().getExpression()))
          man.addVariable(new SingleSearch(query, cri, phenotypes.get(var), adapter));
      }
    }
    return man.execute();
  }
}
