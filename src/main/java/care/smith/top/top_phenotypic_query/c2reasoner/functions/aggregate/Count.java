package care.smith.top.simple_onto_api.calculator.functions.aggregate;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Count extends Function {

  private static Count instance = null;

  private Count() {
    super("count", "count", Notation.PREFIX);
    minArgumentsNumber(1);
    maxArgumentsNumber(1);
  }

  public static Count get() {
    if (instance == null) instance = new Count();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    values = Aggregator.valueToList(values.get(0));
    return new DecimalValue(values.size());
  }
}
