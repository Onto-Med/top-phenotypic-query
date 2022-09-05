package care.smith.top.top_phenotypic_query.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import care.smith.top.backend.model.BooleanRestriction;
import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.NumberRestriction;
import care.smith.top.backend.model.Restriction;
import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.backend.model.StringRestriction;
import care.smith.top.simple_onto_api.util.DateUtil;
import care.smith.top.simple_onto_api.util.StringUtil;

public class RestrictionUtil {

  public static StringBuffer toString(DateTimeRestriction dtr) {
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
      copy.cardinality(r.getCardinality())
          .negated(r.isNegated())
          .quantifier(r.getQuantifier())
          .type(r.getType());

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
    List<OffsetDateTime> dateValues = new ArrayList<>();
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
          if (dv.isPresent()) dateValues.add(DateUtil.convert(dv.get()));
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
}
