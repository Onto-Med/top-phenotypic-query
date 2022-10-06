package care.smith.top.simple_onto_api.calculator.functions.advanced;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.model.property.data.value.list.StringValueList;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;

public class Li extends Function {

  private static Li instance = null;

  private Li() {
    super("list", "list", Notation.PREFIX);
  }

  public static Li get() {
    if (instance == null) instance = new Li();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    if (values.isEmpty()) return new StringValueList();
    values = Aggregator.aggregate(values, defaultAggregateFunction);
    return ValueList.get(values);
  }
}
