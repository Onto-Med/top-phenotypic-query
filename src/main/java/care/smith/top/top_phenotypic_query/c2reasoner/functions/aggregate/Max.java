package care.smith.top.simple_onto_api.calculator.functions.aggregate;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Max extends Function {

  private static Max instance = null;

  private Max() {
    super("max", "max", Notation.PREFIX);
    minArgumentsNumber(1);
  }

  public static Max get() {
    if (instance == null) instance = new Max();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DECIMAL, values);
    values = Aggregator.aggregateIfMultiple(values, defaultAggregateFunction);
    Value max = null;
    for (Value value : values) {
      if (max == null || value.getValueDecimal().compareTo(max.getValueDecimal()) > 0) max = value;
    }
    return max;
  }
}
