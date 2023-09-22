package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 *
 * <h1>DiffYears-function</h1>
 *
 * <p>The DiffYears-function returns the time difference in years between two dates.
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
 *   <td>&lt;exp&gt;: date-time</td>
 *   <td>number</td>
 *   <td>
 *     <i>DiffYears</i>(2020-01-01, 2023-01-01)<br>
 *     The function returns 3 (years).
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class DiffYears extends FunctionEntity {

  private static DiffYears INSTANCE = new DiffYears();

  private DiffYears() {
    super("diffYears", NotationEnum.PREFIX, 2, 2);
  }

  public static DiffYears get() {
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
    Exceptions.checkArgumentsType(getFunction(), DataType.DATE_TIME, args);
    LocalDateTime start = Expressions.getDateTimeValue(args.get(0));
    LocalDateTime end = Expressions.getDateTimeValue(args.get(1));
    return Exp.of(DateUtil.getPeriodInYears(start, end));
  }
}
