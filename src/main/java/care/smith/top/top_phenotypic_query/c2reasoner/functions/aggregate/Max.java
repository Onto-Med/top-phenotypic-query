package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;

public class Max extends FunctionEntity {

  private static final Max INSTANCE = new Max();

  private Max() {
    super(
        new ExpressionFunction()
            .id("max")
            .title("max")
            .minArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static Max get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);
    args = Aggregator.aggregateIfMultiple(args, defaultAggregateFunction, c2r);
    Expression max = null;
    for (Expression arg : args) {
      if (max == null
          || ExpressionUtil.getValueNumber(arg).compareTo(ExpressionUtil.getValueNumber(max)) > 0)
        max = arg;
    }
    return max;
  }
}
