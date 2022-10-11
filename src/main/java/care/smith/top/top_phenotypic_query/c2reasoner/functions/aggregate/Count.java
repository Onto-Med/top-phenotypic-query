package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

public class Count extends FunctionEntity {

  private static final Count INSTANCE = new Count();

  private Count() {
    super(
        new ExpressionFunction()
            .id("count")
            .title("count")
            .minArgumentNumber(1)
            .maxArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static Count get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression arg = c2r.calculate(args.get(0), defaultAggregateFunction);
    Exceptions.checkArgumentHasValues(getFunction(), arg);
    return ValueUtil.toExpression(arg.getValues().size());
  }
}
