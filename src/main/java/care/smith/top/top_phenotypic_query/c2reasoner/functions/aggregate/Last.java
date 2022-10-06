package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.Collections;
import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

public class Last extends FunctionEntity {

  private static final Last INSTANCE = new Last();

  private Last() {
    super(createFunction());
  }

  private static ExpressionFunction createFunction() {
    return new ExpressionFunction()
        .id("last")
        .title("last")
        .minArgumentNumber(1)
        .notation(NotationEnum.PREFIX);
  }

  public static Last get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = Aggregator.aggregateIfMultiple(args, defaultAggregateFunction, c2r);
    Collections.sort(args, ValueUtil.VALUE_DATE_COMPARATOR);
    return args.get(args.size() - 1);
  }
}
