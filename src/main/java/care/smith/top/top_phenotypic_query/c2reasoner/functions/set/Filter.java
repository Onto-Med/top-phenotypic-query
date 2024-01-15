package care.smith.top.top_phenotypic_query.c2reasoner.functions.set;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.DateTimeRange;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.NumberRange;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The function <b>Filter</b> reduces the number of elements in a list (1st argument) based on a
 * value range or date range restriction. I.e., the elements remaining in the list must lie in the
 * value range and must be created/valid in the date range.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt; &lt;days-num&gt;</td>
 *   <td>
 *     &lt;exp&gt;: any<br>
 *     &lt;days-num&gt;: number
 *   </td>
 *   <td>list of values of the same data type as &lt;exp&gt;</td>
 *   <td>
 *     <i>Filter</i>(Creatinine, 7)<br>
 *     The function returns all Creatinine values of the last 7 days.
 *   </td>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt; &lt;days-num&gt; &lt;phe&gt;</td>
 *   <td>
 *     &lt;exp&gt;: any<br>
 *     &lt;days-num&gt;: number<br>
 *     &lt;phe&gt;: phenotype class
 *   </td>
 *   <td>list of values of the same data type as &lt;exp&gt;</td>
 *   <td>
 *     <i>Filter</i>(Dialysis, 7, Creatinine)<br>
 *     The function returns all dialyses for the 7 days prior to the (last) creatinine measurement.
 *   </td>
 * </tr>
 * <tr>
 *   <td>&lt;exp&gt; (&lt;comparison-operator&gt; &lt;limit&gt;)+</td>
 *   <td>
 *     &lt;exp&gt;: if one of &lt;limit&gt; is a number then number, otherwise any<br>
 *     &lt;comparison-operator&gt;: constant (&gt;, &ge;, &lt;, &le;)<br>
 *     &lt;limit&gt;: number or date-time
 *   </td>
 *   <td>list of values of the same data type as &lt;exp&gt;</td>
 *   <td>
 *     <i>Filter</i>(Creatinine, &ge;, 0.7, &le;, 1.3, &ge;, 2023-01-01, &le;, 2023-01-31)<br>
 *     The function returns all Creatinine values from January 2023 that lie between 0.7 and 1.3.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class Filter extends FunctionEntity {

  private static Filter INSTANCE = new Filter();

  private Filter() {
    super("filter", NotationEnum.PREFIX, 2, 9);
  }

  public static Filter get() {
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

    if (args.size() == 2) {
      Expression daysExp = args.get(1);
      Exceptions.checkArgumentTypes(getFunction(), daysExp, DataType.NUMBER);
      int days = Expressions.getNumberValue(daysExp).intValue();
      DateTimeRange dr =
          new DateTimeRange()
              .limit(
                  RestrictionOperator.GREATER_THAN_OR_EQUAL_TO,
                  LocalDateTime.now().minusDays(days));
      return Exp.of(filterDate(phe.getValues(), Restrictions.getInterval(dr.get())));
    }

    if (args.size() == 3) {
      Expression daysExp = args.get(1);
      if (Expressions.getDataType(daysExp) == DataType.NUMBER) {
        Expression datePhe = Aggregator.aggregate(args.get(2), c2r);
        LocalDateTime endDateTime = Values.getDateTime(Expressions.getValue(datePhe));
        if (endDateTime == null) return null;
        LocalDateTime startDateTime =
            endDateTime.minusDays(Expressions.getNumberValue(daysExp).intValue());
        DateTimeRange dr =
            new DateTimeRange()
                .limit(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO, startDateTime)
                .limit(RestrictionOperator.LESS_THAN, endDateTime);
        return Exp.of(filterDate(phe.getValues(), Restrictions.getInterval(dr.get())));
      }
    }

    for (int i = 1; i < args.size(); i++) {
      if (i % 2 == 0)
        Exceptions.checkArgumentTypes(
            getFunction(), args.get(i), DataType.NUMBER, DataType.DATE_TIME);
      else Exceptions.checkArgumentTypes(getFunction(), args.get(i), DataType.STRING);
    }

    NumberRange nr = new NumberRange();
    DateTimeRange dr = new DateTimeRange();

    for (int i = 1; i < args.size(); i += 2) {
      Expression oper = args.get(i);
      Expression val = args.get(i + 1);
      if (Expressions.hasNumberType(val)) nr.limit(oper, val);
      if (Expressions.hasDateTimeType(val)) dr.limit(oper, val);
    }

    List<Value> vals = phe.getValues();

    if (Expressions.hasDateTimeType(phe)) {
      if (dr.isPresent()) vals = filterValue(vals, Restrictions.getInterval(dr.get()));
    } else {
      if (nr.isPresent()) vals = filterValue(vals, Restrictions.getInterval(nr.get()));
      if (dr.isPresent()) vals = filterDate(vals, Restrictions.getInterval(dr.get()));
    }

    return Exp.of(vals);
  }

  private List<Value> filterValue(List<Value> vals, Map<RestrictionOperator, Value> inter) {
    return vals.stream().filter(v -> Values.contains(inter, v)).collect(Collectors.toList());
  }

  public static List<Value> filterDate(List<Value> vals, Map<RestrictionOperator, Value> inter) {
    return vals.stream()
        .filter(
            v ->
                Values.getDateTime(v) != null
                    && Values.contains(inter, Val.of(Values.getDateTime(v))))
        .collect(Collectors.toList());
  }
}
