package care.smith.top.simple_onto_api.calculator.functions.advanced;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;

public class Restrict extends Function {

  private static Restrict instance = null;

  private Restrict() {
    super("restrict", "restrict", Function.Notation.PREFIX);
    minArgumentsNumber(2);
    maxArgumentsNumber(3);
  }

  public static Restrict get() {
    if (instance == null) instance = new Restrict();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentTypes(this, values.get(1), Datatype.DECIMAL, Datatype.DATE_TIME);

    List<Value> vals = Aggregator.valueToList(values.get(0));
    List<Value> range = Aggregator.valueToList(values.get(1));
    List<Value> limits = (values.size() > 2) ? Aggregator.valueToList(values.get(2)) : null;

    ValueList vl = ValueList.get(values.get(0).getDatatype());
    Datatype rangeDatatype = values.get(1).getDatatype();

    if (limits == null || limits.isEmpty()) {
      if (rangeDatatype == Datatype.DECIMAL) checkValueRange(vals, range, vl);
      else checkDateRange(vals, range, vl);
    } else {
      if (rangeDatatype == Datatype.DECIMAL) checkValueRange(vals, range, limits, vl);
      else checkDateRange(vals, range, limits, vl);
    }

    return vl;
  }

  private void checkValueRange(List<Value> vals, List<Value> range, ValueList vl) {
    for (Value v : vals) if (In.valueInSet(v, range)) vl.addValueCheked(v);
  }

  private void checkValueRange(
      List<Value> vals, List<Value> range, List<Value> limits, ValueList vl) {
    for (Value v : vals) if (In.valueInInterval(v, range, limits)) vl.addValueCheked(v);
  }

  private void checkDateRange(List<Value> vals, List<Value> range, ValueList vl) {
    for (Value v : vals)
      if (In.valueInSet(new DateTimeValue(v.getDateTime()), range)) vl.addValueCheked(v);
  }

  private void checkDateRange(
      List<Value> vals, List<Value> range, List<Value> limits, ValueList vl) {
    for (Value v : vals)
      if (In.valueInInterval(new DateTimeValue(v.getDateTime()), range, limits))
        vl.addValueCheked(v);
  }
}
