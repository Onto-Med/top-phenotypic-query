package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The function <b>Median</b> returns the median (separating the higher half from the lower half) of
 * the argument values. It is allowed to use the function with a single argument expression if it
 * returns a list of values.
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
 *     <i>Median</i>(Body_Temperature)<br>
 *     The function returns the median temperature value (e.g., in a certain period).
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Median extends FunctionEntity {

  private static final Median INSTANCE = new Median();

  private Median() {
    super("median", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static Median get() {
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
    args = args.stream().sorted(Values.VALUE_COMPARATOR).collect(Collectors.toList());
    int size = args.size();
    if (size % 2 == 0)
      return Avg.get().calculate(List.of(args.get(size / 2 - 1), args.get(size / 2)), c2r);
    else return args.get(size / 2);
  }
}
