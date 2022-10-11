package care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic;

import java.math.BigDecimal;
import java.util.List;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

public class Divide extends FunctionEntity {

  private static Divide INSTANCE = new Divide();

  private Divide() {
    super(
        new ExpressionFunction()
            .id("divide")
            .title("/")
            .minArgumentNumber(2)
            .maxArgumentNumber(2)
            .notation(NotationEnum.INFIX));
  }

  public static Divide get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);
    args = Aggregator.aggregate(args, defaultAggregateFunction, c2r);
    BigDecimal arg1 = ExpressionUtil.getValueNumber(args.get(0));
    BigDecimal arg2 = ExpressionUtil.getValueNumber(args.get(1));
    BigDecimal div = arg1.divide(arg2, mc);
    return ValueUtil.toExpression(div);
  }
}
