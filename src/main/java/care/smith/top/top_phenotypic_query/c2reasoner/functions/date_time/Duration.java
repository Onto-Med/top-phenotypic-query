package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The <b>Duration-function</b> returns the duration (in days) of a process (e.g., encounter or
 * medication administration).
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt;</td>
 *   <td>&lt;exp&gt;: any</td>
 *   <td>number</td>
 *   <td>
 *     <i>Duration</i>(Dabigatran)<br>
 *     The function returns the duration (in days) of the Dabigatran administration.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Duration extends FunctionEntity {

  private static Duration INSTANCE = new Duration();

  private Duration() {
    super("duration", NotationEnum.PREFIX, 1, 1);
  }

  public static Duration get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype arg) {
    return of(Exp.of(arg));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression arg = c2r.calculate(args.get(0));
    if (!Expressions.hasValues(arg)) return null;
    arg = Aggregator.aggregate(arg, c2r);
    Value val = Expressions.getValue(arg);
    LocalDateTime start = val.getStartDateTime();
    if (start == null) return null;
    LocalDateTime end = val.getEndDateTime();
    if (end == null) end = LocalDateTime.now();
    return Exp.of(DateUtil.getPeriodInDays(start, end));
  }
}
