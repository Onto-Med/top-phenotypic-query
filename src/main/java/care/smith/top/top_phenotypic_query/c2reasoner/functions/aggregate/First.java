package care.smith.top.simple_onto_api.calculator.functions.aggregate;

import java.util.Collections;
import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class First extends Function {

  private static First instance = null;

  private First() {
    super("first", "first", Notation.PREFIX);
    minArgumentsNumber(1);
  }

  public static First get() {
    if (instance == null) instance = new First();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    values = Aggregator.aggregateIfMultiple(values, defaultAggregateFunction);
    Collections.sort(values);
    return values.get(0);
  }
}
