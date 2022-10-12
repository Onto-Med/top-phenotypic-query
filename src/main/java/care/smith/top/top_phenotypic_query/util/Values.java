package care.smith.top.top_phenotypic_query.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import care.smith.top.model.BooleanValue;
import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeValue;
import care.smith.top.model.Expression;
import care.smith.top.model.NumberValue;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.StringValue;
import care.smith.top.model.Value;

public class Values {

  public static Comparator<Expression> VALUE_DATE_COMPARATOR =
      new Comparator<Expression>() {
        @Override
        public int compare(Expression e1, Expression e2) {
          if (e1.getValue() == null || e2.getValue() == null) return 0;
          LocalDateTime d1 = e1.getValue().getDateTime();
          LocalDateTime d2 = e2.getValue().getDateTime();
          if (d1 == null || d2 == null) return 0;
          return d1.compareTo(d2);
        }
      };

  public static BigDecimal toDecimal(Number num) {
    return new BigDecimal(num.toString());
  }

  public static List<BigDecimal> toDecimal(Number... nums) {
    return Stream.of(nums).map(n -> toDecimal(n)).collect(Collectors.toList());
  }

  public static Value newValue(Boolean val) {
    return new BooleanValue().value(val).dataType(DataType.BOOLEAN);
  }

  public static Value newValue(Number val) {
    return new NumberValue().value(toDecimal(val)).dataType(DataType.NUMBER);
  }

  public static Value newValue(BigDecimal val) {
    return new NumberValue().value(val).dataType(DataType.NUMBER);
  }

  public static Value newValue(String val) {
    return new StringValue().value(val).dataType(DataType.STRING);
  }

  public static Value newValue(LocalDateTime val) {
    return new DateTimeValue().value(val).dataType(DataType.DATE_TIME);
  }

  public static Value newValue(Boolean val, LocalDateTime dateTime) {
    return newValue(val).dateTime(dateTime);
  }

  public static Value newValue(Number val, LocalDateTime dateTime) {
    return newValue(val).dateTime(dateTime);
  }

  public static Value newValue(BigDecimal val, LocalDateTime dateTime) {
    return newValue(val).dateTime(dateTime);
  }

  public static Value newValue(String val, LocalDateTime dateTime) {
    return newValue(val).dateTime(dateTime);
  }

  public static Value newValue(LocalDateTime val, LocalDateTime dateTime) {
    return newValue(val).dateTime(dateTime);
  }

  public static Value newValueTrue() {
    return newValue(true);
  }

  public static Value newValueFalse() {
    return newValue(false);
  }

  public static List<Value> toBooleanValues(List<Boolean> vals) {
    return vals.stream().map(v -> newValue(v)).collect(Collectors.toList());
  }

  public static List<Value> toNumberValues(List<BigDecimal> vals) {
    return vals.stream().map(v -> newValue(v)).collect(Collectors.toList());
  }

  public static List<Value> toStringValues(List<String> vals) {
    return vals.stream().map(v -> newValue(v)).collect(Collectors.toList());
  }

  public static List<Value> toDateTimeValues(List<LocalDateTime> vals) {
    return vals.stream().map(v -> newValue(v)).collect(Collectors.toList());
  }

  public static List<Value> toValues(Number... nums) {
    return Stream.of(nums).map(n -> newValue(n)).collect(Collectors.toList());
  }

  public static String toString(Value val) {
    return addDateTime(toStringWithoutDateTime(val), val.getDateTime());
  }

  private static String addDateTime(String str, LocalDateTime dateTime) {
    return (dateTime == null) ? str : str + "|" + DateUtil.format(dateTime);
  }

  public static String toString(List<Value> vals) {
    return vals.stream().map(v -> toString(v)).collect(Collectors.toList()).toString();
  }

  public static String toStringWithoutDateTime(Value val) {
    if (val instanceof NumberValue) return toStringWithoutDateTime((NumberValue) val);
    if (val instanceof DateTimeValue) return toStringWithoutDateTime((DateTimeValue) val);
    if (val instanceof BooleanValue) return toStringWithoutDateTime((BooleanValue) val);
    return toStringWithoutDateTime((StringValue) val);
  }

  public static String toStringWithoutDateTime(StringValue val) {
    return val.getValue();
  }

  public static String toStringWithoutDateTime(BooleanValue val) {
    return val.isValue().toString();
  }

  public static String toStringWithoutDateTime(NumberValue val) {
    return val.getValue().toPlainString();
  }

  public static String toStringWithoutDateTime(DateTimeValue val) {
    return DateUtil.format(val.getValue());
  }

  public static String getStringValue(Value val) {
    return ((StringValue) val).getValue();
  }

  public static BigDecimal getNumberValue(Value val) {
    return ((NumberValue) val).getValue();
  }

  public static LocalDateTime getDateTimeValue(Value val) {
    return ((DateTimeValue) val).getValue();
  }

  public static Boolean getBooleanValue(Value val) {
    return ((BooleanValue) val).isValue();
  }

  public static boolean contains(List<Value> set, Value value) {
    for (Value member : set) {
      if (value.getDataType() == DataType.STRING) {
        if (getStringValue(value).equals(getStringValue(member))) return true;
      } else if (value.getDataType() == DataType.NUMBER) {
        if (getNumberValue(value).compareTo(getNumberValue(member)) == 0) return true;
      } else if (value.getDataType() == DataType.DATE_TIME) {
        if (getDateTimeValue(value).equals(getDateTimeValue(member))) return true;
      } else if (value.getDataType() == DataType.BOOLEAN) {
        if (getBooleanValue(value).equals(getBooleanValue(member))) return true;
      }
    }
    return false;
  }

  public static boolean contains(Map<RestrictionOperator, Value> interval, Value value) {
    for (RestrictionOperator oper : interval.keySet()) {
      if (!checkLimit(value, oper, interval.get(oper))) return false;
    }
    return true;
  }

  private static boolean checkLimit(Value val, RestrictionOperator oper, Value lim) {
    if (oper == RestrictionOperator.GREATER_THAN && compare(val, lim) > 0) return true;
    if (oper == RestrictionOperator.GREATER_THAN_OR_EQUAL_TO && compare(val, lim) >= 0) return true;
    if (oper == RestrictionOperator.LESS_THAN && compare(val, lim) < 0) return true;
    if (oper == RestrictionOperator.LESS_THAN_OR_EQUAL_TO && compare(val, lim) <= 0) return true;
    return false;
  }

  private static int compare(Value v1, Value v2) {
    if (v1.getDataType() == DataType.NUMBER && v2.getDataType() == DataType.NUMBER)
      return getNumberValue(v1).compareTo(getNumberValue(v2));
    if (v1.getDataType() == DataType.DATE_TIME && v2.getDataType() == DataType.DATE_TIME)
      return getDateTimeValue(v1).compareTo(getDateTimeValue(v2));
    if (v1.getDataType() == DataType.STRING && v2.getDataType() == DataType.STRING)
      return getStringValue(v1).compareTo(getStringValue(v2));
    if (v1.getDataType() == DataType.BOOLEAN && v2.getDataType() == DataType.BOOLEAN)
      return getBooleanValue(v1).compareTo(getBooleanValue(v2));
    return 0;
  }
}
