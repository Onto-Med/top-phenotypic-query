package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;

/**
 * The <b>Overlap2-function</b> returns 'true' if the two processes (e.g., encounter or medication
 * administration) represented by the first and the second arguments temporally overlap
 * (bidirectional), otherwise 'false'. The third and the fourth optional arguments specifies the
 * maximum number of hours that may lie between the two processes (in both directions). If only the
 * third argument is specified, it applies to both directions.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt; &lt;exp&gt; [&lt;hours-num&gt;] [&lt;hours-num&gt;]</td>
 *   <td>
 *     &lt;exp&gt;: any<br>
 *     &lt;hours-num&gt;: number
 *   </td>
 *   <td>boolean</td>
 *   <td>
 *     <i>Overlap2</i>(Drug1, Drug2, 3, 4)<br>
 *     The function returns 'true' if the medication administrations of Drug1 and Drug2<br>
 *     temporally overlap (maximum 3 hour may lie between if Drug1 before Drug2<br>
 *     and maximum 4 hour may lie between if Drug2 before Drug1).
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Overlap2 extends FunctionEntity {

  private static Overlap2 INSTANCE = new Overlap2();

  private Overlap2() {
    super("overlap2", NotationEnum.PREFIX, 2, 4);
  }

  public static Overlap2 get() {
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
    if (args == null) return Exp.ofFalse();

    List<Value> vals1 = args.get(0).getValues();
    List<Value> vals2 = args.get(1).getValues();

    int distance1 = 0;
    int distance2 = 0;
    if (args.size() > 2) {
      Expression arg3 = args.get(2);
      Exceptions.checkArgumentType(getFunction(), DataType.NUMBER, arg3);
      distance1 = Expressions.getNumberValue(arg3).intValue();
      if (args.size() > 3) {
        Expression arg4 = args.get(3);
        Exceptions.checkArgumentType(getFunction(), DataType.NUMBER, arg4);
        distance2 = Expressions.getNumberValue(arg4).intValue();
      } else distance2 = distance1;
    }

    for (Value v1 : vals1)
      for (Value v2 : vals2)
        if (Values.overlaps2(v1, v2, distance1, distance2)) return Exp.ofTrue();

    return Exp.ofFalse();
  }
}
