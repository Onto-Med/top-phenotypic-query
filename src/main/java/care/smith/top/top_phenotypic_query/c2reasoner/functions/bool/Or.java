package care.smith.top.top_phenotypic_query.c2reasoner.functions.bool;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class Or extends FunctionEntity {

  private static Or INSTANCE = new Or();

  private Or() {
    super("or", NotationEnum.PREFIX);
    minArgumentNumber(2);
  }

  public static Or get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype... args) {
    return of(Exp.toList(args));
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    for (Expression arg : args) {
      arg = c2r.calculate(arg, defaultAggregateFunction);
      arg = Aggregator.aggregate(arg, defaultAggregateFunction, c2r);
      Exceptions.checkArgumentHasValueOfType(getFunction(), DataType.BOOLEAN, arg);
      if (Expressions.hasValueTrue(arg)) return arg;
    }
    return Exp.ofFalse();
  }
}
