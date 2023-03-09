package care.smith.top.top_phenotypic_query.util.builder;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.model.Quantifier;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.util.Values;

public class Interval {

  protected RestrictionOperator minOperator;
  protected RestrictionOperator maxOperator;
  protected Integer cardinality;
  protected Quantifier quantifier;

  protected Interval() {}

  protected Interval(Quantifier quantifier) {
    this.quantifier = quantifier;
  }

  protected Interval(Quantifier quantifier, Integer cardinality) {
    this.quantifier = quantifier;
    this.cardinality = cardinality;
  }

  protected RestrictionOperator getOperator(Expression exp) {
    if (exp == null) return null;
    List<Value> vals = exp.getValues();
    if (vals == null || vals.isEmpty()) return null;
    return RestrictionOperator.valueOf(Values.getStringValue(vals.get(0)));
  }
}
