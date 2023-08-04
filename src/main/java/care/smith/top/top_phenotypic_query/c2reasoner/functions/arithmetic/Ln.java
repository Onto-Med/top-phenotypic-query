package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class Ln extends FunctionEntity {

  private static Ln INSTANCE = new Ln();

  private Ln() {
    super("ln", NotationEnum.PREFIX, 1, 1);
  }

  public static Ln get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype arg) {
    return of(Exp.toList(arg));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression arg = Aggregator.calcAndAggr(getFunction(), DataType.NUMBER, args.get(0), c2r);
    if (arg == null) return null;
    double val = Expressions.getNumberValue(arg).doubleValue();
    return Exp.of(Math.log(val));
  }
}
