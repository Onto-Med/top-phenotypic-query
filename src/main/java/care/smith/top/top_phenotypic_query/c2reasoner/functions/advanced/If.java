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
 *
 *
 * <h1>If-function</h1>
 *
 * <p>If a condition (the if-expression) is true, the If-function returns the then-expression,
 * otherwise the optional else-expression. If the if-expression is false and the else-expression is
 * undefined, a 'missing value' is returned.
 *
 * <table><caption>Arguments:</caption>
 * <tr>
 *   <th>Arguments</th>
 *   <th>Description</th>
 *   <th>Example</th>
 * </tr>
 * <tr>
 *   <td>&lt;If&gt; ::= &lt;if_exp&gt; &lt;then_exp&gt; [&lt;else_exp&gt;]</td>
 *   <td><p>&lt;if_exp&gt;: boolean
 *   <p>&lt;then_exp&gt; and &lt;else_exp&gt;: any (but the same) data type</td>
 *   <td>If(Bilirubin &lt; 1, 1, Bilirubin)</td>
 * </tr>
 * </table>
 *
 * @author JRG TOP
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
