package care.smith.top.simple_onto_api.calculator.functions.bool;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class And extends Function {

  private static And instance = null;

  private And() {
    super("and", "and", Notation.PREFIX);
    minArgumentsNumber(2);
  }

  public static And get() {
    if (instance == null) instance = new And();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsContainLists(this, values);
    Exceptions.checkArgumentsType(this, Datatype.BOOLEAN, values);
    values = Aggregator.flatten(values);
    for (Value value : values) {
      if (!value.asBooleanValue().getValue()) return new BooleanValue(false);
    }
    return new BooleanValue(true);
  }
}
