package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * The function <b>Power</b> realizes the exponentiation operation.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;base-exp&gt; &lt;power-exp&gt;</td>
 *   <td>
 *     &lt;base-exp&gt;: number<br>
 *     &lt;power-exp&gt;: number
 *   </td>
 *   <td>number</td>
 *   <td>
 *     6 ^ 2<br>
 *     The function returns 36.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
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

  public static Expression of(Phenotype phe1, Phenotype phe2) {
    return of(Exp.of(phe1), Exp.of(phe2));
  }

  public static Expression of(Phenotype phe, Number val) {
    return of(Exp.of(phe), Exp.of(val));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculateCheckValues(args);
    if (args == null) return null;
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);

    BigDecimal arg1 = Expressions.getNumberValue(Aggregator.aggregate(args.get(0), c2r));
    BigDecimal arg2 = Expressions.getNumberValue(args.get(1));

    int signOf2 = arg2.signum();
    double dn1 = arg1.doubleValue();
    arg2 = arg2.multiply(new BigDecimal(signOf2)); // n2 is now positive
    BigDecimal remainderOf2 = arg2.remainder(BigDecimal.ONE);
    BigDecimal n2IntPart = arg2.subtract(remainderOf2);
    BigDecimal intPow = arg1.pow(n2IntPart.intValueExact(), c2r.getMathContext());
    BigDecimal doublePow = BigDecimal.valueOf(Math.pow(dn1, remainderOf2.doubleValue()));

    BigDecimal result = intPow.multiply(doublePow, c2r.getMathContext());
    if (signOf2 == -1)
      result =
          BigDecimal.ONE.divide(result, c2r.getMathContext().getPrecision(), RoundingMode.HALF_UP);

    return Exp.of(result);
  }
}
