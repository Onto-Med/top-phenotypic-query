package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class CompositeSearch extends PhenotypeSearch {

  private QueryCriterion criterion;
  private ResultSet rs;
  private Entities phenotypes;

  private Logger log = LoggerFactory.getLogger(CompositeSearch.class);

  private static final C2R c2r = new C2R();

  public CompositeSearch(Query query, QueryCriterion criterion, ResultSet rs, Entities phenotypes) {
    super(query);
    this.criterion = criterion;
    this.rs = rs;
    this.phenotypes = phenotypes;
  }

  @Override
  public ResultSet execute() {
    Phenotype phe = phenotypes.getPhenotype(criterion.getSubjectId());
    Expression exp = phe.getExpression();
    if (exp != null) {
      Set<String> vars = Expressions.getDirectVariables(exp, phenotypes);
      Set<String> sbjIds = new HashSet<>(rs.getSubjectIds());
      for (String sbjId : sbjIds)
        executeForSubject(sbjId, phe.getId(), exp, vars, criterion.getDateTimeRestriction());
    }
    return rs;
  }

  private void executeForSubject(
      String sbjId, String pheId, Expression exp, Set<String> vars, DateTimeRestriction dateRange) {

    log.debug("start composite search for subject: {} ...", sbjId);
    log.debug("expression: {}", c2r.toString(exp));
    log.debug("variables: {}", vars);

    if (exp == null) return;
    Expression resExp = calculate(sbjId, pheId, exp, vars, dateRange);
    boolean res = (resExp == null) ? false : Expressions.getBooleanValue(resExp);

    log.debug("result of composite search for subject: {} :: {}", pheId, res);

    if ((!criterion.isInclusion() && res) || (criterion.isInclusion() && !res)) rs.remove(sbjId);
  }

  private Expression calculate(
      String sbjId, String pheId, Expression exp, Set<String> vars, DateTimeRestriction dateRange) {
    log.debug("calculate expression: {}", c2r.toString(exp));

    C2R calc = new C2R();
    for (String var : vars) {
      log.debug("variable: {}", var);

      List<Value> vals = getValues(sbjId, var, dateRange);

      log.debug("values of: {} :: {}", var, Values.toString(vals));

      if (vals == null) {
        Expression defaultValue = Expressions.getDefaultValue(exp);
        if (defaultValue == null) {
          log.debug("result of: {} :: null", c2r.toString(exp));
          return null;
        } else {
          log.debug("default result of: {} :: {}", c2r.toString(exp), c2r.toString(defaultValue));
          return defaultValue;
        }
      }

      calc.setVariable(var, vals);
    }
    Expression res = calc.calculate(exp);

    if (res == null) log.debug("result of: {} :: null", c2r.toString(exp));
    else log.debug("result of: {} :: {}", c2r.toString(exp), c2r.toString(res));

    rs.getPhenotypes(sbjId).setValues(pheId, dateRange, Expressions.getValueOrValues(res));
    return res;
  }

  private List<Value> getValues(String sbjId, String var, DateTimeRestriction dateRange) {
    List<Value> vals = rs.getPhenotypes(sbjId).getValues(var, dateRange);
    if (vals != null) return vals;
    Phenotype phe = phenotypes.getPhenotype(var);
    if (Phenotypes.isSingle(phe) && Phenotypes.hasBooleanType(phe)) return List.of(Val.ofFalse());
    Expression newExp = phe.getExpression();

    if (newExp == null) {
      log.debug("nested expression of: {} :: null", var);
      return null;
    } else log.debug("nested expression of: {} :: {}", var, c2r.toString(newExp));

    Set<String> newVars = Expressions.getDirectVariables(newExp, phenotypes);
    Expression newRes = calculate(sbjId, var, newExp, newVars, dateRange);

    if (newRes == null) return null;

    return Expressions.getValueOrValues(newRes);
  }
}
