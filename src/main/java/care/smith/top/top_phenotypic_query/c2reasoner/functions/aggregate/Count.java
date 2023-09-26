package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;

/**
 * The <b>Count-function</b> returns the number of values of the argument expression.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt;</td>
 *   <td>&lt;exp&gt;: any</td>
 *   <td>number</td>
 *   <td>
 *     <i>Count</i>(Body_Temperature)<br>
 *     The function returns the number of temperature measurements (e.g., in a certain period).
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Count extends FunctionEntity {

  private static final Count INSTANCE = new Count();

  private Count() {
    super("count", NotationEnum.PREFIX, 1, 1);
  }

  public static Count get() {
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
    Expression arg = c2r.calculate(args.get(0));
    if (arg == null || arg.getValues() == null) return Exp.of(0);
    return Exp.of(arg.getValues().size());
  }
}
