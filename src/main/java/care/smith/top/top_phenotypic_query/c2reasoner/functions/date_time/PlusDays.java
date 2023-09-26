package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The <b>PlusDays-function</b> adds the given number of days (2. argument) to the given date (1.
 * argument) and returns the resulting date.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;date-exp&gt; &lt;days-num&gt;</td>
 *   <td>
 *     &lt;date-exp&gt;: date-time<br>
 *     &lt;days-num&gt;: number
 *   </td>
 *   <td>date-time</td>
 *   <td>
 *     <i>PlusDays</i>(2020-01-01, 3)<br>
 *     The function returns 2020-01-04.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class PlusDays extends FunctionEntity {

  private static PlusDays INSTANCE = new PlusDays();

  private PlusDays() {
    super("plusDays", NotationEnum.PREFIX, 2, 2);
  }

  public static PlusDays get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculateCheckValues(args);
    if (args == null) return null;
    Exceptions.checkArgumentType(getFunction(), DataType.DATE_TIME, args.get(0));
    Exceptions.checkArgumentType(getFunction(), DataType.NUMBER, args.get(1));
    LocalDateTime start = Expressions.getDateTimeValue(args.get(0));
    long days = Expressions.getNumberValue(args.get(1)).longValue();
    return Exp.of(start.plusDays(days));
  }
}
