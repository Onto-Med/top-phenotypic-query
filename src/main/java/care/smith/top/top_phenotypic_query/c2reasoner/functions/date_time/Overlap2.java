package care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;

public class Overlap2 extends FunctionEntity {

  private static Overlap2 INSTANCE = new Overlap2();

  private Overlap2() {
    super("overlap2", NotationEnum.PREFIX, 2, 4);
  }

  public static Overlap2 get() {
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
    args = c2r.calculateCheckValues(args);
    if (args == null) return Exp.ofFalse();

    List<Value> vals1 = args.get(0).getValues();
    List<Value> vals2 = args.get(1).getValues();

    int distance1 = 0;
    int distance2 = 0;
    if (args.size() > 2) {
      Expression arg3 = args.get(2);
      Exceptions.checkArgumentType(getFunction(), DataType.NUMBER, arg3);
      distance1 = Expressions.getNumberValue(arg3).intValue();
      if (args.size() > 3) {
        Expression arg4 = args.get(3);
        Exceptions.checkArgumentType(getFunction(), DataType.NUMBER, arg4);
        distance2 = Expressions.getNumberValue(arg4).intValue();
      } else distance2 = distance1;
    }

    for (Value v1 : vals1)
      for (Value v2 : vals2)
        if (Values.overlaps2(v1, v2, distance1, distance2)) return Exp.ofTrue();

    return Exp.ofFalse();
  }
}
