package care.smith.top.simple_onto_api.calculator.functions.comparison;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Ge extends Function {

  private static Ge instance = null;

  private Ge() {
    super("ge", ">=", Notation.INFIX);
    minArgumentsNumber(2);
    maxArgumentsNumber(2);
  }

  public static Ge get() {
    if (instance == null) instance = new Ge();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsHaveSameType(this, values);
    values = Aggregator.aggregate(values, defaultAggregateFunction);
    BigDecimal v1 = values.get(0).getValueDecimal();
    BigDecimal v2 = values.get(1).getValueDecimal();
    return new BooleanValue(v1.compareTo(v2) >= 0);
  }
}
