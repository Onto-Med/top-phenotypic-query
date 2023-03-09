package care.smith.top.top_phenotypic_query.util.builder;

import java.time.LocalDateTime;

import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Expression;
import care.smith.top.model.Quantifier;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Expressions;

public class DateTimeInterval extends Interval {

  private LocalDateTime minValue;
  private LocalDateTime maxValue;

  public DateTimeInterval() {}

  public DateTimeInterval(Quantifier quantifier) {
    super(quantifier);
  }

  public DateTimeInterval(Quantifier quantifier, Integer cardinality) {
    super(quantifier, cardinality);
  }

  public DateTimeInterval limit(Expression oper, Expression val) {
    if (val == null) return this;
    return limit(getOperator(oper), Expressions.getDateTimeValue(val));
  }

  public DateTimeInterval limit(RestrictionOperator oper, String val) {
    if (val == null) return this;
    return limit(oper, DateUtil.parse(val));
  }

  public DateTimeInterval limit(RestrictionOperator oper, LocalDateTime val) {
    if (val == null) return this;
    if (oper == RestrictionOperator.GREATER_THAN
        || oper == RestrictionOperator.GREATER_THAN_OR_EQUAL_TO) {
      minOperator = oper;
      minValue = val;
    } else {
      maxOperator = oper;
      maxValue = val;
    }
    return this;
  }

  public boolean isEmpty() {
    return (minOperator == null || minValue == null) && (maxOperator == null || maxValue == null);
  }

  public DateTimeRestriction get() {
    return (DateTimeRestriction)
        new DateTimeRestriction()
            .minOperator(minOperator)
            .maxOperator(maxOperator)
            .addValuesItem(minValue)
            .addValuesItem(maxValue)
            .type(DataType.NUMBER)
            .cardinality(cardinality)
            .quantifier(quantifier);
  }
}
