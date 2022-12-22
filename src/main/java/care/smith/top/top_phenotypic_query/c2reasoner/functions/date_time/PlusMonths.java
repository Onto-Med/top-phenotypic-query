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
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class PlusMonths extends FunctionEntity {

  private static PlusMonths INSTANCE = new PlusMonths();

  private PlusMonths() {
    super(
        new ExpressionFunction()
            .id("plusMonths")
            .title("plusMonths")
            .minArgumentNumber(2)
            .maxArgumentNumber(2)
            .notation(NotationEnum.PREFIX));
  }

  public static PlusMonths get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentType(getFunction(), DataType.DATE_TIME, args.get(0));
    Exceptions.checkArgumentType(getFunction(), DataType.NUMBER, args.get(1));
    LocalDateTime start = Expressions.getDateTimeValue(args.get(0));
    long months = Expressions.getNumberValue(args.get(1)).longValue();
    return Exp.of(start.plusMonths(months));
  }
}
