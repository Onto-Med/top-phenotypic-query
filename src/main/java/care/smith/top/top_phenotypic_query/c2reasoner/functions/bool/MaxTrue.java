package care.smith.top.top_phenotypic_query.c2reasoner.functions.bool;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;

/**
 * The function <b>MaxTrue</b> is an extended Boolean operation. The first argument specifies the
 * maximum number of subsequent arguments that may be true. If there are at most as many true
 * arguments (starting with the second argument), the function returns 'true', otherwise 'false'.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;max-num&gt; &lt;exp&gt; &lt;exp&gt;+</td>
 *   <td>
 *     &lt;max-num&gt;: number<br>
 *     &lt;exp&gt;: boolean
 *   </td>
 *   <td>boolean</td>
 *   <td>
 *     <i>MaxTrue</i>(2, Overweight, Hypertension, Stroke)<br>
 *     The function returns 'true' if at most two of the tree conditions are true,<br>
 *     otherwise 'false'.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class MaxTrue extends FunctionEntity {

  private static MaxTrue INSTANCE = new MaxTrue();

  private MaxTrue() {
    super("maxTrue", NotationEnum.PREFIX);
    minArgumentNumber(2);
  }

  public static MaxTrue get() {
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
    Exceptions.checkArgumentHasValueOfType(getFunction(), DataType.NUMBER, args.get(0));
    int max = Expressions.getNumberValue(args.get(0)).intValue();
    int count = 0;
    for (Expression arg : args.subList(1, args.size())) {
      arg = Aggregator.calcAndAggr(getFunction(), DataType.BOOLEAN, arg, c2r);
      if (Expressions.hasValueTrue(arg)) {
        count++;
        if (count > max) return Exp.ofFalse();
      }
    }
    return Exp.ofTrue();
  }
}
