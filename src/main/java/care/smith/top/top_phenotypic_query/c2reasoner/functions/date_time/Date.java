package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;

public class Date extends FunctionEntity {

  private static Date INSTANCE = new Date();

  private Date() {
    super(
        new ExpressionFunction()
            .id("date")
            .title("date")
            .minArgumentNumber(1)
            .maxArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static Date get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression arg = c2r.calculate(args.get(0), defaultAggregateFunction);
    arg = Aggregator.aggregate(arg, defaultAggregateFunction, c2r);
    return Expressions.newExpression(arg.getValue().getDateTime());
  }
}
