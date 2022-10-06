package care.smith.top.simple_onto_api.calculator.functions.arithmetic;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Multiply extends Function {

  private static Multiply instance = null;

  private Multiply() {
    super("multiply", "*", Notation.INFIX);
    minArgumentsNumber(2);
    maxArgumentsNumber(2);
  }

  public static Multiply get() {
    if (instance == null) instance = new Multiply();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DECIMAL, values);
    values = Aggregator.aggregate(values, defaultAggregateFunction);
    return new DecimalValue(
        values.get(0).getValueDecimal().multiply(values.get(1).getValueDecimal(), mc));
  }
}
