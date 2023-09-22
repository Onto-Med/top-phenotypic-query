package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 *
 * <h1>Avg-function</h1>
 *
 * <p>The Avg-function returns the average of the argument values. It is allowed to use the function
 * with a single argument expression if it returns a list of values.
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
 *     <i>Avg</i>(Body_Temperature)<br>
 *     The function returns the average temperature value (e.g., in a certain period).
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Avg extends FunctionEntity {

  private static final Avg INSTANCE = new Avg();

  private Avg() {
    super("avg", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static Avg get() {
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
    args = Aggregator.calcAndAggrCheckMultipleHaveValues(getFunction(), DataType.NUMBER, args, c2r);
    if (args == null) return null;
    BigDecimal avg = BigDecimal.ZERO;
    for (Expression arg : args)
      avg = avg.add(Expressions.getNumberValue(arg), c2r.getMathContext());
    return Exp.of(avg.divide(new BigDecimal(args.size()), c2r.getMathContext()));
  }
}
