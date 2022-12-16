package care.smith.top.top_phenotypic_query.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;

public class Expressions {

  public static BigDecimal getNumberValue(Expression exp) {
    return Values.getNumberValue(exp.getValue());
  }

  public static String getStringValue(Expression exp) {
    return Values.getStringValue(exp.getValue());
  }

  public static Boolean getBooleanValue(Expression exp) {
    return Values.getBooleanValue(exp.getValue());
  }

  public static LocalDateTime getDateTimeValue(Expression exp) {
    return Values.getDateTimeValue(exp.getValue());
  }

  public static List<BigDecimal> getNumberValues(Expression exp) {
    return Values.getNumberValues(exp.getValues());
  }

  public static List<String> getStringValues(Expression exp) {
    return Values.getStringValues(exp.getValues());
  }

  public static List<Boolean> getBooleanValues(Expression exp) {
    return Values.getBooleanValues(exp.getValues());
  }

  public static List<LocalDateTime> getDateTimeValues(Expression exp) {
    return Values.getDateTimeValues(exp.getValues());
  }

  public static List<Value> getValueOrValues(Expression exp) {
    if (exp.getValue() != null) return List.of(exp.getValue());
    if (exp.getValues() != null) return exp.getValues();
    return null;
  }

  public static boolean hasValueTrue(Expression exp) {
    return Values.getBooleanValue(exp.getValue());
  }

  public static boolean hasValueFalse(Expression exp) {
    return !hasValueTrue(exp);
  }

  public static Expression newExpression(Restriction r) {
    return new Expression().restriction(r);
  }

  public static Expression newExpression(Integer card, Quantifier quan, Number... vals) {
    return newExpression(Restrictions.newRestriction(card, quan, vals));
  }

  public static Expression newExpression(
      Integer card, Quantifier quan, RestrictionOperator oper, Number val) {
    return newExpression(Restrictions.newRestriction(card, quan, oper, val));
  }

  public static Expression newExpression(
      Integer card,
      Quantifier quan,
      RestrictionOperator minOper,
      Number minVal,
      RestrictionOperator maxOper,
      Number maxVal) {
    return newExpression(Restrictions.newRestriction(card, quan, minOper, minVal, maxOper, maxVal));
  }

  public static Expression newExpression(Quantifier quan, Number... vals) {
    return newExpression(null, quan, vals);
  }

  public static Expression newExpression(Quantifier quan, RestrictionOperator oper, Number val) {
    return newExpression(null, quan, oper, val);
  }

  public static Expression newExpression(
      Quantifier quan,
      RestrictionOperator minOper,
      Number minVal,
      RestrictionOperator maxOper,
      Number maxVal) {
    return newExpression(null, quan, minOper, minVal, maxOper, maxVal);
  }

  public static Expression newExpression(Integer card, Quantifier quan, LocalDateTime... vals) {
    return newExpression(Restrictions.newRestriction(card, quan, vals));
  }

  public static Expression newExpression(
      Integer card, Quantifier quan, RestrictionOperator oper, LocalDateTime val) {
    return newExpression(Restrictions.newRestriction(card, quan, oper, val));
  }

  public static Expression newExpression(
      Integer card,
      Quantifier quan,
      RestrictionOperator minOper,
      LocalDateTime minVal,
      RestrictionOperator maxOper,
      LocalDateTime maxVal) {
    return newExpression(Restrictions.newRestriction(card, quan, minOper, minVal, maxOper, maxVal));
  }

  public static Expression newExpression(Quantifier quan, LocalDateTime... vals) {
    return newExpression(null, quan, vals);
  }

  public static Expression newExpression(
      Quantifier quan, RestrictionOperator oper, LocalDateTime val) {
    return newExpression(null, quan, oper, val);
  }

  public static Expression newExpression(
      Quantifier quan,
      RestrictionOperator minOper,
      LocalDateTime minVal,
      RestrictionOperator maxOper,
      LocalDateTime maxVal) {
    return newExpression(null, quan, minOper, minVal, maxOper, maxVal);
  }

  public static Expression newExpression(Value val) {
    return new Expression().value(val);
  }

  public static Expression newExpression(List<Value> vals) {
    return new Expression().values(vals);
  }

  public static Expression newExpression(Value... vals) {
    return newExpression(List.of(vals));
  }

  public static Expression newExpression(Number val, LocalDateTime dateTime) {
    return newExpression(Values.newValue(val, dateTime));
  }

  public static Expression newExpression(Number val) {
    return newExpression(Values.newValue(val));
  }

  public static Expression newExpression(Number... numbers) {
    return newExpression(Values.newValues(numbers));
  }

  public static Expression newExpression(Boolean val) {
    return newExpression(Values.newValue(val));
  }

  public static Expression newExpression(LocalDateTime val) {
    return newExpression(Values.newValue(val));
  }

  public static List<Expression> toExpressionList(List<Value> vals) {
    return vals.stream().map(v -> newExpression(v)).collect(Collectors.toList());
  }

  public static Expression newExpressionTrue() {
    return newExpression(Values.newValueTrue());
  }

  public static Expression newExpressionFalse() {
    return newExpression(Values.newValueFalse());
  }

  public static DataType getDataType(Expression exp) {
    if (exp.getValue() != null) return exp.getValue().getDataType();
    if (exp.getValues() != null && !exp.getValues().isEmpty())
      return exp.getValues().get(0).getDataType();
    if (exp.getRestriction() != null) return exp.getRestriction().getType();
    return null;
  }

  public static String toStringValues(Expression exp) {
    if (exp.getValue() != null) return Values.toString(exp.getValue());
    if (exp.getValues() != null) return Values.toString(exp.getValues());
    return null;
  }

  public static Set<String> getDirectVariables(Expression exp, Entities phenotypes) {
    return getVariables(exp, phenotypes, true);
  }

  public static Set<String> getVariables(Expression exp, Entities phenotypes) {
    return getVariables(exp, phenotypes, false);
  }

  public static Set<String> getVariables(Expression exp, Entities phenotypes, boolean direct) {
    Set<String> vars = new HashSet<>();
    addVariables(exp, vars, phenotypes, direct);
    return vars;
  }

  private static void addVariables(
      Expression exp, Set<String> vars, Entities phenotypes, boolean direct) {
    if (exp == null) return;
    if (exp.getEntityId() != null) {
      vars.add(exp.getEntityId());
      Phenotype varPhe = phenotypes.getPhenotype(exp.getEntityId());
      if (varPhe != null && !direct) {
        Expression varPheExp = varPhe.getExpression();
        if (varPheExp != null) addVariables(varPheExp, vars, phenotypes, direct);
      }
    } else if (exp.getArguments() != null)
      for (Expression arg : exp.getArguments()) addVariables(arg, vars, phenotypes, direct);
  }

  public static Expression restrictionToExpression(Phenotype p) {
    if (!Phenotypes.isRestriction(p)) return null;
    Restriction r = p.getRestriction();
    if (r == null) return null;
    return new Expression()
        .functionId("in")
        .addArgumentsItem(new Expression().entityId(p.getSuperPhenotype().getId()))
        .addArgumentsItem(new Expression().restriction(r));
  }
}
