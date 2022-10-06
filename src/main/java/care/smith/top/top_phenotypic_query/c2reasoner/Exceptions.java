package care.smith.top.top_phenotypic_query.c2reasoner;

import java.util.List;
import java.util.Map;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.ConstantEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;

public class Exceptions {

  public static void checkArgumentsHaveSameType(Function f, List<Value> vals) {
    String msg = "The values for the function '%s' have different data types!";
    Datatype dt = null;
    for (Value v : vals) {
      if (dt != null && dt != v.getDatatype())
        throw new ArithmeticException(String.format(msg, f.getId()));
      else dt = v.getDatatype();
    }
  }

  public static void checkArgumentsType(Function f, Datatype dt, List<Value> vals) {
    for (Value v : vals) checkArgumentType(f, dt, v);
  }

  public static void checkArgumentType(Function f, Datatype dt, Value v) {
    String msg = "The value '%s' has a wrong data type for the function '%s'!";
    if (dt != v.getDatatype())
      throw new ArithmeticException(String.format(msg, v.getRepresentation(), f.getId()));
  }

  public static void checkArgumentTypes(Function f, Value v, Datatype... dts) {
    String msg = "The value '%s' has a wrong data type for the function '%s'!";
    for (Datatype dt : dts) if (dt == v.getDatatype()) return;
    throw new ArithmeticException(String.format(msg, v.getRepresentation(), f.getId()));
  }

  public static void checkArgumentsContainLists(Function f, List<Value> vals) {
    String msg =
        "The arguments of the function '%s' must be either single values or lists of single values!";
    for (Value v : vals) {
      if (v.isValueList() && v.asValueList().getValues().size() > 1)
        throw new ArithmeticException(String.format(msg, f.getId()));
    }
  }

  public static void checkArgumentsNumber(ExpressionFunction f, List<Expression> vals) {
    String msg = "The number of arguments (%s) is not suitable for the function '%s'!";
    if ((f.getMinArgumentNumber() != null && vals.size() < f.getMinArgumentNumber())
        || (f.getMaxArgumentNumber() != null && vals.size() > f.getMaxArgumentNumber()))
      throw new ArithmeticException(String.format(msg, vals.size(), f.getId()));
  }

  public static void checkVariableIsSet(Expression exp, Map<String, Expression> vars) {
    String msg = "Variable '%s' is not set!";
    if (!vars.containsKey(exp.getEntityId()))
      throw new ArithmeticException(String.format(msg, exp.getEntityId()));
  }

  public static void checkConstantExists(Expression exp, Map<String, ConstantEntity> constants) {
    String msg = "Constant '%s' does not exists!";
    if (!constants.containsKey(exp.getConstantId()))
      throw new ArithmeticException(String.format(msg, exp.getConstantId()));
  }

  public static void checkFunctionExists(Expression exp, Map<String, FunctionEntity> functions) {
    String msg = "Function '%s' does not exists!";
    if (!functions.containsKey(exp.getFunctionId()))
      throw new ArithmeticException(String.format(msg, exp.getFunctionId()));
  }
}
