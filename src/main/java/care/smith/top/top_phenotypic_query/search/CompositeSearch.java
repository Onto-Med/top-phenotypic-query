package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Values;

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
      Set<String> vars = Expressions.getVariables(exp, phenotypes);
      Set<String> sbjIds = new HashSet<>(rs.getSubjectIds());
      for (String sbjId : sbjIds)
        executeForSubject(sbjId, phe.getId(), exp, vars, criterion.getDateTimeRestriction());
    }
    return rs;
  }

  private void executeForSubject(
      String sbjId, String pheId, Expression exp, Set<String> vars, DateTimeRestriction dateRange) {
    if (exp == null) return;
    Expression resExp = calculate(sbjId, pheId, exp, vars, dateRange);
    boolean res = (resExp == null) ? false : Expressions.getBooleanValue(resExp);
    if ((!criterion.isInclusion() && res) || (criterion.isInclusion() && !res)) rs.remove(sbjId);
  }

  private Expression calculate(
      String sbjId, String pheId, Expression exp, Set<String> vars, DateTimeRestriction dateRange) {
    C2R calc = new C2R();
    for (String var : vars) {
      List<Value> vals = getValues(sbjId, var, dateRange);
      if (vals == null) return null;
      calc.setVariable(var, vals);
    }
    Expression res = calc.calculate(exp);
    rs.getPhenotypes(sbjId).setValues(pheId, dateRange, Expressions.getValueOrValues(res));
    return res;
  }

  private List<Value> getValues(String sbjId, String var, DateTimeRestriction dateRange) {
    List<Value> vals = rs.getPhenotypes(sbjId).getValues(var, dateRange);
    if (vals != null) return vals;
    Phenotype phe = phenotypes.get(var);
    if (Phenotypes.isSingle(phe) && Phenotypes.getDataType(phe, phenotypes) == DataType.BOOLEAN)
      return List.of(Values.newValueFalse());
    Expression newExp = phe.getExpression();
    if (newExp == null) return null;
    Set<String> newVars = Expressions.getVariables(newExp, phenotypes);
    Expression newRes = calculate(sbjId, var, newExp, newVars, dateRange);
    if (newRes == null) return null;
    return Expressions.getValueOrValues(newRes);
  }
}
