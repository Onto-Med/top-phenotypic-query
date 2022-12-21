package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

import java.util.List;
import java.util.Map;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.ExpressionBuilder;

public class In extends FunctionEntity {

  private static final In INSTANCE = new In();

  private In() {
    super(
        new ExpressionFunction()
            .id("in")
            .title("in")
            .minArgumentNumber(2)
            .maxArgumentNumber(2)
            .notation(NotationEnum.PREFIX));
  }

  public static In get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsHaveSameType(getFunction(), args);

    if (args.get(1).getValues() != null) {
      Expression val = Aggregator.aggregate(args.get(0), defaultAggregateFunction, c2r);
      return ExpressionBuilder.of(Values.contains(args.get(1).getValues(), val.getValue()));
    }

    Restriction r = args.get(1).getRestriction();
    List<Value> vals = args.get(0).getValues();
    if (Restrictions.hasInterval(r))
      return calculateInInterval(
          vals, Restrictions.getInterval(r), r.getQuantifier(), r.getCardinality());
    return calculateInSet(vals, Restrictions.getValues(r), r.getQuantifier(), r.getCardinality());
  }

  private Expression calculateInInterval(
      List<Value> vals, Map<RestrictionOperator, Value> inter, Quantifier quan, Integer card) {
    int hits = 0;
    for (Value v : vals) if (Values.contains(inter, v)) hits++;
    return ExpressionBuilder.of(checkQuantifier(vals.size(), hits, quan, card));
  }

  private Expression calculateInSet(
      List<Value> vals, List<Value> set, Quantifier quan, Integer card) {
    int hits = 0;
    for (Value v : vals) if (Values.contains(set, v)) hits++;
    return ExpressionBuilder.of(checkQuantifier(vals.size(), hits, quan, card));
  }

  private boolean checkQuantifier(int size, int hits, Quantifier quan, Integer card) {
    if (quan == Quantifier.ALL && hits == size) return true;
    if (quan == Quantifier.EXACT && card != null && hits == card.intValue()) return true;
    if (quan == Quantifier.MIN && card != null && hits >= card.intValue()) return true;
    if (quan == Quantifier.MAX && card != null && hits <= card.intValue()) return true;
    return false;
  }
}
