package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class If extends FunctionEntity {

  private static If INSTANCE = new If();

  private If() {
    super("if", NotationEnum.PREFIX, 3, 3);
  }

  public static If get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype... args) {
    return of(Exp.toList(args));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression cond = c2r.calculate(args.get(0));
    if (Expressions.hasValues(cond)) {
      Exceptions.checkArgumentType(getFunction(), DataType.BOOLEAN, cond);
      if (Expressions.getBooleanValue(cond)) return c2r.calculate(args.get(1));
    }
    return c2r.calculate(args.get(2));
  }
}
