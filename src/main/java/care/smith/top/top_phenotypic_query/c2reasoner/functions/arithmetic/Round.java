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
 * The function <b>Round</b> rounds the given number (1st argument) to the desired number of decimal
 * places (2nd argument).
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;num-exp&gt; &lt;dec-exp&gt;</td>
 *   <td>
 *     &lt;num-exp&gt;: number<br>
 *     &lt;dec-exp&gt;: number
 *   </td>
 *   <td>number</td>
 *   <td>
 *     Round(1666.6666, 2)<br>
 *     The function returns 1666.67.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Round extends FunctionEntity {

  private static Round INSTANCE = new Round();

  private Round() {
    super("Round", NotationEnum.PREFIX, 2, 2);
  }

  public static Round get() {
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

  public static Expression of(Expression exp, Number val) {
    return of(exp, Exp.of(val));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculateCheckValues(args);
    if (args == null) return null;
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);

    BigDecimal arg1 = Expressions.getNumberValue(Aggregator.aggregate(args.get(0), c2r));
    BigDecimal arg2 = Expressions.getNumberValue(args.get(1));
    BigDecimal res = arg1.setScale(arg2.intValue(), RoundingMode.HALF_UP);

    return Exp.of(res);
  }
}
