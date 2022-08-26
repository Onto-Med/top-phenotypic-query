package care.smith.top.top_phenotypic_query.search;

import java.util.Map;
import java.util.Set;

import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.Expression;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.simple_onto_api.calculator.Calculator;
import care.smith.top.simple_onto_api.calculator.expressions.MathExpression;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
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
      for (SubjectPhenotypes sbjPhens : rs.values())
        executeForSubject(sbjPhens, exp, vars, criterion.getDateTimeRestriction());
    }
    return rs;
  }

  public void executeForSubject(
      SubjectPhenotypes sbjPhens, Expression exp, Set<String> vars, DateTimeRestriction dateRange) {

    Calculator calc = new Calculator();
    for (String var : vars) calc.setVariable(var, getValues(sbjPhens, exp, var, dateRange, calc));

    MathExpression mathExp = null;

    boolean res = calc.calculate(mathExp).asBooleanValue().getValue();

    if ((criterion.isExclusion() && res) || (!criterion.isExclusion() && !res))
      rs.remove(sbjPhens.getSubjectId());
    // else add value to result set

  }

  public ValueList getValues(
      SubjectPhenotypes sbjPhens,
      Expression exp,
      String var,
      DateTimeRestriction dateRange,
      Calculator calc) {

    ValueList res = sbjPhens.getValues(var, dateRange);

    if (res == null) {
      Expression newExp = phenotypes.get(var).getExpression();
      Set<String> newVars = ExpressionUtil.getVariables(newExp);
      executeForSubject(sbjPhens, newExp, newVars, dateRange);
    }

    return sbjPhens.getValues(var, dateRange);
  }
}
