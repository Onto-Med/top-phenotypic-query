package care.smith.top.top_phenotypic_query.c2reasoner.functions.set;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.ArrayList;
import java.util.List;

/**
 * The function <b>Union</b> combines its argument expressions to one expression containing multiple
 * values. The function must not be used for phenotype restrictions, but only for phenotypes!
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
 *   <td>&lt;exp&gt;: any</td>
 *   <td>list of values of the same data type as &lt;exp&gt;</td>
 *   <td>
 *     <i>Union</i>(Value1, Value2)<br>
 *     The function returns an expression containing the both values.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Union extends FunctionEntity {

  private static final Union INSTANCE = new Union();

  private Union() {
    super("union", NotationEnum.PREFIX);
    minArgumentNumber(2);
  }

  public static Union get() {
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
    args = c2r.calculateHaveValues(args);
    if (args.isEmpty()) return null;
    List<Value> vals = new ArrayList<>();
    for (Expression arg : args) vals.addAll(arg.getValues());
    return Exp.of(vals);
  }
}
