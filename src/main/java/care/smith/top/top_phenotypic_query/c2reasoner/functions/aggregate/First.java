package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.List;
import java.util.stream.Collectors;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Values;

public class First extends FunctionEntity {

  private static final First INSTANCE = new First();

  private First() {
    super(
        new ExpressionFunction()
            .id("first")
            .title("first")
            .minArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static First get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    args = Aggregator.aggregateIfMultiple(args, defaultAggregateFunction, c2r);
    args = args.stream().sorted(Values.VALUE_DATE_COMPARATOR).collect(Collectors.toList());
    return args.get(0);
  }
}
