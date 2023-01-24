package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class Restrict extends FunctionEntity {

  private static Restrict INSTANCE = new Restrict();

  private Restrict() {
    super("restrict", NotationEnum.PREFIX, 2, 2);
  }

  public static Restrict get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args);
    if (args == null) return null;
    Exceptions.checkArgumentTypes(getFunction(), args.get(1), DataType.NUMBER, DataType.DATE_TIME);

    List<Value> vals = args.get(0).getValues();
    Restriction r = args.get(1).getRestriction();
    List<Value> res = new ArrayList<>();

    if (Restrictions.hasInterval(r)) {
      if (Restrictions.hasNumberType(r))
        calculateInNumberInterval(vals, Restrictions.getInterval(r), res);
      else calculateInDateInterval(vals, Restrictions.getInterval(r), res);
    } else {
      if (Restrictions.hasNumberType(r)) calculateInNumberSet(vals, Restrictions.getValues(r), res);
      else calculateInDateSet(vals, Restrictions.getValues(r), res);
    }

    return Exp.of(res);
  }

  private void calculateInNumberInterval(
      List<Value> vals, Map<RestrictionOperator, Value> inter, List<Value> res) {
    for (Value v : vals) if (Values.contains(inter, v)) res.add(v);
  }

  private void calculateInNumberSet(List<Value> vals, List<Value> set, List<Value> res) {
    for (Value v : vals) if (Values.contains(set, v)) res.add(v);
  }

  private void calculateInDateInterval(
      List<Value> vals, Map<RestrictionOperator, Value> inter, List<Value> res) {
    for (Value v : vals) if (Values.contains(inter, Val.of(v.getDateTime()))) res.add(v);
  }

  private void calculateInDateSet(List<Value> vals, List<Value> set, List<Value> res) {
    for (Value v : vals) if (Values.contains(set, Val.of(v.getDateTime()))) res.add(v);
  }
}
