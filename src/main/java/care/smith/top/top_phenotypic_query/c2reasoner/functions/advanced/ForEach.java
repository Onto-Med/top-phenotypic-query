package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class ForEach extends FunctionEntity {

  private static ForEach INSTANCE = new ForEach();

  private ForEach() {
    super("forEach", NotationEnum.PREFIX, 2, 2);
  }

  public static ForEach get() {
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

    Expression phe = args.get(0);
    Expression exp = args.get(1);

    if (phe.getEntityId() == null || (exp.getEntityId() == null && exp.getFunctionId() == null))
      return null;

    List<Value> vals = c2r.getValues(phe.getEntityId());
    if (vals == null || vals.isEmpty()) return null;

    return Exp.of(
        vals.stream()
            .map(v -> calculate(v, phe.getEntityId(), exp, c2r))
            .filter(v -> v != null)
            .collect(Collectors.toList()));
  }

  private Value calculate(Value val, String pheId, Expression exp, C2R c2r) {
    Value i = Val.of(val).putFieldsItem("entityId", Val.of(pheId));
    Expression newExp = replace(i, pheId, exp, c2r);
    if (newExp == null) return null;
    Expression res = c2r.calculate(newExp);
    if (res == null) return null;
    return Expressions.getValue(res);
  }

  private Expression replace(Value i, String pheId, Expression exp, C2R c2r) {
    if (exp == null) return null;
    if (Objects.equals(pheId, exp.getEntityId())) return Exp.of(i);
    if (exp.getConstantId() != null || exp.getRestriction() != null || exp.getValues() != null)
      return exp;
    if (exp.getEntityId() != null)
      return replace(i, pheId, c2r.getPhenotype(exp.getEntityId()).getExpression(), c2r);
    if (exp.getFunctionId() != null) {
      Expression newExp = new Expression().functionId(exp.getFunctionId());
      for (Expression arg : exp.getArguments())
        newExp.addArgumentsItem(replace(i, pheId, arg, c2r));
      return newExp;
    }
    return null;
  }
}
