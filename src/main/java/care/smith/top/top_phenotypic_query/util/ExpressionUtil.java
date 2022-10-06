package care.smith.top.top_phenotypic_query.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import care.smith.top.model.BooleanRestriction;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Expression;
import care.smith.top.model.NumberRestriction;
import care.smith.top.model.NumberValue;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.StringRestriction;
import care.smith.top.model.Value;
import care.smith.top.simple_onto_api.calculator.expressions.ConstantExpression;
import care.smith.top.simple_onto_api.calculator.expressions.FunctionExpression;
import care.smith.top.simple_onto_api.calculator.expressions.MathExpression;
import care.smith.top.simple_onto_api.calculator.expressions.VariableExpression;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;

public class ExpressionUtil {

  public static Set<String> getVariables(Expression exp, Map<String, Phenotype> phenotypes) {
    Set<String> vars = new HashSet<>();
    addVariables(exp, vars, phenotypes);
    return vars;
  }

  private static void addVariables(
      Expression exp, Set<String> vars, Map<String, Phenotype> phenotypes) {
    if (exp == null) return;
    if (exp.getEntityId() != null) {
      vars.add(exp.getEntityId());
      Phenotype varPhe = phenotypes.get(exp.getEntityId());
      if (varPhe != null) {
        Expression varPheExp = varPhe.getExpression();
        if (varPheExp != null) addVariables(varPheExp, vars, phenotypes);
      }
    } else if (exp.getArguments() != null)
      for (Expression arg : exp.getArguments()) addVariables(arg, vars, phenotypes);
  }

  public static MathExpression convert(Expression exp) {
    if (exp == null) return null;
    if (exp.getValue() != null) return getConstantExpression(exp.getConstantId(), exp.getValue());
    if (exp.getFunctionId() != null) return getFunctionExpression(exp);
    return new VariableExpression(exp.getEntityId());
  }

  private static MathExpression getConstantExpression(String constantId, Value value) {
    if (constantId != null) return new ConstantExpression(constantId);
    if (value instanceof care.smith.top.model.BooleanValue)
      return new ConstantExpression(
          new BooleanValue(((care.smith.top.model.BooleanValue) value).isValue()));
    if (value instanceof care.smith.top.model.DateTimeValue)
      return new ConstantExpression(
          new DateTimeValue(((care.smith.top.model.DateTimeValue) value).getValue()));
    if (value instanceof care.smith.top.model.NumberValue)
      return new ConstantExpression(
          new DecimalValue(((care.smith.top.model.NumberValue) value).getValue()));
    return new ConstantExpression(
        new StringValue(((care.smith.top.model.StringValue) value).getValue()));
  }

  private static MathExpression getFunctionExpression(Expression exp) {
    FunctionExpression funcExp = new FunctionExpression(exp.getFunctionId());
    if (exp.getArguments() != null)
      for (Expression arg : exp.getArguments()) funcExp.addArgument(convert(arg));
    return funcExp;
  }

  public static Expression restrictionToExpression(Phenotype p) {
    if (!PhenotypeUtil.isRestriction(p)) return null;
    Restriction r = p.getRestriction();
    if (r == null) return null;

    Expression values = new Expression().entityId(p.getSuperPhenotype().getId());
    Expression range = getValuesAsExpression(r);
    Expression limits = getLimitsAsExpression(r);

    Expression in =
        new Expression()
            .functionId("in")
            .addArgumentsItem(values)
            .addArgumentsItem(range)
            .addArgumentsItem(limits);

    if (r.getQuantifier() != null) {
      in.addArgumentsItem(stringToExpression(r.getQuantifier().getValue()));
      if (r.getCardinality() != null) in.addArgumentsItem(numberToExpression(r.getCardinality()));
    }

    return in;
  }

  public static Expression getLimitsAsExpression(Restriction r) {
    List<Expression> limits = new ArrayList<>();
    if (r instanceof NumberRestriction) {
      NumberRestriction nr = (NumberRestriction) r;
      if (nr.getMinOperator() != null) limits.add(operatorToExpression(nr.getMinOperator()));
      if (nr.getMaxOperator() != null) limits.add(operatorToExpression(nr.getMaxOperator()));
    } else if (r instanceof DateTimeRestriction) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      if (dr.getMinOperator() != null) limits.add(operatorToExpression(dr.getMinOperator()));
      if (dr.getMaxOperator() != null) limits.add(operatorToExpression(dr.getMaxOperator()));
    }
    return new Expression().functionId("list").arguments(limits);
  }

  public static Expression getValuesAsExpression(Restriction r) {
    List<Expression> values = new ArrayList<>();
    if (r instanceof NumberRestriction)
      values =
          ((NumberRestriction) r)
              .getValues().stream().map(v -> numberToExpression(v)).collect(Collectors.toList());
    else if (r instanceof DateTimeRestriction)
      values =
          ((DateTimeRestriction) r)
              .getValues().stream().map(v -> dateTimeToExpression(v)).collect(Collectors.toList());
    else if (r instanceof BooleanRestriction)
      values =
          ((BooleanRestriction) r)
              .getValues().stream().map(v -> booleanToExpression(v)).collect(Collectors.toList());
    else
      values =
          ((StringRestriction) r)
              .getValues().stream().map(v -> stringToExpression(v)).collect(Collectors.toList());
    return new Expression().functionId("list").arguments(values);
  }

  public static Expression operatorToExpression(RestrictionOperator o) {
    return stringToExpression(o.getValue());
  }

  public static Expression valueToExpression(Value v) {
    return new Expression().value(v);
  }

  public static Expression stringToExpression(String v) {
    return valueToExpression(new care.smith.top.model.StringValue().value(v));
  }

  public static Expression numberToExpression(BigDecimal v) {
    return valueToExpression(new NumberValue().value(v));
  }

  public static Expression numberToExpression(Integer v) {
    return numberToExpression(BigDecimal.valueOf(v));
  }

  public static Expression booleanToExpression(Boolean v) {
    return valueToExpression(new care.smith.top.model.BooleanValue().value(v));
  }

  public static Expression dateTimeToExpression(LocalDateTime v) {
    return valueToExpression(new care.smith.top.model.DateTimeValue().value(v));
  }
}
