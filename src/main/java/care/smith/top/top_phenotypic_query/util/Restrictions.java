package care.smith.top.top_phenotypic_query.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import care.smith.top.model.BooleanRestriction;
import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.NumberRestriction;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.StringRestriction;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterSettings;
import care.smith.top.top_phenotypic_query.ucum.UCUM;

public class Restrictions {

  public static Restriction newRestriction(Integer card, Quantifier quan, Number... vals) {
    return new NumberRestriction()
        .values(Values.newDecimals(vals))
        .quantifier(quan)
        .cardinality(card)
        .type(DataType.NUMBER);
  }

  public static Restriction newRestriction(
      Integer card, Quantifier quan, RestrictionOperator oper, Number val) {
    NumberRestriction r = new NumberRestriction();
    if (oper == RestrictionOperator.GREATER_THAN
        || oper == RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
      r.minOperator(oper).addValuesItem(Values.newDecimal(val));
    else if (oper == RestrictionOperator.LESS_THAN
        || oper == RestrictionOperator.LESS_THAN_OR_EQUAL_TO)
      r.maxOperator(oper).addValuesItem(null).addValuesItem(Values.newDecimal(val));
    return r.quantifier(quan).cardinality(card).type(DataType.NUMBER);
  }

  public static Restriction newRestriction(
      Integer card,
      Quantifier quan,
      RestrictionOperator minOper,
      Number minVal,
      RestrictionOperator maxOper,
      Number maxVal) {
    return new NumberRestriction()
        .minOperator(minOper)
        .maxOperator(maxOper)
        .addValuesItem(Values.newDecimal(minVal))
        .addValuesItem(Values.newDecimal(maxVal))
        .quantifier(quan)
        .cardinality(card)
        .type(DataType.NUMBER);
  }

  public static Restriction newRestriction(Quantifier quan, Number... vals) {
    return newRestriction(null, quan, vals);
  }

  public static Restriction newRestriction(Quantifier quan, RestrictionOperator oper, Number val) {
    return newRestriction(null, quan, oper, val);
  }

  public static Restriction newRestriction(
      Quantifier quan,
      RestrictionOperator minOper,
      Number minVal,
      RestrictionOperator maxOper,
      Number maxVal) {
    return newRestriction(null, quan, minOper, minVal, maxOper, maxVal);
  }

  public static Restriction newRestriction(Integer card, Quantifier quan, LocalDateTime... vals) {
    return new DateTimeRestriction()
        .values(List.of(vals))
        .quantifier(quan)
        .cardinality(card)
        .type(DataType.DATE_TIME);
  }

  public static Restriction newRestriction(
      Integer card, Quantifier quan, RestrictionOperator oper, LocalDateTime val) {
    DateTimeRestriction r = new DateTimeRestriction();
    if (oper == RestrictionOperator.GREATER_THAN
        || oper == RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
      r.minOperator(oper).addValuesItem(val);
    else if (oper == RestrictionOperator.LESS_THAN
        || oper == RestrictionOperator.LESS_THAN_OR_EQUAL_TO)
      r.maxOperator(oper).addValuesItem(null).addValuesItem(val);
    return r.quantifier(quan).cardinality(card).type(DataType.DATE_TIME);
  }

  public static Restriction newRestriction(
      Integer card,
      Quantifier quan,
      RestrictionOperator minOper,
      LocalDateTime minVal,
      RestrictionOperator maxOper,
      LocalDateTime maxVal) {
    return new DateTimeRestriction()
        .minOperator(minOper)
        .maxOperator(maxOper)
        .addValuesItem(minVal)
        .addValuesItem(maxVal)
        .quantifier(quan)
        .cardinality(card)
        .type(DataType.DATE_TIME);
  }

  public static Restriction newRestriction(Quantifier quan, LocalDateTime... vals) {
    return newRestriction(null, quan, vals);
  }

  public static Restriction newRestriction(
      Quantifier quan, RestrictionOperator oper, LocalDateTime val) {
    return newRestriction(null, quan, oper, val);
  }

  public static Restriction newRestriction(
      Quantifier quan,
      RestrictionOperator minOper,
      LocalDateTime minVal,
      RestrictionOperator maxOper,
      LocalDateTime maxVal) {
    return newRestriction(null, quan, minOper, minVal, maxOper, maxVal);
  }

  public static boolean hasNumberType(Restriction r) {
    if (r.getType() != null) return r.getType() == DataType.NUMBER;
    return r instanceof NumberRestriction;
  }

  public static boolean hasStringType(Restriction r) {
    if (r.getType() != null) return r.getType() == DataType.STRING;
    return r instanceof StringRestriction;
  }

  public static boolean hasBooleanType(Restriction r) {
    if (r.getType() != null) return r.getType() == DataType.BOOLEAN;
    return r instanceof BooleanRestriction;
  }

  public static boolean hasDateTimeType(Restriction r) {
    if (r.getType() != null) return r.getType() == DataType.DATE_TIME;
    return r instanceof DateTimeRestriction;
  }

  public static String toString(Restriction r) {
    if (r == null) return "|generally valid|";
    if (hasDateTimeType(r) && isEntirePeriod((DateTimeRestriction) r)) return "|entire period|";
    String s = "";
    if (r.getQuantifier() != null) s += "|" + r.getQuantifier().name();
    if (r.getCardinality() != null) s += "|" + r.getCardinality().intValue();
    if (hasInterval(r)) s += getIntervalAsString(r);
    else if (hasValues(r)) s += "|" + getValuesAsString(r);
    return s + "|";
  }

  private static Restriction copy(Restriction r, boolean withBasicAttributes) {
    Restriction copy = null;
    if (hasNumberType(r)) {
      NumberRestriction nr = (NumberRestriction) r;
      copy =
          new NumberRestriction()
              .maxOperator(nr.getMaxOperator())
              .minOperator(nr.getMinOperator())
              .values(nr.getValues());
    } else if (hasDateTimeType(r)) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      copy =
          new DateTimeRestriction()
              .maxOperator(dr.getMaxOperator())
              .minOperator(dr.getMinOperator())
              .values(dr.getValues());
    } else if (hasBooleanType(r)) {
      BooleanRestriction br = (BooleanRestriction) r;
      copy = new BooleanRestriction().values(br.getValues());
    } else {
      StringRestriction sr = (StringRestriction) r;
      copy = new StringRestriction().values(sr.getValues());
    }

    if (withBasicAttributes)
      copy.cardinality(r.getCardinality()).quantifier(r.getQuantifier()).type(r.getType());

    return copy;
  }

  public static Restriction copy(Restriction r) {
    return copy(r, true);
  }

  public static Restriction copyWithoutBasicAttributes(Restriction r) {
    return copy(r, false);
  }

  public static Restriction setType(Restriction r) {
    if (hasNumberType(r)) return r.type(DataType.NUMBER);
    if (hasDateTimeType(r)) return r.type(DataType.DATE_TIME);
    if (hasBooleanType(r)) return r.type(DataType.BOOLEAN);
    return r.type(DataType.STRING);
  }

  public static boolean hasInterval(Restriction r) {
    if (hasNumberType(r)) {
      NumberRestriction nr = (NumberRestriction) r;
      return nr.getMinOperator() != null || nr.getMaxOperator() != null;
    }
    if (hasDateTimeType(r)) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      return dr.getMinOperator() != null || dr.getMaxOperator() != null;
    }
    return false;
  }

