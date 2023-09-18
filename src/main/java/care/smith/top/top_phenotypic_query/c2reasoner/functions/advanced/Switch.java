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
 * <h1>Switch-function</h1>
 *
 * <p>The Switch-function combines any number of if-then pairs and an optional default expression
 * (else-expression). If one of the if-expressions is true, the corresponding then-expression is
 * returned, otherwise the else-expression. If all if-expressions are false and the else-expression
 * is undefined, a 'missing value' is returned.
 *
 * <table><caption>Arguments:</caption>
 * <tr>
 *   <th>Arguments</th>
 *   <th>Description</th>
 *   <th>Example</th>
 * </tr>
 * <tr>
 *   <td>&lt;Switch&gt; ::= (&lt;if_exp&gt; &lt;then_exp&gt;)+ [&lt;else_exp&gt;]</td>
 *   <td><p>all &lt;if_exp&gt;: boolean
 *   <p>all &lt;then_exp&gt; and &lt;else_exp&gt;: any (but the same) data type</td>
 *   <td>Switch(Creatinine &lt; 1, 1, Creatinine &gt; 3, 3, Creatinine)</td>
 * </tr>
 * </table>
 *
 * @author JRG TOP
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
