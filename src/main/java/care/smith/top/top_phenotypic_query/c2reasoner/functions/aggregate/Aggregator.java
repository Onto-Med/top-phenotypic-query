package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class Aggregator {

  private static Logger log = LoggerFactory.getLogger(Aggregator.class);

  public static Expression aggregate(Expression arg, C2R c2r) {
    if (!hasMultipleValues(arg)) return arg;
    Expression ag = c2r.getDefaultAggregateFunction().calculate(Exp.toList(arg.getValues()), c2r);
    log.debug(
        "aggregate: {} = {}",
        c2r.getDefaultAggregateFunction().toStringValues(arg.getValues()),
        Values.toStringWithoutDateTime(Expressions.getValue(ag)));
    return ag;
  }

  public static List<Expression> aggregate(List<Expression> args, C2R c2r) {
    if (containsMultipleValues(args))
      return args.stream().map(a -> aggregate(a, c2r)).collect(Collectors.toList());
    return args;
  }

  public static List<Expression> aggregateIfNumber(List<Expression> args, C2R c2r) {
    if (containsMultipleValues(args))
      return args.stream()
          .map(
              a -> {
                if (Expressions.hasNumberType(a)) return aggregate(a, c2r);
                else return a;
              })
          .collect(Collectors.toList());
    return args;
  }

  public static List<Expression> aggregateIfMultiple(List<Expression> args, C2R c2r) {
    if (args.size() > 1) return aggregate(args, c2r);
    return Exp.toList(args.get(0).getValues());
  }

  public static List<Expression> calcAndAggrIfMultipleHaveValues(
      ExpressionFunction f, List<Expression> args, C2R c2r) {
    return calcAndAggrIfMultipleHaveValues(f, null, args, c2r);
  }

  public static List<Expression> calcAndAggrIfMultipleHaveValues(
      ExpressionFunction f, DataType dt, List<Expression> args, C2R c2r) {
    if (args.size() == 1) {
      Expression arg = args.get(0);
      arg = c2r.calculate(arg);
      if (!Expressions.hasValues(arg)) return null;
      if (dt != null) Exceptions.checkArgumentType(f, dt, arg);
      return Exp.toList(arg.getValues());
    } else {
      args = c2r.calculateHaveValues(args);
      if (args.isEmpty()) return null;
      if (dt != null) Exceptions.checkArgumentsType(f, dt, args);
      return aggregate(args, c2r);
    }
  }

  private static boolean containsMultipleValues(List<Expression> args) {
    for (Expression arg : args) if (hasMultipleValues(arg)) return true;
    return false;
  }

  private static boolean hasMultipleValues(Expression arg) {
    return arg.getValues() != null && arg.getValues().size() > 1;
  }
}
