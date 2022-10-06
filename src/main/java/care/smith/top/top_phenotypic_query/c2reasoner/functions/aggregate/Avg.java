package care.smith.top.simple_onto_api.calculator.functions.aggregate;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Avg extends Function {

  private static Avg instance = null;

  private Avg() {
    super("avg", "avg", Notation.PREFIX);
    minArgumentsNumber(1);
  }

  public static Avg get() {
    if (instance == null) instance = new Avg();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DECIMAL, values);
    values = Aggregator.aggregateIfMultiple(values, defaultAggregateFunction);
    BigDecimal avg = BigDecimal.ZERO;
    for (Value value : values) avg = avg.add(value.getValueDecimal(), mc);
    return new DecimalValue(avg.divide(new BigDecimal(values.size()), mc));
  }
}
