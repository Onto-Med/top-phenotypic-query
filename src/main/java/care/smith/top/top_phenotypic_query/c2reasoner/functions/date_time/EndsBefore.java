package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;

/**
 * The function <b>EndsBefore</b> returns 'true' if the process (e.g., encounter or medication
 * administration) represented by the 1st argument ends before the process represented by the 2nd
 * argument, otherwise 'false'.
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
 *   <td>&lt;exp&gt;: any</td>
 *   <td>boolean</td>
 *   <td>
 *     <i>EndsBefore</i>(eGFR, Dabigatran)<br>
 *     The function returns 'true' if the eGFR measurement ended before the Dabigatran administration.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class EndsBefore extends FunctionEntity {

  private static EndsBefore INSTANCE = new EndsBefore();

  private EndsBefore() {
    super("endsBefore", NotationEnum.PREFIX, 2, 2);
  }

  public static EndsBefore get() {
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
    args = c2r.calculateCheckValues(args);
    if (args == null) return null;
    Value v1 = Expressions.getValue(Aggregator.aggregate(args.get(0), c2r));
    Value v2 = Expressions.getValue(Aggregator.aggregate(args.get(1), c2r));
    return Exp.of(Values.endsBefore(v1, v2));
  }
}
