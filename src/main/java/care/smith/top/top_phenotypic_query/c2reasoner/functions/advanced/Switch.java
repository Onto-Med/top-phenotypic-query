package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;

public class Switch extends FunctionEntity {

  private static Switch INSTANCE = new Switch();

  private Switch() {
    super(
        new ExpressionFunction()
            .id("switch")
            .title("switch")
            .minArgumentNumber(2)
            .notation(NotationEnum.PREFIX));
  }

  public static Switch get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    if (args.size() % 2 == 0)
      return calculate(args, args.size(), null, defaultAggregateFunction, c2r);
    return calculate(
        args, args.size() - 1, args.get(args.size() - 1), defaultAggregateFunction, c2r);
  }

  private Expression calculate(
      List<Expression> args,
      int lastValueNum,
      Expression defaultValue,
      FunctionEntity defaultAggregateFunction,
      C2R c2r) {
    for (int i = 0; i < lastValueNum; i += 2) {
      Expression cond = c2r.calculate(args.get(i), defaultAggregateFunction);
      Exceptions.checkArgumentType(getFunction(), DataType.BOOLEAN, cond);
      if (Expressions.getBooleanValue(cond))
        return c2r.calculate(args.get(i + 1), defaultAggregateFunction);
    }
    if (defaultValue != null) return defaultValue;
    throw new ArithmeticException("No default value defined for the function 'switch'!");
  }
}
