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
