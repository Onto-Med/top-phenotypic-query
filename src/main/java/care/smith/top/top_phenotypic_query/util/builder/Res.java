package care.smith.top.top_phenotypic_query.util.builder;

import java.time.LocalDateTime;
import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.NumberRestriction;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.StringRestriction;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Values;

public class Res {

  public static Restriction ofCodes(Phenotype p) {
    return new StringRestriction()
        .values(Phenotypes.getOwnCodeUris(p))
        .quantifier(Quantifier.MIN)
        .cardinality(1)
        .type(DataType.STRING);
  }

  public static Restriction of(Integer card, Quantifier quan, Number... vals) {
    return new NumberRestriction()
        .values(Values.toDecimals(vals))
        .quantifier(quan)
        .cardinality(card)
        .type(DataType.NUMBER);
  }

  public static Restriction of(Number... vals) {
    return of(1, Quantifier.MIN, vals);
  }

  public static Restriction of(
      Integer card, Quantifier quan, RestrictionOperator oper, Number val) {
    NumberRestriction r = new NumberRestriction();
    if (oper == RestrictionOperator.GREATER_THAN
        || oper == RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
      r.minOperator(oper).addValuesItem(Values.toDecimal(val));
    else if (oper == RestrictionOperator.LESS_THAN
        || oper == RestrictionOperator.LESS_THAN_OR_EQUAL_TO)
      r.maxOperator(oper).addValuesItem(null).addValuesItem(Values.toDecimal(val));
    return r.quantifier(quan).cardinality(card).type(DataType.NUMBER);
  }

  public static Restriction of(RestrictionOperator oper, Number val) {
    return of(1, Quantifier.MIN, oper, val);
  }

  public static Restriction ge(Number val) {
    return of(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO, val);
  }

  public static Restriction gt(Number val) {
    return of(RestrictionOperator.GREATER_THAN, val);
  }

  public static Restriction le(Number val) {
    return of(RestrictionOperator.LESS_THAN_OR_EQUAL_TO, val);
  }

  public static Restriction lt(Number val) {
    return of(RestrictionOperator.LESS_THAN, val);
  }

  public static Restriction of(
      Integer card,
      Quantifier quan,
      RestrictionOperator minOper,
      Number minVal,
      RestrictionOperator maxOper,
      Number maxVal) {
    return new NumberRestriction()
        .minOperator(minOper)
        .maxOperator(maxOper)
        .addValuesItem(Values.toDecimal(minVal))
        .addValuesItem(Values.toDecimal(maxVal))
        .quantifier(quan)
        .cardinality(card)
        .type(DataType.NUMBER);
  }

  public static Restriction of(
      RestrictionOperator minOper, Number minVal, RestrictionOperator maxOper, Number maxVal) {
    return of(1, Quantifier.MIN, minOper, minVal, maxOper, maxVal);
  }

  public static Restriction gtLt(Number min, Number max) {
    return of(RestrictionOperator.GREATER_THAN, min, RestrictionOperator.LESS_THAN, max);
  }

  public static Restriction geLe(Number min, Number max) {
    return of(
        RestrictionOperator.GREATER_THAN_OR_EQUAL_TO,
        min,
        RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
        max);
  }

  public static Restriction gtLe(Number min, Number max) {
    return of(
        RestrictionOperator.GREATER_THAN, min, RestrictionOperator.LESS_THAN_OR_EQUAL_TO, max);
  }

  public static Restriction geLt(Number min, Number max) {
    return of(
        RestrictionOperator.GREATER_THAN_OR_EQUAL_TO, min, RestrictionOperator.LESS_THAN, max);
  }

  public static Restriction of(Quantifier quan, Number... vals) {
    return of(null, quan, vals);
  }

  public static Restriction of(Quantifier quan, RestrictionOperator oper, Number val) {
    return of(null, quan, oper, val);
  }

  public static Restriction of(
      Quantifier quan,
      RestrictionOperator minOper,
      Number minVal,
      RestrictionOperator maxOper,
      Number maxVal) {
    return of(null, quan, minOper, minVal, maxOper, maxVal);
  }

  public static Restriction of(Integer card, Quantifier quan, LocalDateTime... vals) {
    return new DateTimeRestriction()
        .values(List.of(vals))
        .quantifier(quan)
        .cardinality(card)
        .type(DataType.DATE_TIME);
  }

  public static Restriction of(LocalDateTime... vals) {
    return of(1, Quantifier.MIN, vals);
  }

  public static Restriction of(
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

  public static Restriction of(
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

  public static Restriction of(Quantifier quan, LocalDateTime... vals) {
    return of(null, quan, vals);
  }

  public static Restriction of(Quantifier quan, RestrictionOperator oper, LocalDateTime val) {
    return of(null, quan, oper, val);
  }

  public static Restriction of(
      Quantifier quan,
      RestrictionOperator minOper,
      LocalDateTime minVal,
      RestrictionOperator maxOper,
      LocalDateTime maxVal) {
    return of(null, quan, minOper, minVal, maxOper, maxVal);
  }
}
