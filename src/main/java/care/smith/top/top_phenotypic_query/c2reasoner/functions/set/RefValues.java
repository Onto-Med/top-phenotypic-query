package care.smith.top.top_phenotypic_query.c2reasoner.functions.set;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.DateTimeRange;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The <b>RefValues-function</b> reduces the number of elements in a list (1. argument) based on a
 * date range restriction. The 2. and the optional 3. argument specify the date range in days with
 * respect to the temporally last element (index) in the given list (1. argument), e.g., 2-4 days
 * before the last element. The elements remaining in the list must be created/valid in the defined
 * date range.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt; &lt;days-num&gt; [&lt;days-num&gt;]</td>
 *   <td>
 *     &lt;exp&gt;: any<br>
 *     &lt;days-num&gt;: number
 *   </td>
 *   <td>list of values of the same data type as &lt;exp&gt;</td>
 *   <td>
 *     <i>RefValues</i>(Creatinine, 2, 4)<br>
 *     The function returns all Creatinine values collected 2-4 days before the last value.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class RefValues extends FunctionEntity {

  private static RefValues INSTANCE = new RefValues();

  private RefValues() {
    super("refValues", NotationEnum.PREFIX, 2, 3);
  }

  public static RefValues get() {
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

    Expression phe = args.get(0);
    List<Value> refVals = phe.getValues();
    Value index = refVals.get(0);
    Value pheId = Values.getField(index, "entityId");

    if (pheId != null) refVals = c2r.getValues(Values.getStringValue(pheId));
    else
      index =
          refVals.stream().sorted(Values.VALUE_DATE_COMPARATOR).reduce((a, b) -> b).orElse(null);

    int days1 = getDays(args.get(1));
    int days2 = (args.size() > 2) ? getDays(args.get(2)) : 0;

    LocalDateTime indexDateTime = Values.getDateTime(index);

    DateTimeRange dr =
        new DateTimeRange()
            .limit(
                RestrictionOperator.GREATER_THAN_OR_EQUAL_TO,
                indexDateTime.minusDays(Math.max(days1, days2)))
            .limit(RestrictionOperator.LESS_THAN, indexDateTime.minusDays(Math.min(days1, days2)));

    return Exp.of(Filter.filterDate(refVals, Restrictions.getInterval(dr.get())));
  }

  private int getDays(Expression e) {
    Exceptions.checkArgumentTypes(getFunction(), e, DataType.NUMBER);
    return Expressions.getNumberValue(e).intValue();
  }
}
