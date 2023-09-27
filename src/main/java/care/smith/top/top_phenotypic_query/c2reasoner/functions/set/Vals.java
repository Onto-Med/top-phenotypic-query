package care.smith.top.top_phenotypic_query.c2reasoner.functions.set;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.ArrayList;
import java.util.List;

/**
 * The function <b>Vals</b> combines its argument expressions to one expression containing multiple
 * values. The function should be used mainly for phenotype restrictions. It is allowed to use the
 * function with a single argument expression.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt;+</td>
 *   <td>&lt;exp&gt;: any</td>
 *   <td>list of values of the same data type as &lt;exp&gt;</td>
 *   <td>
 *     <i>Vals</i>(Value1, Value2)<br>
 *     The function returns an expression containing the both values.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Vals extends FunctionEntity {

  private static Vals INSTANCE = new Vals();

  private Vals() {
    super("values", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static Vals get() {
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
    List<Value> vals = new ArrayList<>();
    for (Expression arg : args) {
      if (arg.getEntityId() == null) continue;
      List<Value> argVals = c2r.getValues(arg.getEntityId());
      if (argVals != null) vals.addAll(argVals);
    }
    return Exp.of(vals);
  }
}
