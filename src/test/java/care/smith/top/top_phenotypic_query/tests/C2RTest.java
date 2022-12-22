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
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class C2RTest {

  @Test
  public void testRestrict1() {
    Value v1 = Val.of(5, DateUtil.parse("2000-01-01"));
    Value v2 = Val.of(10, DateUtil.parse("2001-01-01"));
    Value v3 = Val.of(15, DateUtil.parse("2002-01-01"));
    Value v4 = Val.of(8, DateUtil.parse("2003-01-01"));
    Expression vals = Exp.of(v1, v2, v3, v4);

    Expression rest =
        Exp.of(
            Res.of(
                null,
                RestrictionOperator.GREATER_THAN_OR_EQUAL_TO,
                5,
                RestrictionOperator.LESS_THAN,
                15));

    C2R c = new C2R();
    Expression e =
        new Expression().functionId("restrict").addArgumentsItem(vals).addArgumentsItem(rest);

    assertEquals(
        List.of(BigDecimal.valueOf(5), BigDecimal.valueOf(10), BigDecimal.valueOf(8)),
        Expressions.getNumberValues(c.calculate(e)));
  }

  @Test
  public void testRestrict2() {
    Value v1 = Val.of(5, DateUtil.parse("2000-01-01"));
    Value v2 = Val.of(10, DateUtil.parse("2001-01-01"));
    Value v3 = Val.of(15, DateUtil.parse("2002-01-01"));
    Value v4 = Val.of(8, DateUtil.parse("2003-01-01"));
    Expression vals = Exp.of(v1, v2, v3, v4);

    Expression rest =
        Exp.of(
            Res.of(
                null,
                RestrictionOperator.GREATER_THAN_OR_EQUAL_TO,
                DateUtil.parse("2001-01-01"),
                RestrictionOperator.LESS_THAN,
                DateUtil.parse("2002-01-02")));

    C2R c = new C2R();
    Expression e =
        new Expression().functionId("restrict").addArgumentsItem(vals).addArgumentsItem(rest);

    assertEquals(
        List.of(BigDecimal.valueOf(10), BigDecimal.valueOf(15)),
        Expressions.getNumberValues(c.calculate(e)));
  }

  @Test
  public void testNot() {
    Expression v1 = Exp.ofFalse();
    Expression v2 = Exp.ofTrue();

    C2R c = new C2R();
    Expression e = new Expression().functionId("not").addArgumentsItem(v1);
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e = new Expression().functionId("not").addArgumentsItem(v2);
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testOr() {
    Expression v1 = Exp.ofFalse();
    Expression v2 = Exp.ofTrue();
    Expression v3 = Exp.ofFalse();

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
    Expression v1 = Exp.ofTrue();
    Expression v2 = Exp.ofFalse();
    Expression v3 = Exp.ofTrue();

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
    Expression minTrue = Exp.of(2);
    Expression v1 = Exp.ofTrue();
    Expression v2 = Exp.ofFalse();
    Expression v3 = Exp.ofTrue();

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
    Expression s1 = Exp.of(1);
    Expression s2 = Exp.of(2);
    Expression s3 = Exp.of(3);
    Expression d = Exp.of(-1);

    Expression v1 = Exp.of(5);
    Expression v2 = Exp.of(10);

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
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(5));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("eq")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ne")
            .addArgumentsItem(Exp.of(5))
            .addArgumentsItem(Exp.of(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ne")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(3));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("lt")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(5));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("lt")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(3));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("le")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(5));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("le")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("le")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(2));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));
    e =
        new Expression()
            .functionId("gt")
            .addArgumentsItem(Exp.of(5))
            .addArgumentsItem(Exp.of(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("gt")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(3));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ge")
            .addArgumentsItem(Exp.of(5))
            .addArgumentsItem(Exp.of(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ge")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(3));
    assertTrue(Expressions.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("ge")
            .addArgumentsItem(Exp.of(2))
            .addArgumentsItem(Exp.of(3));
    assertFalse(Expressions.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testAvg() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("avg")
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(5))
            .addArgumentsItem(Exp.of(10));

    assertEquals(
        new BigDecimal("6.00"),
        Expressions.getNumberValue(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));

    c = new C2R();
    e =
        new Expression()
            .functionId("avg")
            .addArgumentsItem(Exp.of(2, 3, 4))
            .addArgumentsItem(Exp.of(5))
            .addArgumentsItem(Exp.of(10));

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
            .addArgumentsItem(Exp.of(5));

    Value a1 = Val.of(12, LocalDateTime.parse("2020-01-02T00:00"));
    Value a2 = Val.of(3, LocalDateTime.parse("2021-01-02T00:00"));
    Value a3 = Val.of(10, LocalDateTime.parse("2022-01-02T00:00"));
    Value a4 = Val.of(7, LocalDateTime.parse("2019-01-02T00:00"));

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

    Value a1 = Val.of(12, LocalDateTime.parse("2020-01-02T00:00"));
    Value a2 = Val.of(3, LocalDateTime.parse("2021-01-02T00:00"));
    Value a3 = Val.of(10, LocalDateTime.parse("2022-01-02T00:00"));
    Value a4 = Val.of(7, LocalDateTime.parse("2019-01-02T00:00"));

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
            .addArgumentsItem(Exp.of(5))
            .addArgumentsItem(Exp.of(3))
            .addArgumentsItem(Exp.of(10));
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
            .addArgumentsItem(Exp.of(5))
            .addArgumentsItem(Exp.of(13))
            .addArgumentsItem(Exp.of(10));
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
            .addArgumentsItem(Exp.of(DateUtil.parse("1990-08-10")))
            .addArgumentsItem(Exp.of(DateUtil.parse("2030-06-16")));
    assertEquals(39, Expressions.getNumberValue(c.calculate(e)).intValue());

    e =
        new Expression()
            .functionId("diffYears")
            .addArgumentsItem(Exp.of(DateUtil.parse("1990-08-10")))
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
            .addArgumentsItem(Exp.of(DateUtil.parse("1990-08-10")))
            .addArgumentsItem(Exp.of(2));
    assertEquals(DateUtil.parse("1992-08-10"), Expressions.getDateTimeValue(c.calculate(e)));
  }

  @Test
  public void testInSet() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(2, 10))
            .addArgumentsItem(Exp.of(5, 6, 7));
    assertTrue(Expressions.getBooleanValue(c.calculate(e, Avg.get())));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(2, 10))
            .addArgumentsItem(Exp.of(5, 7));
    assertFalse(Expressions.getBooleanValue(c.calculate(e, Avg.get())));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(1, 2))
            .addArgumentsItem(Exp.of(Res.of(Quantifier.ALL, 1, 2, 3)));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(1, 2, 4))
            .addArgumentsItem(Exp.of(Res.of(Quantifier.ALL, 1, 2, 3)));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(5, 6, 2))
            .addArgumentsItem(
                Exp.of(Res.of(1, Quantifier.MIN, 1, 2, 3)));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(5, 6, 7))
            .addArgumentsItem(
                Exp.of(Res.of(1, Quantifier.MIN, 1, 2, 3)));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(5, 6, 2))
            .addArgumentsItem(
                Exp.of(Res.of(2, Quantifier.MIN, 1, 2, 3)));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(5, 1, 2))
            .addArgumentsItem(
                Exp.of(Res.of(2, Quantifier.MIN, 1, 2, 3)));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));
  }

  @Test
  public void testInInterval() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(11, 20))
            .addArgumentsItem(
                Exp.of(
                    Res.of(
                        Quantifier.ALL,
                        RestrictionOperator.GREATER_THAN,
                        10,
                        RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                        20)));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(11, 21))
            .addArgumentsItem(
                Exp.of(
                    Res.of(
                        Quantifier.ALL,
                        RestrictionOperator.GREATER_THAN,
                        10,
                        RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                        20)));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(11, 21))
            .addArgumentsItem(
                Exp.of(
                    Res.of(
                        1,
                        Quantifier.MIN,
                        RestrictionOperator.GREATER_THAN,
                        10,
                        RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                        20)));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(10, 21))
            .addArgumentsItem(
                Exp.of(
                    Res.of(
                        1,
                        Quantifier.MIN,
                        RestrictionOperator.GREATER_THAN,
                        10,
                        RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                        20)));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(15, 6, 21))
            .addArgumentsItem(
                Exp.of(
                    Res.of(
                        2,
                        Quantifier.MIN,
                        RestrictionOperator.GREATER_THAN,
                        10,
                        RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                        20)));
    assertFalse(Expressions.getBooleanValue(c.calculate(e)));

    c = new C2R();
    e =
        new Expression()
            .functionId("in")
            .addArgumentsItem(Exp.of(15, 6, 20))
            .addArgumentsItem(
                Exp.of(
                    Res.of(
                        2,
                        Quantifier.MIN,
                        RestrictionOperator.GREATER_THAN,
                        10,
                        RestrictionOperator.LESS_THAN_OR_EQUAL_TO,
                        20)));
    assertTrue(Expressions.getBooleanValue(c.calculate(e)));
  }
}
