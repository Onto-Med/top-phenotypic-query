package care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

public class Aggregator {

  private static Logger log = LoggerFactory.getLogger(Aggregator.class);

  public static List<Expression> aggregate(
      List<Expression> args, FunctionEntity function, C2R c2r) {
    if (containsLists(args))
      return args.stream().map(a -> aggregate(a, function, c2r)).collect(Collectors.toList());
    return args;
  }

  public static List<Expression> aggregateIfMultiple(
      List<Expression> args, FunctionEntity function, C2R c2r) {
    if (args.size() > 1) return aggregate(args, function, c2r);
    return argToList(args.get(0));
  }

  public static List<Expression> argToList(Expression arg) {
    if (arg.getValues() != null) return ValueUtil.toExpressionList(arg.getValues());
    return List.of(arg);
  }

  public static Expression aggregate(Expression arg, FunctionEntity function, C2R c2r) {
    if (arg.getValues() == null || arg.getValues().isEmpty()) return arg;
    else {
      Expression ag =
          function.calculate(ValueUtil.toExpressionList(arg.getValues()), function, c2r);
      log.info(
          "aggregate: {} = {}",
          function.toStringValues(arg.getValues()),
          ValueUtil.getValueAsString(ag.getValue()));
      return ag;
    }
  }

  public static List<Expression> flatten(List<Expression> args) {
    List<Expression> vals = new ArrayList<>();
    for (Expression arg : args) {
      if (arg.getValues() != null) vals.addAll(ValueUtil.toExpressionList(arg.getValues()));
      else vals.add(arg);
    }
    return vals;
  }

  private static boolean containsLists(List<Expression> args) {
    for (Expression arg : args) if (arg.getValues() != null) return true;
    return false;
  }
}
