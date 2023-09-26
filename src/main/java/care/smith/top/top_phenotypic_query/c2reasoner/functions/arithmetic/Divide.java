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
import java.math.BigDecimal;
import java.util.List;

/**
 * The <b>Divide-function</b> divides the first argument by the second one.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt; &lt;exp&gt;</td>
 *   <td>&lt;exp&gt;: number</td>
 *   <td>number</td>
 *   <td>
 *     6 / 2<br>
 *     The function returns 3.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Divide extends FunctionEntity {

  private static Divide INSTANCE = new Divide();

  private Divide() {
    super("/", NotationEnum.INFIX, 2, 2);
  }

  public static Divide get() {
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
    args = Aggregator.calcAndAggrCheckValues(getFunction(), DataType.NUMBER, args, c2r);
    if (args == null) return null;
    BigDecimal arg1 = Expressions.getNumberValue(args.get(0));
    BigDecimal arg2 = Expressions.getNumberValue(args.get(1));
    BigDecimal div = arg1.divide(arg2, c2r.getMathContext());
    return Exp.of(div);
  }
}
