package care.smith.top.simple_onto_api.calculator.functions.advanced;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Switch extends Function {

  private static Switch instance = null;

  private Switch() {
    super("switch", "switch", Notation.PREFIX);
    minArgumentsNumber(2);
  }

  public static Switch get() {
    if (instance == null) instance = new Switch();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    if (values.size() % 2 == 0) return calculate(values, values.size(), null);
    else return calculate(values, values.size() - 1, values.get(values.size() - 1));
  }

  private Value calculate(List<Value> values, int lastValueNum, Value defaultValue) {
    for (int i = 0; i < lastValueNum; i += 2) {
      Exceptions.checkArgumentType(this, Datatype.BOOLEAN, values.get(i));
      if (values.get(i).asBooleanValue().getValue()) return values.get(i + 1);
    }
    if (defaultValue != null) return defaultValue;
    throw new ArithmeticException("No default value defined for the function 'switch'!");
  }
}
