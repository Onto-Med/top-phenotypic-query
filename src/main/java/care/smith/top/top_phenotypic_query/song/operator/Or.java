package care.smith.top.top_phenotypic_query.song.operator;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public abstract class Or extends SearchOperator {

  protected Or() {
    super("OR", NotationEnum.INFIX);
    minArgumentNumber(2);
  }

  public static Expression of(List<Expression> args) {
    return Exp.function("Or", args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype... args) {
    return of(Exp.toList(args));
  }
}
