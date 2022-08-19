package care.smith.top.top_phenotypic_query.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.Expression;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.simple_onto_api.calculator.Calculator;
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
        executeForSubject(sbjPhens, exp, criterion.getDateTimeRestrictions());
    }
    return rs;
  }

  public void executeForSubject(
      SubjectPhenotypes sbjPhens, Expression exp, List<DateTimeRestriction> dateRanges) {
    Calculator calc = new Calculator();
    for (String var : ExpressionUtil.getVariables(exp))
      calc.setVariable(var, getValues(sbjPhens, exp, var, dateRanges, calc));
  }

  public ValueList getValues(
      SubjectPhenotypes sbjPhens,
      Expression exp,
      String var,
      List<DateTimeRestriction> dateRanges,
      Calculator calc) {
    ValueList res = null;

    if (dateRanges == null || dateRanges.isEmpty()) res = sbjPhens.getValues(var, null);
    else {
      res = sbjPhens.getValues(var, dateRanges.get(0));
    }

    for (DateTimeRestriction dateRange : dateRanges) {
      ValueList values = sbjPhens.getValues(var, dateRange);
    }
    return null;
  }

  public ValueList getValues(
      SubjectPhenotypes sbjPhens, String var, DateTimeRestriction dateRange) {
    ValueList res = sbjPhens.getValues(var, dateRange);
    if (res == null)
      executeForSubject(sbjPhens, phenotypes.get(var).getExpression(), List.of(dateRange));
    return sbjPhens.getValues(var, dateRange);
  }
}
