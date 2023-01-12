package care.smith.top.top_phenotypic_query.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;

public class Expressions {

  private static Value getValue(Expression exp) {
    if (exp.getValue() != null) return exp.getValue();
    if (exp.getValues() != null && !exp.getValues().isEmpty()) return exp.getValues().get(0);
    return null;
  }

  public static BigDecimal getNumberValue(Expression exp) {
    return Values.getNumberValue(getValue(exp));
  }

  public static String getStringValue(Expression exp) {
    return Values.getStringValue(getValue(exp));
  }

  public static Boolean getBooleanValue(Expression exp) {
    return Values.getBooleanValue(getValue(exp));
  }

  public static LocalDateTime getDateTimeValue(Expression exp) {
    return Values.getDateTimeValue(getValue(exp));
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
    Set<String> vars = new LinkedHashSet<>();
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

  public static Expression getDefaultValue(Expression exp) {
    if (exp.getFunctionId() == null) return null;
    return new C2R().getFunction(exp.getFunctionId()).getDefaultValue();
  }
}
