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
 * The function <b>If</b> returns the then-expression (2nd argument) if a condition (the
 * if-expression, 1st argument) is true, otherwise the optional else-expression (3rd argument). If the
 * if-expression is false and the else-expression is undefined, a 'missing value' is returned.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;if-exp&gt; &lt;then-exp&gt; [&lt;else-exp&gt;]</td>
 *   <td>
 *     &lt;if-exp&gt;: boolean<br>
 *     &lt;then-exp&gt;: any<br>
 *     &lt;else-exp&gt;: same as &lt;then-exp&gt;
 *   </td>
 *   <td>same as &lt;then-exp&gt;</td>
 *   <td>
 *     <i>If</i>(Bilirubin &lt; 1, 1, Bilirubin)<br>
 *     The function returns 1 if the Bilirubin value is less than 1,<br>
 *     otherwise the Bilirubin value.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class If extends FunctionEntity {

  private static If INSTANCE = new If();

  private If() {
    super("if", NotationEnum.PREFIX, 3, 3);
  }

  public static If get() {
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
    Expression cond = Aggregator.calcAndAggr(getFunction(), DataType.BOOLEAN, args.get(0), c2r);
    if (Expressions.hasValueTrue(cond)) return c2r.calculate(args.get(1));
    return c2r.calculate(args.get(2));
  }
}
