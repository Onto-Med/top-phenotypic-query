package care.smith.top.simple_onto_api.calculator.functions.advanced;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Empty extends Function {

  private static Empty instance = null;

  private Empty() {
    super("empty", "empty", Notation.PREFIX);
    minArgumentsNumber(1);
    maxArgumentsNumber(1);
  }

  public static Empty get() {
    if (instance == null) instance = new Empty();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    return new BooleanValue(values.get(0).isValueList() && values.get(0).asValueList().isEmpty());
  }
}
