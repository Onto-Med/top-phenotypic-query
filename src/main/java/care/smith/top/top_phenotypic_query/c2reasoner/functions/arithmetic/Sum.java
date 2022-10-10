package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

public class Sum extends FunctionEntity {

  private static Sum INSTANCE = new Sum();

  private Sum() {
    super(
        new ExpressionFunction()
            .id("sum")
            .title("sum")
            .minArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static Sum get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);
    args = Aggregator.aggregateIfMultiple(args, defaultAggregateFunction, c2r);
    BigDecimal result = BigDecimal.ZERO;
    for (Expression arg : args) result = result.add(ExpressionUtil.getValueNumber(arg), mc);
    Expression res = ValueUtil.toExpression(result);
    logResult(res);
    return res;
  }
}
