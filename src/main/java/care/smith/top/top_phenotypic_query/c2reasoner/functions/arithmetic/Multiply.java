package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class Multiply extends FunctionEntity {

  private static Multiply INSTANCE = new Multiply();

  private Multiply() {
    super("*", NotationEnum.INFIX, 2, 2);
  }

  public static Multiply get() {
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
    args = Aggregator.calcAndAggrCheckValues(getFunction(), DataType.NUMBER, args, c2r);
    if (args == null) return null;
    Double arg1 = Expressions.getNumberValue(args.get(0));
    Double arg2 = Expressions.getNumberValue(args.get(1));
    Double mul = arg1*arg2;
    return Exp.of(mul);
  }
}
