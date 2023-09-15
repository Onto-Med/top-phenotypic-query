package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

import java.math.RoundingMode;
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

public class Power extends FunctionEntity {

  private static Power INSTANCE = new Power();

  private Power() {
    super("^", NotationEnum.INFIX, 2, 2);
  }

  public static Power get() {
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
    args = c2r.calculateCheckValues(args);
    if (args == null) return null;
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);

    Double arg1 = Expressions.getNumberValue(Aggregator.aggregate(args.get(0), c2r));
    Double arg2 = Expressions.getNumberValue(args.get(1));

    int signOf2 = (int) Math.signum(arg2);
    double dn1 = arg1.doubleValue();
    arg2 = arg2*signOf2; // n2 is now positive
    Double remainderOf2 = arg2%1.0;
    Double n2IntPart = arg2-remainderOf2;
    Double intPow = Math.pow(arg1,n2IntPart.intValue());
    Double doublePow = Double.valueOf(Math.pow(dn1, remainderOf2.doubleValue()));

    Double result = intPow*doublePow;
    if (signOf2 == -1)
      result =
          1.0/result;

    return Exp.of(result);
  }
}
