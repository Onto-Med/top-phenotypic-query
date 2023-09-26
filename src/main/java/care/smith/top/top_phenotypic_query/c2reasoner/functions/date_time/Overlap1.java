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
 * The <b>Overlap1-function</b> returns 'true' if the two processes (e.g., encounter or medication
 * administration) represented by the first and the second arguments temporally overlap
 * (unidirectional, i.e., the first one starts not later as the second one), otherwise 'false'. The
 * third optional argument specifies the maximum number of hours that may lie between the two
 * processes.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt; &lt;exp&gt; [&lt;hours-num&gt;]</td>
 *   <td>
 *     &lt;exp&gt;: any<br>
 *     &lt;hours-num&gt;: number
 *   </td>
 *   <td>boolean</td>
 *   <td>
 *     <i>Overlap1</i>(Drug1, Drug2, 3)<br>
 *     The function returns 'true' if the medication administrations of Drug1 and Drug2<br>
 *     temporally overlap (maximum 3 hour may lie between) and Drug1 starts not later as Drug2.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Overlap1 extends FunctionEntity {

  private static Overlap1 INSTANCE = new Overlap1();

  private Overlap1() {
    super("overlap1", NotationEnum.PREFIX, 2, 3);
  }

  public static Overlap1 get() {
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

    int distance = 0;
    if (args.size() > 2) {
      Expression arg3 = args.get(2);
      Exceptions.checkArgumentType(getFunction(), DataType.NUMBER, arg3);
      distance = Expressions.getNumberValue(arg3).intValue();
    }

    for (Value v1 : vals1)
      for (Value v2 : vals2) if (Values.overlaps1(v1, v2, distance)) return Exp.ofTrue();

    return Exp.ofFalse();
  }
}
