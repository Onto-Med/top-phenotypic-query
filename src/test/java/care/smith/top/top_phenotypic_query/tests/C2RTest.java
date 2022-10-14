package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.NumberValue;
import care.smith.top.model.Quantifier;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Avg;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.First;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Last;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Max;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Min;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;

public class C2RTest {

  @Test
  public void testRestrict1() {
    Value v1 = Values.newValue(5, DateUtil.parse("2000-01-01"));
    Value v2 = Values.newValue(10, DateUtil.parse("2001-01-01"));
    Value v3 = Values.newValue(15, DateUtil.parse("2002-01-01"));
    Value v4 = Values.newValue(8, DateUtil.parse("2003-01-01"));
    Expression vals = Expressions.newExpression(v1, v2, v3, v4);

    Expression rest =
        Expressions.newExpression(
            null,
            RestrictionOperator.GREATER_THAN_OR_EQUAL_TO,
            5,
            RestrictionOperator.LESS_THAN,
            15);

    C2R c = new C2R();
    Expression e =
        new Expression().functionId("restrict").addArgumentsItem(vals).addArgumentsItem(rest);

    assertEquals(
        List.of(BigDecimal.valueOf(5), BigDecimal.valueOf(10), BigDecimal.valueOf(8)),
        Expressions.getNumberValues(c.calculate(e)));
  }

  @Test
  public void testRestrict2() {
    Value v1 = Values.newValue(5, DateUtil.parse("2000-01-01"));
    Value v2 = Values.newValue(10, DateUtil.parse("2001-01-01"));
    Value v3 = Values.newValue(15, DateUtil.parse("2002-01-01"));
    Value v4 = Values.newValue(8, DateUtil.parse("2003-01-01"));
    Expression vals = Expressions.newExpression(v1, v2, v3, v4);

    Expression rest =
        Expressions.newExpression(
            null,
            RestrictionOperator.GREATER_THAN_OR_EQUAL_TO,
            DateUtil.parse("2001-01-01"),
            RestrictionOperator.LESS_THAN,
            DateUtil.parse("2002-01-02"));

    C2R c = new C2R();
    Expression e =
        new Expression().functionId("restrict").addArgumentsItem(vals).addArgumentsItem(rest);

    assertEquals(
        List.of(BigDecimal.valueOf(10), BigDecimal.valueOf(15)),
        Expressions.getNumberValues(c.calculate(e)));
  }

