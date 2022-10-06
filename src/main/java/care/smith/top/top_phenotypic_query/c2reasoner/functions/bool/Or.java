package care.smith.top.simple_onto_api.calculator.functions.bool;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Or extends Function {

  private static Or instance = null;

  private Or() {
    super("or", "or", Notation.PREFIX);
    minArgumentsNumber(2);
  }

  public static Or get() {
    if (instance == null) instance = new Or();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsContainLists(this, values);
    Exceptions.checkArgumentsType(this, Datatype.BOOLEAN, values);
    values = Aggregator.flatten(values);
    for (Value value : values) {
      if (value.asBooleanValue().getValue()) return new BooleanValue(true);
    }
    return new BooleanValue(false);
  }
}
