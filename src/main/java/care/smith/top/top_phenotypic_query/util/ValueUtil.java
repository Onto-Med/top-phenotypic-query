package care.smith.top.top_phenotypic_query.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import care.smith.top.model.BooleanValue;
import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeValue;
import care.smith.top.model.Expression;
import care.smith.top.model.NumberValue;
import care.smith.top.model.StringValue;
import care.smith.top.model.Value;

public class ValueUtil {

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

  public static Value toValue(Boolean val) {
    return new BooleanValue().value(val).dataType(DataType.BOOLEAN);
  }

  public static Value toValue(Number val) {
    return new NumberValue().value(toDecimal(val)).dataType(DataType.NUMBER);
  }

  public static Value toValue(String val) {
    return new StringValue().value(val).dataType(DataType.STRING);
  }

  public static Value toValue(LocalDateTime val) {
    return new DateTimeValue().value(val).dataType(DataType.DATE_TIME);
  }

  public static List<Value> toBooleanValues(List<Boolean> vals) {
    return vals.stream().map(v -> toValue(v)).collect(Collectors.toList());
  }

  public static List<Value> toNumberValues(List<BigDecimal> vals) {
    return vals.stream().map(v -> toValue(v)).collect(Collectors.toList());
  }

  public static List<Value> toStringValues(List<String> vals) {
    return vals.stream().map(v -> toValue(v)).collect(Collectors.toList());
  }

  public static List<Value> toDateTimeValues(List<LocalDateTime> vals) {
    return vals.stream().map(v -> toValue(v)).collect(Collectors.toList());
  }

  public static List<Value> toValueList(Number... nums) {
    return Stream.of(nums).map(n -> toValue(n)).collect(Collectors.toList());
  }

  public static Expression toExpression(boolean val) {
    return toExpression(toValue(val));
  }

  public static Value getValueTrue() {
    return toValue(true);
  }

  public static Value getValueFalse() {
    return toValue(false);
  }

  public static boolean hasValueTrue(Expression exp) {
    return ((BooleanValue) exp.getValue()).isValue();
  }

  public static boolean hasValueFalse(Expression exp) {
    return !hasValueTrue(exp);
  }

  public static Expression getExpressionTrue() {
    return toExpression(getValueTrue());
  }

  public static Expression getExpressionFalse() {
    return toExpression(getValueFalse());
  }

  public static Value toValue(Number num, LocalDateTime dateTime) {
    return toValue(num).dateTime(dateTime);
  }

  public static Expression toExpression(Value val) {
    return new Expression().value(val);
  }

  public static Expression toExpression(List<Value> vals) {
    return new Expression().values(vals);
  }

  public static List<Expression> toExpressionList(List<Value> vals) {
    return vals.stream().map(v -> toExpression(v)).collect(Collectors.toList());
  }

  public static Expression toExpression(Value... vals) {
    return toExpression(List.of(vals));
  }

  public static Expression toExpression(Number num, LocalDateTime dateTime) {
    return toExpression(toValue(num, dateTime));
  }

  public static Expression toExpression(Number num) {
    return toExpression(toValue(num));
  }

  public static Expression toExpression(Number... numbers) {
    return toExpression(toValueList(numbers));
  }

  private static String addDateTime(String str, Value val) {
    if (val.getDateTime() != null) str += "|" + toStringDateTime(val);
    return str;
  }

  public static String toStringDateTime(Value val) {
    return DateUtil.format(val.getDateTime());
  }

  public static String toString(Value val) {
    return addDateTime(toStringValue(val), val);
  }

  public static String toString(List<Value> vals) {
    return vals.stream().map(v -> toString(v)).collect(Collectors.toList()).toString();
  }

  public static String toStringValue(Value val) {
    if (val instanceof NumberValue) return toStringValue((NumberValue) val);
    if (val instanceof DateTimeValue) return toStringValue((DateTimeValue) val);
    if (val instanceof BooleanValue) return toStringValue((BooleanValue) val);
    return toStringValue((StringValue) val);
  }

  public static String toStringValue(StringValue val) {
    return val.getValue();
  }

  public static String toStringValue(BooleanValue val) {
    return val.isValue().toString();
  }

  public static String toStringValue(NumberValue val) {
    return val.getValue().toPlainString();
  }

  public static String toStringValue(DateTimeValue val) {
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
}
