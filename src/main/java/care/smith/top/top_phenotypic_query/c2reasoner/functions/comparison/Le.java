package care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class Le extends FunctionEntity {

  private static Le INSTANCE = new Le();

  private Le() {
    super(
        new ExpressionFunction()
            .id("le")
            .title("<=")
            .minArgumentNumber(2)
            .maxArgumentNumber(2)
            .notation(NotationEnum.INFIX));
  }

  public static Le get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsHaveSameType(getFunction(), args);
    args = Aggregator.aggregate(args, defaultAggregateFunction, c2r);
    return Exp.of(
        Values.compare(args.get(0).getValue(), args.get(1).getValue()) <= 0);
  }
}
