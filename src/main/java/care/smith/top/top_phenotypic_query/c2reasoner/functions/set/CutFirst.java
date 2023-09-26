package care.smith.top.top_phenotypic_query.c2reasoner.functions.set;

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
import java.util.stream.Collectors;

/**
 * The <b>CutFirst-function</b> cuts the temporally first element of the given arguments list and
 * returns the resulting list. It is allowed to use the function with a single argument expression
 * if it returns a list of values.
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
 *     <i>CutFirst</i>(Body_Temperature)<br>
 *     The function returns all temperature values except the first one.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class CutFirst extends FunctionEntity {

  private static final CutFirst INSTANCE = new CutFirst();

  private CutFirst() {
    super("cutFirst", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static CutFirst get() {
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
    args = Aggregator.calcAndAggrCheckMultipleHaveValues(getFunction(), args, c2r);
    if (args == null) return null;
    return Exp.of(
        args.stream()
            .sorted(Values.EXP_DATE_COMPARATOR)
            .skip(1)
            .map(e -> Expressions.getValue(e))
            .collect(Collectors.toList()));
  }
}
