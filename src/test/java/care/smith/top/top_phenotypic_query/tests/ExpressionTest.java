package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Expression;
import care.smith.top.model.NumberValue;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.StringValue;
import care.smith.top.top_phenotypic_query.util.Expressions;

public class ExpressionTest extends AbstractTest {

  @Test
  public void test() {
    Phenotype age = getPhenotype("Age", null, null);
    Phenotype res =
        getRestriction(
            "Young", age, DEFAULT_MIN_OPERATOR, 18, DEFAULT_MAX_OPERATOR, 34, Quantifier.EXACT, 5);
    Expression exp =
        getExpression(
            "Young", age, DEFAULT_MIN_OPERATOR, 18, DEFAULT_MAX_OPERATOR, 34, Quantifier.EXACT, 5);
    assertEquals(exp, Expressions.restrictionToExpression(res));
  }

  static Expression getExpression(
      String name,
      Phenotype parent,
      RestrictionOperator minOperator,
      Number min,
      RestrictionOperator maxOperator,
      Number max,
      Quantifier quantifier,
      Integer cardinality) {
    Expression values = new Expression().entityId(parent.getId());
    Expression range = new Expression().functionId("list");
    Expression limits = new Expression().functionId("list");

    if (min != null) {
      addArgument(range, new BigDecimal(min.toString()));
      addArgument(limits, minOperator.getValue());
    }

    if (max != null) {
      addArgument(range, new BigDecimal(max.toString()));
      addArgument(limits, maxOperator.getValue());
    }

    Expression exp =
        new Expression()
            .functionId("in")
            .addArgumentsItem(values)
            .addArgumentsItem(range)
            .addArgumentsItem(limits);

    if (quantifier != null) {
      exp.addArgumentsItem(Expressions.stringToExpression(quantifier.getValue()));
      if (cardinality != null) exp.addArgumentsItem(Expressions.numberToExpression(cardinality));
    }

    return exp;
  }

  private static void addArgument(Expression exp, BigDecimal val) {
    exp.addArgumentsItem(new Expression().value(new NumberValue().value(val)));
  }

  private static void addArgument(Expression exp, String val) {
    exp.addArgumentsItem(new Expression().value(new StringValue().value(val)));
  }
}
