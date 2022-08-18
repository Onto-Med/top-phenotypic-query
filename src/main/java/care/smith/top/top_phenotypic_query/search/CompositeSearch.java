package care.smith.top.top_phenotypic_query.search;

import java.util.Map;
import java.util.Set;

import care.smith.top.backend.model.Expression;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.simple_onto_api.calculator.Calculator;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;

public class CompositeSearch extends PhenotypeSearch {

  private QueryCriterion criterion;
  private ResultSet rs;
  private Map<String, Phenotype> phenotypes;

  public CompositeSearch(
      Query query, QueryCriterion criterion, ResultSet rs, Map<String, Phenotype> phenotypes) {
    super(query);
    this.criterion = criterion;
    this.rs = rs;
    this.phenotypes = phenotypes;
  }

  @Override
  public ResultSet execute() {
    Expression exp = criterion.getSubject().getExpression();
    if (exp != null) {
      Set<String> vars = ExpressionUtil.getVariables(exp);
      Calculator c = new Calculator();
    }

    return null;
  }
}
