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
 * The <b>MinTrue-function</b> is an extended Boolean operation. The first argument specifies the
 * minimum number of subsequent arguments that must be true. If there are at least as many true
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
 *   <td>&lt;min-num&gt; &lt;exp&gt; &lt;exp&gt;+</td>
 *   <td>
 *     &lt;min-num&gt;: number<br>
 *     &lt;exp&gt;: boolean
 *   </td>
 *   <td>boolean</td>
 *   <td>
 *     <i>MinTrue</i>(2, Overweight, Hypertension, Stroke)<br>
 *     The function returns 'true' if at least two of the tree conditions are true,<br>
 *     otherwise 'false'.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class MinTrue extends FunctionEntity {

  private static MinTrue INSTANCE = new MinTrue();

  private MinTrue() {
    super("minTrue", NotationEnum.PREFIX);
    minArgumentNumber(2);
  }

  public static MinTrue get() {
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
    int min = Expressions.getNumberValue(args.get(0)).intValue();
    int count = 0;
    for (Expression arg : args.subList(1, args.size())) {
      arg = Aggregator.calcAndAggr(getFunction(), DataType.BOOLEAN, arg, c2r);
      if (Expressions.hasValueTrue(arg)) {
        count++;
        if (count >= min) return Exp.ofTrue();
      }
    }
    return Exp.ofFalse();
  }
}
