package care.smith.top.simple_onto_api.calculator.functions.bool;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class MinTrue extends Function {

  private static MinTrue instance = null;

  private MinTrue() {
    super("minTrue", "minTrue", Notation.PREFIX);
    minArgumentsNumber(2);
  }

  public static MinTrue get() {
    if (instance == null) instance = new MinTrue();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentType(this, Datatype.DECIMAL, values.get(0));
    long minTrue = values.get(0).getValueDecimal().longValue();
    long count =
        values.stream()
            .skip(1)
            .filter(
                v -> v.hasBooleanDatatype() && !v.isValueList() && v.asBooleanValue().getValue())
            .count();
    return new BooleanValue(minTrue <= count);
  }
}
