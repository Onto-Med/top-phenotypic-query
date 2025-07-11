package care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;

/**
 * The function <b>Lt</b> realizes the comparison operator '&lt;' (less) and returns 'true' if the
 * first argument is less than the second argument, otherwise 'false'.
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
 *   <td>boolean</td>
 *   <td>
 *     6 &lt; 2<br>
 *     The function returns 'false'.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Lt extends FunctionEntity {

  private static Lt INSTANCE = new Lt();

  private Lt() {
    super("<", NotationEnum.INFIX, 2, 2);
  }

  public static Lt get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype phe1, Phenotype phe2) {
    return of(Exp.of(phe1), Exp.of(phe2));
  }

  public static Expression of(Phenotype phe, String val) {
    return of(Exp.of(phe), Exp.of(val));
  }

  public static Expression of(Phenotype phe, Number val) {
    return of(Exp.of(phe), Exp.of(val));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculateCheckValues(args);
    if (args == null) return null;
    Exceptions.checkArgumentsHaveSameType(getFunction(), args);
    args = Aggregator.aggregate(args, c2r);
    return Exp.of(
        Values.compare(Expressions.getValue(args.get(0)), Expressions.getValue(args.get(1))) < 0);
  }
}
