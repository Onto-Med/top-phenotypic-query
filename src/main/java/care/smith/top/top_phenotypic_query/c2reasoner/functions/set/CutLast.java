package care.smith.top.top_phenotypic_query.c2reasoner.functions.set;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;
import java.util.stream.Collectors;

public class CutLast extends FunctionEntity {

  private static final CutLast INSTANCE = new CutLast();

  private CutLast() {
    super("cutLast", NotationEnum.PREFIX);
    minArgumentNumber(1);
  }

  public static CutLast get() {
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
    args = Aggregator.calcAndAggrCheckMultipleHaveValues(getFunction(), args, c2r);
    if (args == null) return null;
    int size = args.size();
    return Exp.of(
        args.stream()
            .sorted(Values.EXP_DATE_COMPARATOR)
            .limit(size - 1)
            .map(e -> Expressions.getValue(e))
            .collect(Collectors.toList()));
  }
}
