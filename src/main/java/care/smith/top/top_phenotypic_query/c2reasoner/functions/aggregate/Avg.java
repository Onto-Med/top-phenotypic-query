package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class Avg extends FunctionEntity {

  private static final Avg INSTANCE = new Avg();

  private Avg() {
    super("avg", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static Avg get() {
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
    args = Aggregator.calcAndAggrCheckMultipleHaveValues(getFunction(), DataType.NUMBER, args, c2r);
    if (args == null) return null;
    BigDecimal avg = BigDecimal.ZERO;
    for (Expression arg : args)
      avg = avg.add(Expressions.getNumberValue(arg), c2r.getMathContext());
    return Exp.of(avg.divide(new BigDecimal(args.size()), c2r.getMathContext()));
  }
}
