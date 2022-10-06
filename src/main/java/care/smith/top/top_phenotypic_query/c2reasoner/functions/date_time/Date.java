package care.smith.top.simple_onto_api.calculator.functions.date_time;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Date extends Function {

  private static Date instance = null;

  private Date() {
    super("date", "date", Notation.PREFIX);
    minArgumentsNumber(1);
    maxArgumentsNumber(1);
  }

  public static Date get() {
    if (instance == null) instance = new Date();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Value value = Aggregator.aggregate(values.get(0), defaultAggregateFunction);
    return new DateTimeValue(value.getDateTime());
  }
}
