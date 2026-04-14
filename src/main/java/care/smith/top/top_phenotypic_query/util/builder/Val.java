package care.smith.top.top_phenotypic_query.util.builder;

import care.smith.top.model.BooleanValue;
import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeValue;
import care.smith.top.model.NumberValue;
import care.smith.top.model.StringValue;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Values;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.util.Strings;

public class Val {

  public static Value of(Boolean val) {
    return new BooleanValue().value(val).dataType(DataType.BOOLEAN);
  }

  public static Value of(Number val) {
    return new NumberValue().value(Values.toDecimal(val)).dataType(DataType.NUMBER);
  }

  public static Value of(BigDecimal val) {
    return new NumberValue().value(val).dataType(DataType.NUMBER);
  }

  public static Value of(String val) {
    return new StringValue().value(val).dataType(DataType.STRING);
  }

  public static Value of(LocalDateTime val) {
    return new DateTimeValue().value(val).dataType(DataType.DATE_TIME);
  }

  public static Value of(Boolean val, LocalDateTime dateTime) {
    return of(val).dateTime(dateTime);
  }

  public static Value of(Number val, LocalDateTime dateTime) {
    return of(val).dateTime(dateTime);
  }

  public static Value of(BigDecimal val, LocalDateTime dateTime) {
    return of(val).dateTime(dateTime);
  }

  public static Value of(String val, LocalDateTime dateTime) {
    return of(val).dateTime(dateTime);
  }

  public static Value of(LocalDateTime val, LocalDateTime dateTime) {
    return of(val).dateTime(dateTime);
  }

  public static Value of(Boolean val, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return of(val).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(Number val, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return of(val).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(BigDecimal val, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return of(val).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(String val, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return of(val).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(
      LocalDateTime val, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return of(val).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(
      Boolean val, LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return of(val).dateTime(dateTime).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(
      Number val, LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return of(val).dateTime(dateTime).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(
      BigDecimal val,
      LocalDateTime dateTime,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime) {
    return of(val).dateTime(dateTime).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(
      String val, LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return of(val).dateTime(dateTime).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(
      LocalDateTime val,
      LocalDateTime dateTime,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime) {
    return of(val).dateTime(dateTime).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value of(Map<String, String> record) {
    String s = record.get("start_date_time");
    String e = record.get("end_date_time");
    String d = record.get("date_time");

    LocalDateTime start = (Strings.isBlank(s)) ? null : DateUtil.parse(s);
    LocalDateTime end = (Strings.isBlank(e)) ? null : DateUtil.parse(e);
    LocalDateTime date = (Strings.isBlank(d)) ? null : DateUtil.parse(d);

    String v = null;
    if (!Strings.isBlank(v = record.get("boolean_value")))
      return of(Boolean.valueOf(v), date, start, end);
    if (!Strings.isBlank(v = record.get("number_value")))
      return of(new BigDecimal(v), date, start, end);
    if (!Strings.isBlank(v = record.get("date_time_value")))
      return of(DateUtil.parse(v), date, start, end);
    if (!Strings.isBlank(v = record.get("string_value"))) return of(v, date, start, end);

    return null;
  }

  public static Value ofTrue() {
    return of(true);
  }

  public static Value ofFalse() {
    return of(false);
  }

  public static Value ofTrue(LocalDateTime dateTime) {
    return ofTrue().dateTime(dateTime);
  }

  public static Value ofFalse(LocalDateTime dateTime) {
    return ofFalse().dateTime(dateTime);
  }

  public static Value ofTrue(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return ofTrue().startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value ofFalse(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return ofFalse().startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value ofTrue(
      LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return ofTrue().dateTime(dateTime).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static Value ofFalse(
      LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return ofFalse().dateTime(dateTime).startDateTime(startDateTime).endDateTime(endDateTime);
  }

  public static List<Value> ofBoolean(List<Boolean> vals) {
    return vals.stream().map(v -> of(v)).collect(Collectors.toList());
  }

  public static List<Value> ofNumber(List<BigDecimal> vals) {
    return vals.stream().map(v -> of(v)).collect(Collectors.toList());
  }

  public static List<Value> ofString(List<String> vals) {
    return vals.stream().map(v -> of(v)).collect(Collectors.toList());
  }

  public static List<Value> ofDateTime(List<LocalDateTime> vals) {
    return vals.stream().map(v -> of(v)).collect(Collectors.toList());
  }

  public static List<Value> of(String... strings) {
    return Stream.of(strings).map(s -> of(s)).collect(Collectors.toList());
  }

  public static List<Value> of(Number... nums) {
    return Stream.of(nums).map(n -> of(n)).collect(Collectors.toList());
  }

  public static Value of(Value v) {
    ObjectMapper om = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    try {
      return om.readValue(om.writeValueAsString(v), Value.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
