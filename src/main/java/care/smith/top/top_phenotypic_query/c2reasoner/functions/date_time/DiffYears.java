package care.smith.top.simple_onto_api.calculator.functions.date_time;

import java.time.LocalDateTime;
import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.util.DateUtil;

public class DiffYears extends Function {

  private static DiffYears instance = null;

  private DiffYears() {
    super("diffYears", "diffYears", Notation.PREFIX);
    minArgumentsNumber(2);
    maxArgumentsNumber(2);
  }

  public static DiffYears get() {
    if (instance == null) instance = new DiffYears();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DATE_TIME, values);
    LocalDateTime start = values.get(0).asDateTimeValue().getValue();
    LocalDateTime end = values.get(1).asDateTimeValue().getValue();
    return new DecimalValue(DateUtil.getPeriodInYears(start, end));
  }
}
