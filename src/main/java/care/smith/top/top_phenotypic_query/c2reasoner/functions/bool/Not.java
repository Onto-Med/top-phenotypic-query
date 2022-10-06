package care.smith.top.simple_onto_api.calculator.functions.bool;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Not extends Function {

  private static Not instance = null;

  private Not() {
    super("not", "not", Notation.PREFIX);
    minArgumentsNumber(1);
    maxArgumentsNumber(1);
  }

  public static Not get() {
    if (instance == null) instance = new Not();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsContainLists(this, values);
    Exceptions.checkArgumentsType(this, Datatype.BOOLEAN, values);
    values = Aggregator.flatten(values);
    BooleanValue value = values.get(0).asBooleanValue();
    return new BooleanValue(!value.getValue());
  }
}
