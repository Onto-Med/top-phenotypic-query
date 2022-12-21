package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import java.time.LocalDateTime;
import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.ExpressionBuilder;

public class DiffMonths extends FunctionEntity {

  private static DiffMonths INSTANCE = new DiffMonths();

  private DiffMonths() {
    super(
        new ExpressionFunction()
            .id("diffMonths")
            .title("diffMonths")
            .minArgumentNumber(2)
            .maxArgumentNumber(2)
            .notation(NotationEnum.PREFIX));
  }

  public static DiffMonths get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsType(getFunction(), DataType.DATE_TIME, args);
    LocalDateTime start = Expressions.getDateTimeValue(args.get(0));
    LocalDateTime end = Expressions.getDateTimeValue(args.get(1));
    return ExpressionBuilder.of(DateUtil.getPeriodInMonths(start, end));
  }
}
