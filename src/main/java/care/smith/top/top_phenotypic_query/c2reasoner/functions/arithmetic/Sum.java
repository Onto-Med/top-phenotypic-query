package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.math.BigDecimal;
import java.util.List;

/**
 * The <b>Sum-function</b> adds all its arguments.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt; &lt;exp&gt;+</td>
 *   <td>&lt;exp&gt;: number</td>
 *   <td>number</td>
 *   <td>
 *     <i>Sum</i>(6, 2, 3)<br>
 *     The function returns 11.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Sum extends FunctionEntity {

  private static Sum INSTANCE = new Sum();

  private Sum() {
    super("sum", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static Sum get() {
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
    args =
        Aggregator.calcAndAggrCheckMultipleCheckValues(getFunction(), DataType.NUMBER, args, c2r);
    if (args == null) return null;
    BigDecimal result = BigDecimal.ZERO;
    for (Expression arg : args)
      result = result.add(Expressions.getNumberValue(arg), c2r.getMathContext());
    return Exp.of(result);
  }
}
