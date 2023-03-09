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
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.DateTimeInterval;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.NumberInterval;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class Filter extends FunctionEntity {

  private static Filter INSTANCE = new Filter();

  private Filter() {
    super("filter", NotationEnum.PREFIX, 3, 9);
  }

  public static Filter get() {
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

    for (int i = 1; i < args.size(); i++) {
      if (i % 2 == 0)
        Exceptions.checkArgumentTypes(
            getFunction(), args.get(i), DataType.NUMBER, DataType.DATE_TIME);
      else Exceptions.checkArgumentTypes(getFunction(), args.get(i), DataType.STRING);
    }

    NumberInterval ni = new NumberInterval();
    DateTimeInterval di = new DateTimeInterval();

    for (int i = 1; i < args.size(); i += 2) {
      Expression oper = args.get(i);
      Expression val = args.get(i + 1);
      if (Expressions.hasNumberType(val)) ni.limit(oper, val);
      if (Expressions.hasDateTimeType(val)) di.limit(oper, val);
    }

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
