package care.smith.top.simple_onto_api.calculator.functions.date_time;

import java.time.LocalDateTime;
import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class PlusDays extends Function {

  private static PlusDays instance = null;

  private PlusDays() {
    super("plusDays", "plusDays", Notation.PREFIX);
    minArgumentsNumber(2);
    maxArgumentsNumber(2);
  }

  public static PlusDays get() {
    if (instance == null) instance = new PlusDays();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentType(this, Datatype.DATE_TIME, values.get(0));
    Exceptions.checkArgumentType(this, Datatype.DECIMAL, values.get(1));
    LocalDateTime start = values.get(0).asDateTimeValue().getValue();
    long days = values.get(1).getValueDecimal().longValue();
    return new DateTimeValue(start.plusDays(days));
  }
}
