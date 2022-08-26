package care.smith.top.top_phenotypic_query.util;

import java.util.HashSet;
import java.util.Set;

import care.smith.top.backend.model.Expression;

public class ExpressionUtil {

  public static Set<String> getVariables(Expression exp) {
    Set<String> vars = new HashSet<>();
    addVariables(exp, vars);
    return vars;
  }

  private static void addVariables(Expression exp, Set<String> vars) {
    if (exp.getEntityId() != null) vars.add(exp.getEntityId());
    else for (Expression arg : exp.getArguments()) addVariables(arg, vars);
  }
}
