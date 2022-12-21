package care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.ExpressionBuilder;
import care.smith.top.top_phenotypic_query.util.builder.ValueBuilder;

public class Restrict extends FunctionEntity {

  private static Restrict INSTANCE = new Restrict();

  private Restrict() {
    super(
        new ExpressionFunction()
            .id("restrict")
            .title("restrict")
            .minArgumentNumber(2)
            .maxArgumentNumber(2)
            .notation(NotationEnum.PREFIX));
  }

  public static Restrict get() {
    return INSTANCE;
  }

  @Override
  public Expression calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args, defaultAggregateFunction);
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

    return ExpressionBuilder.of(res);
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
    for (Value v : vals) if (Values.contains(inter, ValueBuilder.of(v.getDateTime()))) res.add(v);
  }

  private void calculateInDateSet(List<Value> vals, List<Value> set, List<Value> res) {
    for (Value v : vals) if (Values.contains(set, ValueBuilder.of(v.getDateTime()))) res.add(v);
  }
}
