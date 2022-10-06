package care.smith.top.simple_onto_api.calculator.functions.arithmetic;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Subtract extends Function {

  private static Subtract instance = null;

  private Subtract() {
    super("subtract", "-", Notation.INFIX);
    minArgumentsNumber(2);
    maxArgumentsNumber(2);
  }

  public static Subtract get() {
    if (instance == null) instance = new Subtract();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DECIMAL, values);
    values = Aggregator.aggregate(values, defaultAggregateFunction);
    return new DecimalValue(
        values.get(0).getValueDecimal().subtract(values.get(1).getValueDecimal(), mc));
  }
}
