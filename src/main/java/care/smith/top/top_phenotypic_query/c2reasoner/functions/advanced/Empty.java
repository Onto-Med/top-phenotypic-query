package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.builder.ExpBuild;

public class Empty extends FunctionEntity {

  private static Empty INSTANCE = new Empty();

  private Empty() {
    super(
        new ExpressionFunction()
            .id("empty")
            .title("empty")
            .minArgumentNumber(1)
            .maxArgumentNumber(1)
            .notation(NotationEnum.PREFIX));
  }

  public static Empty get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    Expression arg = args.get(0);
    Value val = arg.getValue();
    List<Value> vals = arg.getValues();
    return ExpBuild.of(val == null && (vals == null || vals.isEmpty()));
  }
}
