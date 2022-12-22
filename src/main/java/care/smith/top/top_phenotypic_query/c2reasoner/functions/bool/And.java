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

public class And extends FunctionEntity {

  private static And INSTANCE = new And();

  private And() {
    super(
        new ExpressionFunction()
            .id("and")
            .title("and")
            .minArgumentNumber(2)
            .notation(NotationEnum.PREFIX));
  }

  public static And get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    for (Expression arg : args) {
      arg = c2r.calculate(arg, defaultAggregateFunction);
      arg = Aggregator.aggregate(arg, defaultAggregateFunction, c2r);
      Exceptions.checkArgumentHasValueOfType(getFunction(), DataType.BOOLEAN, arg);
      if (Expressions.hasValueFalse(arg)) return arg;
    }
    return Exp.ofTrue();
  }
}
