package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.model.DataType;
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
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

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
    Exceptions.checkArgumentsHaveSameType(getFunction(), args.subList(0, 2));

    if (args.get(1).getValues() != null) {
      Expression val = Aggregator.aggregate(args.get(0), defaultAggregateFunction, c2r);
      return ValueUtil.toExpression(valueInSet(val.getValue(), args.get(1).getValues()));
    }

    Restriction restr = args.get(1).getRestriction();
    if (RestrictionUtil.hasInterval(restr))
      return calculateInInterval(args.get(0).getValues(), restr);
    return calculateInSet(args.get(0).getValues(), RestrictionUtil.getValuesAsString(restr));
  }

  private Expression calculateInInterval(List<Value> values, Restriction restr) {
    int hits = 0;
    for (Value v : values) if (valueInInterval(v, range, limits)) hits++;
    return new BooleanValue(checkQuantifier(values.size(), hits, quantifier, quantifierValue));
  }

  public static boolean valueInInterval(Value value, List<Value> range, List<Value> limits) {
    for (int i = 0; i < range.size(); i++) {
      if (!checkLimit(value, range.get(i), limits.get(i))) return false;
    }
    return true;
  }

  private static boolean checkLimit(Value value, RestrictionOperator oper, Value limit) {
    BigDecimal val = ValueUtil.getNumberValue(value);
    BigDecimal lim = ValueUtil.getNumberValue(limit);
    if (oper == RestrictionOperator.GREATER_THAN && val.compareTo(lim) > 0) return true;
    if (oper == RestrictionOperator.GREATER_THAN_OR_EQUAL_TO && val.compareTo(lim) >= 0)
      return true;
    if (oper == RestrictionOperator.LESS_THAN && val.compareTo(lim) < 0) return true;
    if (oper == RestrictionOperator.LESS_THAN_OR_EQUAL_TO && val.compareTo(lim) <= 0) return true;
    return false;
  }

  private Expression calculateInSet(
      List<Value> values, List<Value> set, Quantifier quan, Integer card) {
    int hits = 0;
    for (Value v : values) if (valueInSet(v, Restriction)) hits++;
    return ValueUtil.toExpression(checkQuantifier(values.size(), hits, quan, card));
  }

  public static boolean valueInSet(Value value, List<Value> range) {
    for (Value member : range) {
      if (value.getDataType() == DataType.STRING) {
        if (ValueUtil.getStringValue(value).equals(ValueUtil.getStringValue(member))) return true;
      } else if (value.getDataType() == DataType.NUMBER) {
        if (ValueUtil.getNumberValue(value).compareTo(ValueUtil.getNumberValue(member)) == 0)
          return true;
      } else if (value.getDataType() == DataType.DATE_TIME) {
        if (ValueUtil.getDateTimeValue(value).equals(ValueUtil.getDateTimeValue(member)))
          return true;
      } else if (value.getDataType() == DataType.BOOLEAN) {
        if (ValueUtil.getBooleanValue(value).equals(ValueUtil.getBooleanValue(member))) return true;
      }
    }
    return false;
  }

  private boolean checkQuantifier(int size, int hits, Quantifier quan, Integer card) {
    if (quan == Quantifier.ALL && hits == size) return true;
    if (quan == Quantifier.EXACT && card != null && hits == card.intValue()) return true;
    if (quan == Quantifier.MIN && card != null && hits >= card.intValue()) return true;
    if (quan == Quantifier.MAX && card != null && hits <= card.intValue()) return true;
    return false;
  }
}
