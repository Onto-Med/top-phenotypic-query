package care.smith.top.top_phenotypic_query.util;

import java.util.HashSet;
import java.util.Set;

import care.smith.top.backend.model.Constant;
import care.smith.top.backend.model.Expression;
import care.smith.top.backend.model.Value;
import care.smith.top.simple_onto_api.calculator.expressions.ConstantExpression;
import care.smith.top.simple_onto_api.calculator.expressions.FunctionExpression;
import care.smith.top.simple_onto_api.calculator.expressions.MathExpression;
import care.smith.top.simple_onto_api.calculator.expressions.VariableExpression;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;

public class ExpressionUtil {

  public static Set<String> getVariables(Expression exp) {
    Set<String> vars = new HashSet<>();
    addVariables(exp, vars);
    return vars;
  }

  private static void addVariables(Expression exp, Set<String> vars) {
    if (exp.getEntityId() != null) vars.add(exp.getEntityId());
    else if (exp.getArguments() != null)
      for (Expression arg : exp.getArguments()) addVariables(arg, vars);
  }

  public static MathExpression convert(Expression exp) {
    if (exp.getValue() != null)
      return getConstantExpression(exp.getValue().getConstant(), exp.getValue().getValue());
    if (exp.getFunction() != null) return getFunctionExpression(exp);
    return new VariableExpression(exp.getEntityId());
  }

  private static MathExpression getConstantExpression(Constant constant, Value value) {
    if (constant != null) return new ConstantExpression(constant.getId());
    if (value instanceof care.smith.top.backend.model.BooleanValue)
      return new ConstantExpression(
          new BooleanValue(((care.smith.top.backend.model.BooleanValue) value).isValue()));
    if (value instanceof care.smith.top.backend.model.DateTimeValue)
      return new ConstantExpression(
          new DateTimeValue(((care.smith.top.backend.model.DateTimeValue) value).getValue()));
    if (value instanceof care.smith.top.backend.model.NumberValue)
      return new ConstantExpression(
          new DecimalValue(((care.smith.top.backend.model.NumberValue) value).getValue()));
    return new ConstantExpression(
        new StringValue(((care.smith.top.backend.model.StringValue) value).getValue()));
  }

  private static MathExpression getFunctionExpression(Expression exp) {
    FunctionExpression funcExp = new FunctionExpression(exp.getFunction());
    for (Expression arg : exp.getArguments()) funcExp.addArgument(convert(arg));
    return funcExp;
  }
}
