package care.smith.top.simple_onto_api.calculator.functions.aggregate;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Min extends Function {

  private static Min instance = null;

  private Min() {
    super("min", "min", Notation.PREFIX);
    minArgumentsNumber(1);
  }

  public static Min get() {
    if (instance == null) instance = new Min();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DECIMAL, values);
    values = Aggregator.aggregateIfMultiple(values, defaultAggregateFunction);
    Value min = null;
    for (Value value : values) {
      if (min == null || value.getValueDecimal().compareTo(min.getValueDecimal()) < 0) min = value;
    }
    return min;
  }
}
