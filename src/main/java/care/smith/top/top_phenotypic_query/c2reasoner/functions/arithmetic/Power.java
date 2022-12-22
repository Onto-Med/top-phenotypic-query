package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import care.smith.top.top_phenotypic_query.util.builder.ExpBuild;

public class Power extends FunctionEntity {

  private static Power INSTANCE = new Power();

  private Power() {
    super(
        new ExpressionFunction()
            .id("power")
            .title("^")
            .minArgumentNumber(2)
            .maxArgumentNumber(2)
            .notation(NotationEnum.INFIX));
  }

  public static Power get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);

    BigDecimal arg1 =
        Expressions.getNumberValue(
            Aggregator.aggregate(args.get(0), defaultAggregateFunction, c2r));
    BigDecimal arg2 = Expressions.getNumberValue(args.get(1));

    int signOf2 = arg2.signum();
    double dn1 = arg1.doubleValue();
    arg2 = arg2.multiply(new BigDecimal(signOf2)); // n2 is now positive
    BigDecimal remainderOf2 = arg2.remainder(BigDecimal.ONE);
    BigDecimal n2IntPart = arg2.subtract(remainderOf2);
    BigDecimal intPow = arg1.pow(n2IntPart.intValueExact(), mc);
    BigDecimal doublePow = BigDecimal.valueOf(Math.pow(dn1, remainderOf2.doubleValue()));

    BigDecimal result = intPow.multiply(doublePow, mc);
    if (signOf2 == -1)
      result = BigDecimal.ONE.divide(result, mc.getPrecision(), RoundingMode.HALF_UP);

    return ExpBuild.of(result);
  }
}