  public static Map<String, String> getIntervalAsStringMap(
      Restriction r, DataAdapterSettings format) {
    Map<String, String> limits = new LinkedHashMap<>();
    if (hasNumberType(r)) {
      NumberRestriction nr = (NumberRestriction) r;
      if (nr.getMinOperator() != null)
        limits.put(format(nr.getMinOperator(), format), format(nr.getValues().get(0), format));
      if (nr.getMaxOperator() != null)
        limits.put(format(nr.getMaxOperator(), format), format(nr.getValues().get(1), format));
    }
    if (hasDateTimeType(r)) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      if (dr.getMinOperator() != null)
        limits.put(format(dr.getMinOperator(), format), format(dr.getValues().get(0), format));
      if (dr.getMaxOperator() != null)
        limits.put(format(dr.getMaxOperator(), format), format(dr.getValues().get(1), format));
    }
    return limits;
  }

  public static Map<String, String> getIntervalAsStringMap(Restriction r) {
    return getIntervalAsStringMap(r, null);
  }

  public static String getIntervalAsString(Restriction r) {
    String s = "";
    Map<String, String> in = getIntervalAsStringMap(r);
    for (String op : in.keySet()) s += "|" + op + " " + in.get(op);
    return s;
  }

  public static boolean hasValues(Restriction r) {
    if (hasNumberType(r)) return !ObjectUtils.isEmpty(getNumberValues(r));
    if (hasDateTimeType(r)) return !ObjectUtils.isEmpty(getDateTimeValues(r));
    if (hasBooleanType(r)) return !ObjectUtils.isEmpty(getBooleanValues(r));
    return !ObjectUtils.isEmpty(getStringValues(r));
  }

  public static String getValuesAsString(Restriction r) {
    return getValuesAsString(r, null);
  }

  public static String getValuesAsString(Restriction r, DataAdapterSettings format) {
    if (hasNumberType(r))
      return format(
          getNumberValues(r).stream().map(v -> format(v, format)).map(String.class::cast), format);
    if (hasDateTimeType(r))
      return format(
          getDateTimeValues(r).stream().map(v -> format(v, format)).map(String.class::cast),
          format);
    if (hasBooleanType(r))
      return format(
          getBooleanValues(r).stream().map(v -> format(v, format)).map(String.class::cast), format);
    return format(
        getStringValues(r).stream().map(v -> format(v, format)).map(String.class::cast), format);
  }

  public static int getValuesCount(Restriction r) {
    if (hasNumberType(r)) return removeNullValues(getNumberValues(r)).size();
    if (hasDateTimeType(r)) return removeNullValues(getDateTimeValues(r)).size();
    if (hasBooleanType(r)) return removeNullValues(getBooleanValues(r)).size();
    return removeNullValues(getStringValues(r)).size();
  }

  private static <T> List<T> removeNullValues(List<T> vals) {
    while (vals.remove(null))
      ;
    return vals;
  }

  public static Map<RestrictionOperator, Value> getInterval(Restriction r) {
    Map<RestrictionOperator, Value> limits = new LinkedHashMap<>();
    if (hasNumberType(r)) {
      NumberRestriction nr = (NumberRestriction) r;
      List<Value> nVals = Values.newNumberValues(nr.getValues());
      if (nr.getMinOperator() != null) limits.put(nr.getMinOperator(), nVals.get(0));
      if (nr.getMaxOperator() != null) limits.put(nr.getMaxOperator(), nVals.get(1));
    }
    if (hasDateTimeType(r)) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      List<Value> dVals = Values.newDateTimeValues(dr.getValues());
      if (dr.getMinOperator() != null) limits.put(dr.getMinOperator(), dVals.get(0));
      if (dr.getMaxOperator() != null) limits.put(dr.getMaxOperator(), dVals.get(1));
    }
    return limits;
  }

  public static List<Value> getValues(Restriction r) {
    if (hasNumberType(r)) return Values.newNumberValues(getNumberValues(r));
    if (hasDateTimeType(r)) return Values.newDateTimeValues(getDateTimeValues(r));
    if (hasBooleanType(r)) return Values.newBooleanValues(getBooleanValues(r));
    return Values.newStringValues(getStringValues(r));
  }

  public static List<String> getStringValues(Restriction r) {
    return ((StringRestriction) r).getValues();
  }

  public static List<BigDecimal> getNumberValues(Restriction r) {
    return ((NumberRestriction) r).getValues();
  }

  public static List<Boolean> getBooleanValues(Restriction r) {
    return ((BooleanRestriction) r).getValues();
  }

  public static List<LocalDateTime> getDateTimeValues(Restriction r) {
    return ((DateTimeRestriction) r).getValues();
  }

  public static boolean isEntirePeriod(DateTimeRestriction r) {
    return !hasInterval(r) && !hasValues(r);
  }

  public static BigDecimal getMinIntervalValue(NumberRestriction r) {
    if (r.getMinOperator() == null) return null;
    return r.getValues().get(0);
  }

  public static BigDecimal getMaxIntervalValue(NumberRestriction r) {
    if (r.getMaxOperator() == null) return null;
    return r.getValues().get(1);
  }

  private static String format(String val, DataAdapterSettings format) {
    return (format == null) ? val : format.formatString(val);
  }

  private static String format(Boolean val, DataAdapterSettings format) {
    return (format == null) ? val.toString() : format.formatBoolean(val);
  }

  private static String format(BigDecimal val, DataAdapterSettings format) {
    return (format == null) ? val.toPlainString() : format.formatNumber(val);
  }

  private static String format(LocalDateTime val, DataAdapterSettings format) {
    return (format == null) ? DateUtil.format(val) : format.formatDateTime(val);
  }

  private static String format(Stream<String> vals, DataAdapterSettings format) {
    return (format == null)
        ? vals.collect(Collectors.joining(", ", "[", "]"))
        : format.formatList(vals);
  }

  private static String format(RestrictionOperator op, DataAdapterSettings format) {
    return (format == null) ? op.getValue() : format.formatOperator(op);
  }

  public static Restriction getRestriction(List<String> list) {
    RestrictionOperator minOperator = null;
    RestrictionOperator maxOperator = null;
    List<BigDecimal> decimalValues = new ArrayList<>();
    List<LocalDateTime> dateValues = new ArrayList<>();
    List<Boolean> booleanValues = new ArrayList<>();
    List<String> stringValues = new ArrayList<>();

    for (String e : list) {
      if (RestrictionOperator.GREATER_THAN.getValue().equals(e))
        minOperator = RestrictionOperator.GREATER_THAN;
      else if (RestrictionOperator.GREATER_THAN_OR_EQUAL_TO.getValue().equals(e))
        minOperator = RestrictionOperator.GREATER_THAN_OR_EQUAL_TO;
      else if (RestrictionOperator.LESS_THAN.getValue().equals(e))
        maxOperator = RestrictionOperator.LESS_THAN;
      else if (RestrictionOperator.LESS_THAN_OR_EQUAL_TO.getValue().equals(e))
        maxOperator = RestrictionOperator.LESS_THAN_OR_EQUAL_TO;
      else {
        if (NumberUtils.isParsable(e)) decimalValues.add(new BigDecimal(e));
        else {
          Optional<LocalDateTime> dv = DateUtil.parseOptional(e);
          if (dv.isPresent()) dateValues.add(dv.get());
          else if (e.equalsIgnoreCase("true") || e.equalsIgnoreCase("false"))
            booleanValues.add(Boolean.parseBoolean(e));
          else stringValues.add(e);
        }
      }
    }

    if (!decimalValues.isEmpty()) {
      NumberRestriction nr =
          new NumberRestriction().minOperator(minOperator).maxOperator(maxOperator);
      if (minOperator == null && maxOperator != null)
        nr.setValues(List.of(null, decimalValues.get(0)));
      else nr.setValues(decimalValues);
      return nr;
    }
    if (!dateValues.isEmpty()) {
      DateTimeRestriction dr =
          new DateTimeRestriction().minOperator(minOperator).maxOperator(maxOperator);
      if (minOperator == null && maxOperator != null)
        dr.setValues(List.of(null, dateValues.get(0)));
      else dr.setValues(dateValues);
      return dr;
    }
    if (!booleanValues.isEmpty()) return new BooleanRestriction().values(booleanValues);
    return new StringRestriction().values(stringValues);
  }

  public static Restriction convertValues(Restriction r, String inUnit, String outUnit) {
    NumberRestriction nr = (NumberRestriction) r;
    List<BigDecimal> vals =
        nr.getValues().stream()
            .map(v -> UCUM.convert(v, inUnit, outUnit))
            .collect(Collectors.toList());
    return nr.values(vals);
  }
}
