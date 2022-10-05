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

  public static StringBuffer toString(DateTimeRestriction dtr) {
    if (dtr == null) return new StringBuffer("[generally valid]");
    if (isEntirePeriod(dtr)) return new StringBuffer("[entire period]");
    StringBuffer sb = new StringBuffer("[ ");
    if (dtr.getMinOperator() != null)
      sb.append(dtr.getMinOperator()).append(dtr.getValues().get(0)).append(" ");
    if (dtr.getMaxOperator() != null)
      sb.append(dtr.getMaxOperator()).append(dtr.getValues().get(1)).append(" ");
    return sb.append("]");
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
        limits.put(
            format.formatOperator(nr.getMinOperator()), format.formatNumber(nr.getValues().get(0)));
        if (nr.getMaxOperator() != null)
          limits.put(
              format.formatOperator(nr.getMaxOperator()),
              format.formatNumber(nr.getValues().get(1)));
      } else if (nr.getMaxOperator() != null)
        limits.put(
            format.formatOperator(nr.getMaxOperator()), format.formatNumber(nr.getValues().get(0)));
    }
    if (r instanceof DateTimeRestriction) {
      DateTimeRestriction dr = (DateTimeRestriction) r;
      if (dr.getMinOperator() != null) {
        limits.put(
            format.formatOperator(dr.getMinOperator()),
            format.formatDateTime(dr.getValues().get(0)));
        if (dr.getMaxOperator() != null)
          limits.put(
              format.formatOperator(dr.getMaxOperator()),
              format.formatDateTime(dr.getValues().get(1)));
      } else if (dr.getMaxOperator() != null)
        limits.put(
            format.formatOperator(dr.getMaxOperator()),
            format.formatDateTime(dr.getValues().get(0)));
    }
    return limits;
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

  public static String getValuesAsString(Restriction r, DataAdapterFormat format) {
    if (r instanceof NumberRestriction)
      return format.formatList(
          ((NumberRestriction) r).getValues().stream().map(v -> format.formatNumber(v)));
    if (r instanceof DateTimeRestriction)
      return format.formatList(
          ((DateTimeRestriction) r).getValues().stream().map(v -> format.formatDateTime(v)));
    if (r instanceof BooleanRestriction)
      return format.formatList(
          ((BooleanRestriction) r).getValues().stream().map(v -> format.formatBoolean(v)));
    return format.formatList(
        ((StringRestriction) r).getValues().stream().map(v -> format.formatString(v)));
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
}
