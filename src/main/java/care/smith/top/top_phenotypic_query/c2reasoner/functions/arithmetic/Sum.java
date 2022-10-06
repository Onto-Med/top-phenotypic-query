package care.smith.top.simple_onto_api.calculator.functions.arithmetic;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Sum extends Function {

  private static Sum instance = null;

  private Sum() {
    super("sum", "sum", Notation.PREFIX);
    minArgumentsNumber(1);
  }

  public static Sum get() {
    if (instance == null) instance = new Sum();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DECIMAL, values);
    values = Aggregator.aggregateIfMultiple(values, defaultAggregateFunction);
    BigDecimal result = BigDecimal.ZERO;
    for (Value value : values) result = result.add(value.getValueDecimal(), mc);
    return new DecimalValue(result);
  }
}
