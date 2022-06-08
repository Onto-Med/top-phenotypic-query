package care.smith.top.top_phenotypic_query.search;

import care.smith.top.backend.model.Expression;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class CombinedRestrictionSearch extends PhenotypeSearch {

  public CombinedRestrictionSearch(Query query, QueryCriterion criterion) {
    super(query, criterion);
  }

  @Override
  public ResultSet execute() {
    Expression exp = getCriterion().getSubject().getExpression();
    return null;
  }

  private ResultSet execute(Expression exp) {
    //    if (exp.getId() != null) return execute(exp.getId());
    //    if (exp.getOperator())
    return null;
  }

  private ResultSet execute(String id) {
    return null;
  }
}
