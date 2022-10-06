package care.smith.top.simple_onto_api.calculator.functions.advanced;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class In extends Function {

  private static In instance = null;

  private In() {
    super("in", "in", Function.Notation.PREFIX);
    minArgumentsNumber(3);
    maxArgumentsNumber(5);
  }

  public static In get() {
    if (instance == null) instance = new In();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsHaveSameType(this, values.subList(0, 2));

    List<Value> vals;
    List<Value> range = Aggregator.valueToList(values.get(1));
    List<Value> limits = Aggregator.valueToList(values.get(2));
    Value quantifier = new StringValue("some");
    Value quantifierValue = null;

    if (values.size() > 3) {
      vals = Aggregator.valueToList(values.get(0));
      quantifier = values.get(3);
      if (values.size() > 4) quantifierValue = values.get(4);
    } else vals = List.of(Aggregator.aggregate(values.get(0), defaultAggregateFunction));

    if (limits.isEmpty()) return calculateInSet(vals, range, quantifier, quantifierValue);
    return calculateInInterval(vals, range, limits, quantifier, quantifierValue);
  }

  private Value calculateInInterval(
      List<Value> values,
      List<Value> range,
      List<Value> limits,
      Value quantifier,
      Value quantifierValue) {
    int hits = 0;
    for (Value v : values) if (valueInInterval(v, range, limits)) hits++;
    return new BooleanValue(checkQuantifier(values.size(), hits, quantifier, quantifierValue));
  }

  public static boolean valueInInterval(Value value, List<Value> range, List<Value> limits) {
    for (int i = 0; i < range.size(); i++) {
      if (!checkLimit(value, range.get(i), limits.get(i))) return false;
    }
    return true;
  }

  private static boolean checkLimit(Value value, Value limitValue, Value limit) {
    BigDecimal v = value.getValueDecimal();
    BigDecimal lv = limitValue.getValueDecimal();
    String l = limit.getValueString();
    if (("gt".equals(l) || ">".equals(l)) && v.compareTo(lv) > 0) return true;
    if (("ge".equals(l) || ">=".equals(l)) && v.compareTo(lv) >= 0) return true;
    if (("lt".equals(l) || "<".equals(l)) && v.compareTo(lv) < 0) return true;
    if (("le".equals(l) || "<=".equals(l)) && v.compareTo(lv) <= 0) return true;
    return false;
  }

  private Value calculateInSet(
      List<Value> values, List<Value> range, Value quantifier, Value quantifierValue) {
    int hits = 0;
    for (Value v : values) if (valueInSet(v, range)) hits++;
    return new BooleanValue(checkQuantifier(values.size(), hits, quantifier, quantifierValue));
  }

  public static boolean valueInSet(Value value, List<Value> range) {
    for (Value member : range) {
      if (value.hasStringDatatype()) {
        if (value.getValueString().equals(member.getValueString())) return true;
      } else if (value.getValueDecimal().compareTo(member.getValueDecimal()) == 0) return true;
    }
    return false;
  }

  private boolean checkQuantifier(int size, int hits, Value quantifier, Value quantifierValue) {
    String q = quantifier.getValueString();
    if ("some".equals(q) && hits > 0) return true;
    if ("all".equals(q) && hits == size) return true;
    if ("exact".equals(q)
        && quantifierValue != null
        && hits == quantifierValue.getValueDecimal().intValue()) return true;
    if ("min".equals(q)
        && quantifierValue != null
        && hits >= quantifierValue.getValueDecimal().intValue()) return true;
    if ("max".equals(q)
        && quantifierValue != null
        && hits <= quantifierValue.getValueDecimal().intValue()) return true;
    return false;
  }
}
