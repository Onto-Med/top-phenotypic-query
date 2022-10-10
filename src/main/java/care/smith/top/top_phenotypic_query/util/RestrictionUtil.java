package care.smith.top.top_phenotypic_query.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;

import care.smith.top.model.BooleanRestriction;
import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.NumberRestriction;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.StringRestriction;
import care.smith.top.simple_onto_api.util.StringUtil;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterFormat;

public class RestrictionUtil {

  public static String toString(Restriction r) {
    if (r == null) return "|generally valid|";
    if (r instanceof DateTimeRestriction && isEntirePeriod((DateTimeRestriction) r))
      return "|entire period|";
    String s = "";
    if (r.getQuantifier() != null) s += "|" + r.getQuantifier().name();
    if (r.getCardinality() != null) s += "|" + r.getCardinality().intValue();
    if (hasInterval(r)) s += getIntervalAsString(r);
    else if (hasValues(r)) s += "|" + getValuesAsString(r);
    return s + "|";
  }

  private static Restriction copy(Restriction r, boolean withBasicAttributes) {
    Restriction copy = null;
    if (r instanceof NumberRestriction) {
      NumberRestriction nr = (NumberRestriction) r;
      copy =
          new NumberRestriction()
              .maxOperator(nr.getMaxOperator())
              .minOperator(nr.getMinOperator())
              .values(nr.getValues());
    } else if (r instanceof DateTimeRestriction) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      copy =
          new DateTimeRestriction()
              .maxOperator(dr.getMaxOperator())
              .minOperator(dr.getMinOperator())
              .values(dr.getValues());
    } else if (r instanceof BooleanRestriction) {
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
        Optional<BigDecimal> nv = StringUtil.parseNumber(e);
        if (nv.isPresent()) decimalValues.add(nv.get());
        else {
          Optional<LocalDateTime> dv = StringUtil.parseDate(e);
          if (dv.isPresent()) dateValues.add(dv.get());
          else {
            Optional<Boolean> bv = StringUtil.parseBoolean(e);
            if (bv.isPresent()) booleanValues.add(bv.get());
            else stringValues.add(e);
          }
        }
      }
    }

    if (!decimalValues.isEmpty())
      return new NumberRestriction()
          .values(decimalValues)
          .minOperator(minOperator)
          .maxOperator(maxOperator);
    if (!dateValues.isEmpty())
      return new DateTimeRestriction()
          .values(dateValues)
          .minOperator(minOperator)
          .maxOperator(maxOperator);
    if (!booleanValues.isEmpty()) return new BooleanRestriction().values(booleanValues);
    return new StringRestriction().values(stringValues);
  }

  public static Restriction setType(Restriction r) {
    if (r instanceof NumberRestriction) return r.type(DataType.NUMBER);
    if (r instanceof DateTimeRestriction) return r.type(DataType.DATE_TIME);
    if (r instanceof BooleanRestriction) return r.type(DataType.BOOLEAN);
    return r.type(DataType.STRING);
  }

  public static boolean hasInterval(Restriction r) {
    if (r instanceof NumberRestriction) {
      NumberRestriction nr = (NumberRestriction) r;
      return nr.getMinOperator() != null || nr.getMaxOperator() != null;
    }
    if (r instanceof DateTimeRestriction) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      return dr.getMinOperator() != null || dr.getMaxOperator() != null;
    }
    return false;
  }

  public static Map<String, String> getInterval(Restriction r, DataAdapterFormat format) {
    Map<String, String> limits = new HashMap<>();
    if (r instanceof NumberRestriction) {
      NumberRestriction nr = (NumberRestriction) r;
      if (nr.getMinOperator() != null) {
        limits.put(format(nr.getMinOperator(), format), format(nr.getValues().get(0), format));
        if (nr.getMaxOperator() != null)
          limits.put(format(nr.getMaxOperator(), format), format(nr.getValues().get(1), format));
      } else if (nr.getMaxOperator() != null)
        limits.put(format(nr.getMaxOperator(), format), format(nr.getValues().get(0), format));
    }
    if (r instanceof DateTimeRestriction) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      if (dr.getMinOperator() != null) {
        limits.put(format(dr.getMinOperator(), format), format(dr.getValues().get(0), format));
        if (dr.getMaxOperator() != null)
          limits.put(format(dr.getMaxOperator(), format), format(dr.getValues().get(1), format));
      } else if (dr.getMaxOperator() != null)
        limits.put(format(dr.getMaxOperator(), format), format(dr.getValues().get(0), format));
    }
    return limits;
  }

  public static Map<String, String> getInterval(Restriction r) {
    return getInterval(r, null);
  }

  public static String getIntervalAsString(Restriction r) {
    String s = "";
    Map<String, String> in = getInterval(r);
    for (String op : in.keySet()) s += "|" + op + " " + in.get(op);
    return s;
  }

  public static boolean hasValues(Restriction r) {
    if (r instanceof NumberRestriction)
      return !ObjectUtils.isEmpty(((NumberRestriction) r).getValues());
    if (r instanceof DateTimeRestriction)
      return !ObjectUtils.isEmpty(((DateTimeRestriction) r).getValues());
    if (r instanceof BooleanRestriction)
      return !ObjectUtils.isEmpty(((BooleanRestriction) r).getValues());
    return !ObjectUtils.isEmpty(((StringRestriction) r).getValues());
  }

  public static String getValuesAsString(Restriction r) {
    return getValuesAsString(r, null);
  }

  public static String getValuesAsString(Restriction r, DataAdapterFormat format) {
    if (r instanceof NumberRestriction)
      return format.formatList(
          ((NumberRestriction) r).getValues().stream().map(v -> format(v, format)));
    if (r instanceof DateTimeRestriction)
      return format.formatList(
          ((DateTimeRestriction) r).getValues().stream().map(v -> format(v, format)));
    if (r instanceof BooleanRestriction)
      return format.formatList(
          ((BooleanRestriction) r).getValues().stream().map(v -> format(v, format)));
    return format.formatList(
        ((StringRestriction) r).getValues().stream().map(v -> format(v, format)));
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
    if (r.getMinOperator() == null) return r.getValues().get(0);
    return r.getValues().get(1);
  }

  private static String format(String val, DataAdapterFormat format) {
    return (format == null) ? val : format.formatString(val);
  }

  private static String format(Boolean val, DataAdapterFormat format) {
    return (format == null) ? val.toString() : format.formatBoolean(val);
  }

  private static String format(BigDecimal val, DataAdapterFormat format) {
    return (format == null) ? val.toPlainString() : format.formatNumber(val);
  }

  private static String format(LocalDateTime val, DataAdapterFormat format) {
    return (format == null) ? DateUtil.format(val) : format.formatDateTime(val);
  }

  private static String format(RestrictionOperator op, DataAdapterFormat format) {
    return (format == null) ? op.getValue() : format.formatOperator(op);
  }
}
