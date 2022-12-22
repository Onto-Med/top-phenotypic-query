package care.smith.top.top_phenotypic_query.c2reasoner.functions.bool;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class MinTrue extends FunctionEntity {

  private static MinTrue INSTANCE = new MinTrue();

  private MinTrue() {
    super(
        new ExpressionFunction()
            .id("minTrue")
            .title("minTrue")
            .minArgumentNumber(2)
            .notation(NotationEnum.PREFIX));
  }

  public static MinTrue get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Exceptions.checkArgumentHasValueOfType(getFunction(), DataType.NUMBER, args.get(0));
    int min = Expressions.getNumberValue(args.get(0)).intValue();
    int count = 0;
    for (Expression arg : args.subList(1, args.size())) {
      arg = c2r.calculate(arg, defaultAggregateFunction);
      arg = Aggregator.aggregate(arg, defaultAggregateFunction, c2r);
      Exceptions.checkArgumentHasValueOfType(getFunction(), DataType.BOOLEAN, arg);
      if (Expressions.hasValueTrue(arg)) {
        count++;
        if (count >= min) return Exp.ofTrue();
      }
    }
    return Exp.ofFalse();
  }
}
