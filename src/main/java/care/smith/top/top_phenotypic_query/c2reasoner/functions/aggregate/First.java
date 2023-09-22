package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

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
 *
 *
 * <h1>First-function</h1>
 *
 * <p>The First-function returns the temporally first value of the argument values (based on their
 * timestamps). It is allowed to use the function with a single argument expression if it returns a
 * list of values.
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
 *   <td>&lt;exp&gt;: any</td>
 *   <td>same as &lt;exp&gt;</td>
 *   <td>
 *     <i>First</i>(Body_Temperature)<br>
 *     The function returns the first temperature value (e.g., in a certain period).
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class First extends FunctionEntity {

  private static final First INSTANCE = new First();

  private First() {
    super("first", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static First get() {
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
    args = Aggregator.calcAndAggrCheckMultipleHaveValues(getFunction(), args, c2r);
    if (args == null) return null;
    args = args.stream().sorted(Values.EXP_DATE_COMPARATOR).collect(Collectors.toList());
    return args.get(0);
  }
}
