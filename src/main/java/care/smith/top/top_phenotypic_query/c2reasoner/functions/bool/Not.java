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
 * <h1>Not-function</h1>
 *
 * <p>The Not-function is a Boolean operation on a single Boolean expression (argument). It returns
 * 'false' if the argument is true, otherwise 'true'.
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
 *   <td>&lt;exp&gt;: boolean</td>
 *   <td>boolean</td>
 *   <td>
 *     <i>Not</i>(Overweight)<br>
 *     The function returns 'false' if the condition 'Overweight' is true,<br>
 *     otherwise 'true'.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Not extends FunctionEntity {

  private static Not INSTANCE = new Not();

  private Not() {
    super("not", NotationEnum.PREFIX, 1, 1);
  }

  public static Not get() {
    return INSTANCE;
  }

  public static Expression of(Expression arg) {
    return Exp.function(get().getClass().getSimpleName(), arg);
  }

  public static Expression of(Phenotype arg) {
    return of(Exp.of(arg));
  }

  public static Expression of(String phenotypeId) {
    return of(Exp.ofEntity(phenotypeId));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression arg = Aggregator.calcAndAggr(getFunction(), DataType.BOOLEAN, args.get(0), c2r);
    return Exp.of(!Expressions.hasValueTrue(arg));
  }
}
