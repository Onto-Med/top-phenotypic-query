package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

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
 * The <b>Switch-function</b> combines any number of if-then pairs and an optional default
 * expression (else-expression). If one of the if-expressions is true, the corresponding
 * then-expression is returned, otherwise the else-expression. If all if-expressions are false and
 * the else-expression is undefined, a 'missing value' is returned.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>(&lt;if-exp&gt; &lt;then-exp&gt;)+ [&lt;else-exp&gt;]</td>
 *   <td>
 *     &lt;if-exp&gt;: boolean<br>
 *     &lt;then-exp&gt;: any (all equal)<br>
 *     &lt;else-exp&gt;: same as all &lt;then-exp&gt;
 *   </td>
 *   <td>same as all &lt;then-exp&gt;</td>
 *   <td>
 *     <i>Switch</i>(Creatinine &lt; 1, 1, Creatinine &gt; 3, 3, Creatinine)<br>
 *     The function returns 1 if the Creatinine value is less than 1,<br>
 *     3 if the Creatinine value is greater than 3,<br>
 *     otherwise the Creatinine value.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Switch extends FunctionEntity {

  private static Switch INSTANCE = new Switch();

  private Switch() {
    super("switch", NotationEnum.PREFIX);
    minArgumentNumber(2);
  }

  public static Switch get() {
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
    if (args.size() % 2 == 0) return calculate(args, args.size(), null, c2r);
    return calculate(args, args.size() - 1, args.get(args.size() - 1), c2r);
  }

  private Expression calculate(
      List<Expression> args, int lastValueNum, Expression defaultValue, C2R c2r) {
    for (int i = 0; i < lastValueNum; i += 2) {
      Expression cond = Aggregator.calcAndAggr(getFunction(), DataType.BOOLEAN, args.get(i), c2r);
      if (Expressions.hasValueTrue(cond)) return c2r.calculate(args.get(i + 1));
    }
    return c2r.calculate(defaultValue);
  }
}
