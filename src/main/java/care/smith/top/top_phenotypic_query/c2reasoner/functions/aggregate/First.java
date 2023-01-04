package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.List;
import java.util.stream.Collectors;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class First extends FunctionEntity {

  private static final First INSTANCE = new First();

  private First() {
    super("first", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static First get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
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
