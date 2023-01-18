package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class Min extends FunctionEntity {

  private static final Min INSTANCE = new Min();

  private Min() {
    super("min", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static Min get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args);
    Exceptions.checkArgumentsAreNotNull(getFunction(), args);
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);
    args = Aggregator.aggregateIfMultiple(args, c2r);
    Expression min = null;
    for (Expression arg : args) {
      if (min == null
          || Expressions.getNumberValue(arg).compareTo(Expressions.getNumberValue(min)) < 0)
        min = arg;
    }
    return min;
  }
}
