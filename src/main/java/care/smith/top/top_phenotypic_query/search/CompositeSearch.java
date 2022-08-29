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
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
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
    Phenotype phe = criterion.getSubject();
    Expression exp = phe.getExpression();
    if (exp != null) {
      Set<String> vars = ExpressionUtil.getVariables(exp);
      for (Phenotypes sbjPhens : rs.values())
        executeForSubject(sbjPhens, phe.getId(), exp, vars, criterion.getDateTimeRestriction());
    }
    return rs;
  }

  private void executeForSubject(
      Phenotypes sbjPhens,
      String pheId,
      Expression exp,
      Set<String> vars,
      DateTimeRestriction dateRange) {
    boolean res = calculate(sbjPhens, pheId, exp, vars, dateRange).asBooleanValue().getValue();
    if ((criterion.isExclusion() && res) || (!criterion.isExclusion() && !res))
      rs.remove(sbjPhens.getSubjectId());
  }

  private Value calculate(
      Phenotypes sbjPhens,
      String pheId,
      Expression exp,
      Set<String> vars,
      DateTimeRestriction dateRange) {
    Calculator calc = new Calculator();
    for (String var : vars) calc.setVariable(var, getValues(sbjPhens, var, dateRange));
    MathExpression mathExp = null; // map exp to mathExp
    Value res = calc.calculate(mathExp);
    sbjPhens.setValues(pheId, dateRange, ValueList.get(res));
    return res;
  }

  private ValueList getValues(Phenotypes sbjPhens, String var, DateTimeRestriction dateRange) {
    ValueList vals = sbjPhens.getValues(var, dateRange);
    if (vals != null) return vals;
    Expression newExp = phenotypes.get(var).getExpression();
    Set<String> newVars = ExpressionUtil.getVariables(newExp);
    return ValueList.get(calculate(sbjPhens, var, newExp, newVars, dateRange));
  }
}
