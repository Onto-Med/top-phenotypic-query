package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import java.time.LocalDateTime;
import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

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
    args = c2r.calculate(args);
    Exceptions.checkArgumentsAreNotNull(getFunction(), args);
    Exceptions.checkArgumentType(getFunction(), DataType.DATE_TIME, args.get(0));
    Exceptions.checkArgumentType(getFunction(), DataType.NUMBER, args.get(1));
    LocalDateTime start = Expressions.getDateTimeValue(args.get(0));
    long days = Expressions.getNumberValue(args.get(1)).longValue();
    return Exp.of(start.plusDays(days));
  }
}
