package care.smith.top.top_phenotypic_query.util.builder;

import java.math.BigDecimal;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.NumberRestriction;
import care.smith.top.model.Quantifier;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;

public class NumberInterval extends Interval {

  private BigDecimal minValue;
  private BigDecimal maxValue;

  public NumberInterval() {}

  public NumberInterval(Quantifier quantifier) {
    super(quantifier);
  }

  public NumberInterval(Quantifier quantifier, Integer cardinality) {
    super(quantifier, cardinality);
  }

  public NumberInterval limit(Expression oper, Expression val) {
    if (val == null) return this;
    return limit(getOperator(oper), Expressions.getNumberValue(val));
  }

  public NumberInterval limit(RestrictionOperator oper, Number val) {
    if (val == null) return this;
    return limit(oper, Values.toDecimal(val));
  }

  public NumberInterval limit(RestrictionOperator oper, BigDecimal val) {
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

  public NumberRestriction get() {
    return (NumberRestriction)
        new NumberRestriction()
            .minOperator(minOperator)
            .maxOperator(maxOperator)
            .addValuesItem(minValue)
            .addValuesItem(maxValue)
            .type(DataType.NUMBER)
            .cardinality(cardinality)
            .quantifier(quantifier);
  }
}
