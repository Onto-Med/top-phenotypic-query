package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

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
import java.time.LocalDateTime;
import java.util.List;

public class Date extends FunctionEntity {

  private static Date INSTANCE = new Date();

  private Date() {
    super("date", NotationEnum.PREFIX, 1, 1);
  }

  public static Date get() {
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
    LocalDateTime dateTime = Values.getDateTime(Expressions.getValue(arg));
    if (dateTime == null) return null;
    return Exp.of(dateTime);
  }
}