  @Test
  public void testNot() {
    Expression v1 = Expressions.newExpressionFalse();
    Expression v2 = Expressions.newExpressionTrue();

    C2R c = new C2R();
    Expression e = new Expression().functionId("not").addArgumentsItem(v1);
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e = new Expression().functionId("not").addArgumentsItem(v2);
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testOr() {
    Expression v1 = Expressions.newExpressionFalse();
    Expression v2 = Expressions.newExpressionTrue();
    Expression v3 = Expressions.newExpressionFalse();

    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("or")
            .addArgumentsItem(v1)
            .addArgumentsItem(v2)
            .addArgumentsItem(v3);
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e = new Expression().functionId("or").addArgumentsItem(v1).addArgumentsItem(v3);
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testAnd() {
    Expression v1 = Expressions.newExpressionTrue();
    Expression v2 = Expressions.newExpressionFalse();
    Expression v3 = Expressions.newExpressionTrue();

    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("and")
            .addArgumentsItem(v1)
            .addArgumentsItem(v2)
            .addArgumentsItem(v3);
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e = new Expression().functionId("and").addArgumentsItem(v1).addArgumentsItem(v3);
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testMinTrue() {
    Expression minTrue = Expressions.newExpression(2);
    Expression v1 = Expressions.newExpressionTrue();
    Expression v2 = Expressions.newExpressionFalse();
    Expression v3 = Expressions.newExpressionTrue();

    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("minTrue")
            .addArgumentsItem(minTrue)
            .addArgumentsItem(v1)
            .addArgumentsItem(v2);
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("minTrue")
            .addArgumentsItem(minTrue)
            .addArgumentsItem(v1)
            .addArgumentsItem(v2)
            .addArgumentsItem(v3);
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testSwitch() {
    Expression s1 = Expressions.newExpression(1);
    Expression s2 = Expressions.newExpression(2);
    Expression s3 = Expressions.newExpression(3);
    Expression d = Expressions.newExpression(-1);

    Expression v1 = Expressions.newExpression(5);
    Expression v2 = Expressions.newExpression(10);

    C2R c = new C2R();

    Expression e1 = new Expression().functionId("eq").addArgumentsItem(v1).addArgumentsItem(v2);
    Expression e2 = new Expression().functionId("lt").addArgumentsItem(v1).addArgumentsItem(v2);
    Expression e3 = new Expression().functionId("gt").addArgumentsItem(v1).addArgumentsItem(v2);

    Expression res =
        new Expression()
            .functionId("switch")
            .addArgumentsItem(e1)
            .addArgumentsItem(s1)
            .addArgumentsItem(e2)
            .addArgumentsItem(s2)
            .addArgumentsItem(e3)
            .addArgumentsItem(s3);

    assertEquals(new BigDecimal("2"), Expressions.getNumberValue(c.calculate(res)));

    res =
        new Expression()
            .functionId("switch")
            .addArgumentsItem(e1)
            .addArgumentsItem(s1)
            .addArgumentsItem(e3)
            .addArgumentsItem(s3)
            .addArgumentsItem(d);

    assertEquals(new BigDecimal("-1"), Expressions.getNumberValue(c.calculate(res)));

    Expression res1 =
        new Expression()
            .functionId("switch")
            .addArgumentsItem(e1)
            .addArgumentsItem(s1)
            .addArgumentsItem(e3)
            .addArgumentsItem(s3);

    Exception exception =
        assertThrows(
            ArithmeticException.class,
            () -> {
              c.calculate(res1);
            });
    assertEquals("No default value defined for the function 'switch'!", exception.getMessage());
  }

  @Test
  public void testComparison() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("eq")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(5));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("eq")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ne")
            .addArgumentsItem(Expressions.newExpression(5))
            .addArgumentsItem(Expressions.newExpression(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ne")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(3));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("lt")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(5));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("lt")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(3));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("le")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(5));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("le")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("le")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(2));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));
    e =
        new Expression()
            .functionId("gt")
            .addArgumentsItem(Expressions.newExpression(5))
            .addArgumentsItem(Expressions.newExpression(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("gt")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(3));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ge")
            .addArgumentsItem(Expressions.newExpression(5))
            .addArgumentsItem(Expressions.newExpression(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ge")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ge")
            .addArgumentsItem(Expressions.newExpression(2))
            .addArgumentsItem(Expressions.newExpression(3));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testAvg() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("avg")
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(5))
            .addArgumentsItem(Expressions.newExpression(10));

    assertEquals(
        new BigDecimal("6.00"),
        Expressions.getNumberValue(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));

    c = new C2R();
    e =
        new Expression()
            .functionId("avg")
            .addArgumentsItem(Expressions.newExpression(2, 3, 4))
            .addArgumentsItem(Expressions.newExpression(5))
            .addArgumentsItem(Expressions.newExpression(10));

    assertEquals(
        new BigDecimal("6.00"),
        Expressions.getNumberValue(c.calculate(e, Avg.get())).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testAvg2() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("avg")
            .addArgumentsItem(new Expression().entityId("a"))
            .addArgumentsItem(Expressions.newExpression(5));

    Value a1 = Values.newValue(12, LocalDateTime.parse("2020-01-02T00:00"));
    Value a2 = Values.newValue(3, LocalDateTime.parse("2021-01-02T00:00"));
    Value a3 = Values.newValue(10, LocalDateTime.parse("2022-01-02T00:00"));
    Value a4 = Values.newValue(7, LocalDateTime.parse("2019-01-02T00:00"));

    c.setVariable("a", a1, a2, a3, a4);

    assertEquals(
        new BigDecimal("4.00"),
        Expressions.getNumberValue(c.calculate(e, Min.get())).setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("8.50"),
        Expressions.getNumberValue(c.calculate(e, Max.get())).setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("6.00"),
        Expressions.getNumberValue(c.calculate(e, First.get())).setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("7.50"),
        Expressions.getNumberValue(c.calculate(e, Last.get())).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testAvg3() {
    C2R c = new C2R();
    Expression e =
        new Expression().functionId("avg").addArgumentsItem(new Expression().entityId("a"));

    Value a1 = Values.newValue(12, LocalDateTime.parse("2020-01-02T00:00"));
    Value a2 = Values.newValue(3, LocalDateTime.parse("2021-01-02T00:00"));
    Value a3 = Values.newValue(10, LocalDateTime.parse("2022-01-02T00:00"));
    Value a4 = Values.newValue(7, LocalDateTime.parse("2019-01-02T00:00"));

    c.setVariable("a", a1, a2, a3, a4);

    assertEquals(
        new BigDecimal("8.00"),
        Expressions.getNumberValue(c.calculate(e, Min.get())).setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("8.00"),
        Expressions.getNumberValue(c.calculate(e, Max.get())).setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("8.00"),
        Expressions.getNumberValue(c.calculate(e, First.get())).setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("8.00"),
        Expressions.getNumberValue(c.calculate(e, Last.get())).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testMin() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("min")
            .addArgumentsItem(Expressions.newExpression(5))
            .addArgumentsItem(Expressions.newExpression(3))
            .addArgumentsItem(Expressions.newExpression(10));
    assertEquals(
        new BigDecimal("3.00"),
        Expressions.getNumberValue(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testMax() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("max")
            .addArgumentsItem(Expressions.newExpression(5))
            .addArgumentsItem(Expressions.newExpression(13))
            .addArgumentsItem(Expressions.newExpression(10));
    assertEquals(
        new BigDecimal("13.00"),
        Expressions.getNumberValue(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testBMI() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("divide")
            .addArgumentsItem(new Expression().entityId("m"))
            .addArgumentsItem(
                new Expression()
                    .functionId("power")
                    .addArgumentsItem(new Expression().entityId("l"))
                    .addArgumentsItem(
                        new Expression()
                            .value(
                                ((NumberValue) new NumberValue().dataType(DataType.NUMBER))
                                    .value(new BigDecimal(2)))));

    c.setVariable("m", 65);
    c.setVariable("l", 1.78);

    assertEquals(
        new BigDecimal("20.52"),
        Expressions.getNumberValue(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void test1() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("multiply")
            .addArgumentsItem(new Expression().entityId("a"))
            .addArgumentsItem(
                new Expression()
                    .functionId("add")
                    .addArgumentsItem(new Expression().entityId("b"))
                    .addArgumentsItem(new Expression().entityId("c")));

    c.setVariable("a", 2);
    c.setVariable("b", 3);
    c.setVariable("c", 4);

    assertEquals(new BigDecimal("14.00"), Expressions.getNumberValue(c.calculate(e)).setScale(2));
  }

  @Test
  public void test2() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("power")
            .addArgumentsItem(new Expression().entityId("a"))
            .addArgumentsItem(new Expression().entityId("b"));

    c.setVariable("a", new NumberValue().value(new BigDecimal(-5)).dataType(DataType.NUMBER));
    c.setVariable("b", 3);

    assertEquals(new BigDecimal("-125.00"), Expressions.getNumberValue(c.calculate(e)).setScale(2));
  }

  @Test
  public void test3() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("power")
            .addArgumentsItem(new Expression().constantId("pi"))
            .addArgumentsItem(new Expression().constantId("e"));

    assertEquals(
        new BigDecimal("22.46"),
        Expressions.getNumberValue(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testAge() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("diffYears")
            .addArgumentsItem(Expressions.newExpression(DateUtil.parse("1990-08-10")))
            .addArgumentsItem(Expressions.newExpression(DateUtil.parse("2030-06-16")));
    assertEquals(39, Expressions.getNumberValue(c.calculate(e)).intValue());

    e =
        new Expression()
            .functionId("diffYears")
            .addArgumentsItem(Expressions.newExpression(DateUtil.parse("1990-08-10")))
            .addArgumentsItem(new Expression().constantId("now"));
    assertEquals(
        DateUtil.getPeriodInYears(DateUtil.parse("1990-08-10")).intValue(),
        Expressions.getNumberValue(c.calculate(e)).intValue());
  }

  @Test
  public void testPlusYears() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("plusYears")
            .addArgumentsItem(Expressions.newExpression(DateUtil.parse("1990-08-10")))
            .addArgumentsItem(Expressions.newExpression(2));
    assertEquals(DateUtil.parse("1992-08-10"), Expressions.getDateTimeValue(c.calculate(e)));
  }

  @Test
  public void testInSet() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(2, 10))
            .addArgumentsItem(Expressions.newExpression(5, 6, 7));
    assertTrue(Expressions.getBooleanValue(c.calculate(e, Avg.get())));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(2, 10))
            .addArgumentsItem(Expressions.newExpression(5, 7));
    assertFalse(Expressions.getBooleanValue(c.calculate(e, Avg.get())));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(1, 2))
            .addArgumentsItem(Expressions.newExpression(Quantifier.ALL, 1, 2, 3));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(1, 2, 4))
            .addArgumentsItem(Expressions.newExpression(Quantifier.ALL, 1, 2, 3));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(5, 6, 2))
            .addArgumentsItem(Expressions.newExpression(1, Quantifier.MIN, 1, 2, 3));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(5, 6, 7))
            .addArgumentsItem(Expressions.newExpression(1, Quantifier.MIN, 1, 2, 3));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(5, 6, 2))
            .addArgumentsItem(Expressions.newExpression(2, Quantifier.MIN, 1, 2, 3));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(5, 1, 2))
            .addArgumentsItem(Expressions.newExpression(2, Quantifier.MIN, 1, 2, 3));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));
  }

  @Test
  public void testInInterval() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(11, 20))
            .addArgumentsItem(
                Expressions.newExpression(
                    Quantifier.ALL,
                    RestrictionOperator.GREATER_THAN,
                    10,
                    RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                    20));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(11, 21))
            .addArgumentsItem(
                Expressions.newExpression(
                    Quantifier.ALL,
                    RestrictionOperator.GREATER_THAN,
                    10,
                    RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                    20));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(11, 21))
            .addArgumentsItem(
                Expressions.newExpression(
                    1,
                    Quantifier.MIN,
                    RestrictionOperator.GREATER_THAN,
                    10,
                    RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                    20));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(10, 21))
            .addArgumentsItem(
                Expressions.newExpression(
                    1,
                    Quantifier.MIN,
                    RestrictionOperator.GREATER_THAN,
                    10,
                    RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                    20));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(15, 6, 21))
            .addArgumentsItem(
                Expressions.newExpression(
                    2,
                    Quantifier.MIN,
                    RestrictionOperator.GREATER_THAN,
                    10,
                    RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                    20));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Expressions.newExpression(15, 6, 20))
            .addArgumentsItem(
                Expressions.newExpression(
                    2,
                    Quantifier.MIN,
                    RestrictionOperator.GREATER_THAN,
                    10,
                    RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                    20));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));
  }
}
