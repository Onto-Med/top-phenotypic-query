package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.simple_onto_api.calculator.Calculator;
import care.smith.top.simple_onto_api.calculator.expressions.MathExpression;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;
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
    Phenotype phe = phenotypes.get(criterion.getSubjectId());
    Expression exp = phe.getExpression();
    if (exp != null) {
      Set<String> vars = ExpressionUtil.getVariables(exp, phenotypes);
      Set<String> sbjIds = new HashSet<>(rs.getSubjectIds());
      for (String sbjId : sbjIds)
        executeForSubject(sbjId, phe.getId(), exp, vars, criterion.getDateTimeRestriction());
    }
    return rs;
  }

  private void executeForSubject(
      String sbjId, String pheId, Expression exp, Set<String> vars, DateTimeRestriction dateRange) {
    if (exp == null) return;
    Value resVal = calculate(sbjId, pheId, exp, vars, dateRange);
    boolean res = (resVal == null) ? false : resVal.asBooleanValue().getValue();
    if ((criterion.isExclusion() && res) || (!criterion.isExclusion() && !res)) rs.remove(sbjId);
  }

  private Value calculate(
      String sbjId, String pheId, Expression exp, Set<String> vars, DateTimeRestriction dateRange) {
    Calculator calc = new Calculator();
    for (String var : vars) {
      ValueList vals = getValues(sbjId, var, dateRange);
      if (vals == null) return null;
      calc.setVariable(var, vals);
    }
    MathExpression mathExp = ExpressionUtil.convert(exp);
    Value res = calc.calculate(mathExp);
    rs.getPhenotypes(sbjId).setValues(pheId, dateRange, ValueList.get(res));
    return res;
  }

  private ValueList getValues(String sbjId, String var, DateTimeRestriction dateRange) {
    ValueList vals = rs.getPhenotypes(sbjId).getValues(var, dateRange);
    if (vals != null) return vals;
    Expression newExp = phenotypes.get(var).getExpression();
    if (newExp == null) return null;
    Set<String> newVars = ExpressionUtil.getVariables(newExp, phenotypes);
    Value newVal = calculate(sbjId, var, newExp, newVars, dateRange);
    if (newVal == null) return null;
    return ValueList.get(newVal);
  }
}
