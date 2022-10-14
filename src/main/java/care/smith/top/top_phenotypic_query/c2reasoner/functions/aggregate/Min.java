package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;

public class Min extends FunctionEntity {

  private static final Min INSTANCE = new Min();

  private Min() {
    super(
        new ExpressionFunction()
            .id("min")
            .title("min")
            .minArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static Min get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);
    args = Aggregator.aggregateIfMultiple(args, defaultAggregateFunction, c2r);
    Expression min = null;
    for (Expression arg : args) {
      if (min == null
          || Expressions.getNumberValue(arg).compareTo(Expressions.getNumberValue(min)) < 0)
        min = arg;
    }
    return min;
  }
}
