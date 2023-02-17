package care.smith.top.top_phenotypic_query.song.operator;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public abstract class Not extends SearchOperator {

  protected Not() {
    super("NOT", NotationEnum.INFIX, 1, 1);
  }

  public static Expression of(Expression arg) {
    return Exp.function("Not", arg);
  }

  public static Expression of(Phenotype arg) {
    return of(Exp.of(arg));
  }

  public static Expression of(String phenotypeId) {
    return of(Exp.ofEntity(phenotypeId));
  }
}
