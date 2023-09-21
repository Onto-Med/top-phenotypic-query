package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

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
 * <h1>Ln-function</h1>
 *
 * <p>The Ln-function realizes the natural logarithm.
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
 *   <td>&lt;exp&gt;: number</td>
 *   <td>number</td>
 *   <td>
 *     <i>Ln</i>(1)<br>
 *     The function returns 0.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Ln extends FunctionEntity {

  private static Ln INSTANCE = new Ln();

  private Ln() {
    super("ln", NotationEnum.PREFIX, 1, 1);
  }

  public static Ln get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype arg) {
    return of(Exp.toList(arg));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression arg = Aggregator.calcAndAggr(getFunction(), DataType.NUMBER, args.get(0), c2r);
    if (arg == null) return null;
    double val = Expressions.getNumberValue(arg).doubleValue();
    return Exp.of(Math.log(val));
  }
}
