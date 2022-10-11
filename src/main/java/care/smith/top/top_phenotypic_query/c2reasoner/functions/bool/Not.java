package care.smith.top.top_phenotypic_query.c2reasoner.functions.bool;

import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

public class Not extends FunctionEntity {

  private static Not INSTANCE = new Not();

  private Not() {
    super(
        new ExpressionFunction()
            .id("not")
            .title("not")
            .minArgumentNumber(1)
            .maxArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static Not get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression arg = c2r.calculate(args.get(0), defaultAggregateFunction);
    Exceptions.checkArgumentHasValueOfType(getFunction(), DataType.BOOLEAN, arg);
    if (ValueUtil.hasValueTrue(arg)) return ValueUtil.getExpressionFalse();
    return ValueUtil.getExpressionTrue();
  }
}
