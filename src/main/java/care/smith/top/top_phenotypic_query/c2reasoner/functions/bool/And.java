package care.smith.top.top_phenotypic_query.c2reasoner.functions.bool;

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
import java.util.List;

/**
 * The <b>And-function</b> is a Boolean operation on any number of Boolean expressions (arguments).
 * It returns 'true' if all arguments are true, otherwise 'false'. It is allowed to use the function
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
 *   <td>&lt;exp&gt;: boolean</td>
 *   <td>boolean</td>
 *   <td>
 *     Male <i>And</i> Young <i>And</i> Overweight<br>
 *     The function returns 'true' if all three condition are true,<br>
 *     otherwise 'false'.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class And extends FunctionEntity {

  private static And INSTANCE = new And();

  private And() {
    super("and", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static And get() {
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
    if (args.size() == 1) {
      args = Aggregator.calcToList(getFunction(), DataType.BOOLEAN, args.get(0), c2r);
      if (args == null) return Exp.ofFalse();
      for (Expression arg : args) if (!Expressions.hasValueTrue(arg)) return Exp.ofFalse();
      return Exp.ofTrue();
    } else {
      for (Expression arg : args) {
        arg = Aggregator.calcAndAggr(getFunction(), DataType.BOOLEAN, arg, c2r);
        if (!Expressions.hasValueTrue(arg)) return Exp.ofFalse();
      }
      return Exp.ofTrue();
    }
  }
}
