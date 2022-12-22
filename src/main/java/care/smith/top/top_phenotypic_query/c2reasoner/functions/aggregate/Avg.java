package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.ExpBuild;

public class Avg extends FunctionEntity {

  private static final Avg INSTANCE = new Avg();

  private Avg() {
    super(
        new ExpressionFunction()
            .id("avg")
            .title("avg")
            .minArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static Avg get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);
    args = Aggregator.aggregateIfMultiple(args, defaultAggregateFunction, c2r);
    BigDecimal avg = BigDecimal.ZERO;
    for (Expression arg : args) avg = avg.add(Expressions.getNumberValue(arg), mc);
    return ExpBuild.of(avg.divide(new BigDecimal(args.size()), mc));
  }
}
