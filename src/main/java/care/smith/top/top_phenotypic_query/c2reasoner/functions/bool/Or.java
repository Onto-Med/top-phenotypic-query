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
 *
 *
 * <h1>Or-function</h1>
 *
 * <p>The Or-function is a Boolean operation on any number of Boolean expressions (arguments). It
 * returns 'true' if at least one argument is true, otherwise 'false'. It is allowed to use the
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
 *   <td>&lt;exp&gt;: boolean</td>
 *   <td>boolean</td>
 *   <td>
 *     Hypertension <i>Or</i> Overweight<br>
 *     The function returns 'true' if at least one of the two conditions is true,<br>
 *     otherwise 'false'.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Or extends FunctionEntity {

  private static Or INSTANCE = new Or();

  private Or() {
    super("or", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static Or get() {
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
      for (Expression arg : args) if (Expressions.hasValueTrue(arg)) return arg;
      return Exp.ofFalse();
    } else {
      for (Expression arg : args) {
        arg = Aggregator.calcAndAggr(getFunction(), DataType.BOOLEAN, arg, c2r);
        if (Expressions.hasValueTrue(arg)) return arg;
      }
      return Exp.ofFalse();
    }
  }
}
