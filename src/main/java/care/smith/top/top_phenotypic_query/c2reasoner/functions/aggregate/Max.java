package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;

/**
 *
 *
 * <h1>Max-function</h1>
 *
 * <p>The Max-function returns the maximum value of the argument values. It is allowed to use the
 * function with a single argument expression if it returns a list of values.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt;+</td>
 *   <td>&lt;exp&gt;: number</td>
 *   <td>number</td>
 *   <td>
 *     <i>Max</i>(Body_Temperature)<br>
 *     The function returns the maximum temperature value (e.g., in a certain period).
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Max extends FunctionEntity {

  private static final Max INSTANCE = new Max();

  private Max() {
    super("max", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static Max get() {
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
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = Aggregator.calcAndAggrCheckMultipleHaveValues(getFunction(), DataType.NUMBER, args, c2r);
    if (args == null) return null;
    Expression max = null;
    for (Expression arg : args) {
      if (max == null
          || Expressions.getNumberValue(arg).compareTo(Expressions.getNumberValue(max)) > 0)
        max = arg;
    }
    return max;
  }
}
