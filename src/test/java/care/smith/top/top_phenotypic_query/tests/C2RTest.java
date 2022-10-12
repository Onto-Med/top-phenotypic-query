package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

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
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;

public class C2RTest {

  //  @Test
  //  public void testRestrict1() {
  //    DecimalValue v1 = new DecimalValue(5, "2000-01-01");
  //    DecimalValue v2 = new DecimalValue(10, "2001-01-01");
  //    DecimalValue v3 = new DecimalValue(15, "2002-01-01");
  //    DecimalValueList valList = new DecimalValueList(v1, v2, v3);
  //    ConstantExpression vals = new ConstantExpression(valList);
  //
  //    DecimalValue min = new DecimalValue(5);
  //    DecimalValue max = new DecimalValue(15);
  //    DecimalValueList rangeList = new DecimalValueList(min, max);
  //    ConstantExpression range = new ConstantExpression(rangeList);
  //
  //    StringValue ge = new StringValue("ge");
  //    StringValue lt = new StringValue("lt");
  //    StringValueList limitsList = new StringValueList(ge, lt);
  //    ConstantExpression limits = new ConstantExpression(limitsList);
  //
  //    Calculator c = new Calculator();
  //    MathExpression e = new FunctionExpression("restrict").arg(vals).arg(range).arg(limits);
  //    Value res = c.calculate(e);
  //    assertTrue(res.isValueList());
  //
  //    List<Value> values = res.asValueList().getValues();
  //    assertEquals(
  //        Set.of(5, 10),
  //        values.stream().map(v -> v.getValueDecimal().intValue()).collect(Collectors.toSet()));
  //  }
  //
  //  @Test
  //  public void testRestrict2() {
  //    DecimalValue v1 = new DecimalValue(5, "2000-01-01");
  //    DecimalValue v2 = new DecimalValue(10, "2001-01-01");
  //    DecimalValue v3 = new DecimalValue(15, "2002-01-01");
  //    DecimalValueList valList = new DecimalValueList(v1, v2, v3);
  //    ConstantExpression vals = new ConstantExpression(valList);
  //
  //    DateTimeValue min = new DateTimeValue("2001-01-01");
  //    DateTimeValue max = new DateTimeValue("2002-01-01");
  //    DateTimeValueList rangeList = new DateTimeValueList(min, max);
  //    ConstantExpression range = new ConstantExpression(rangeList);
  //
  //    StringValue ge = new StringValue("ge");
  //    StringValue lt = new StringValue("le");
  //    StringValueList limitsList = new StringValueList(ge, lt);
  //    ConstantExpression limits = new ConstantExpression(limitsList);
  //
  //    Calculator c = new Calculator();
  //    MathExpression e = new FunctionExpression("restrict").arg(vals).arg(range).arg(limits);
  //    Value res = c.calculate(e);
  //    assertTrue(res.isValueList());
  //
  //    List<Value> values = res.asValueList().getValues();
  //    assertEquals(
  //        Set.of(15, 10),
  //        values.stream().map(v -> v.getValueDecimal().intValue()).collect(Collectors.toSet()));
  //  }
  //

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

  //  @Test
  //  public void testSwitch() {
  //    ConstantExpression s1 = new ConstantExpression(new DecimalValue(1));
  //    ConstantExpression s2 = new ConstantExpression(new DecimalValue(2));
  //    ConstantExpression s3 = new ConstantExpression(new DecimalValue(3));
  //    ConstantExpression d = new ConstantExpression(new DecimalValue(-1));
  //
  //    ConstantExpression v1 = new ConstantExpression(new DecimalValue(5));
  //    ConstantExpression v2 = new ConstantExpression(new DecimalValue(10));
  //
  //    Calculator c = new Calculator();
  //
  //    MathExpression e1 = new FunctionExpression("eq").arg(v1).arg(v2);
  //    MathExpression e2 = new FunctionExpression("lt").arg(v1).arg(v2);
  //    MathExpression e3 = new FunctionExpression("gt").arg(v1).arg(v2);
  //    MathExpression res =
  //        new FunctionExpression("switch").arg(e1).arg(s1).arg(e2).arg(s2).arg(e3).arg(s3).arg(d);
  //    assertEquals(new BigDecimal("2"), c.calculate(res).getValueDecimal());
  //
  //    MathExpression res2 = new
  // FunctionExpression("switch").arg(e1).arg(s1).arg(e3).arg(s3).arg(d);
  //    assertEquals(new BigDecimal("-1"), c.calculate(res2).getValueDecimal());
  //
  //    MathExpression res3 = new FunctionExpression("switch").arg(e1).arg(s1).arg(e3).arg(s3);
  //    Exception exception =
  //        assertThrows(
  //            ArithmeticException.class,
  //            () -> {
  //              c.calculate(res3);
  //            });
  //    assertEquals("No default value defined for the function 'switch'!", exception.getMessage());
  //  }
  //
  //  @Test
  //  public void testComparison() {
  //    ConstantExpression v1 = new ConstantExpression(new DecimalValue(3));
  //    ConstantExpression v2 = new ConstantExpression(new DecimalValue(5));
  //    ConstantExpression v3 = new ConstantExpression(new DecimalValue(10));
  //    ConstantExpression v4 = new ConstantExpression(new DecimalValue(12));
  //
  //    Calculator c = new Calculator();
  //    MathExpression e1 =
  //        new FunctionExpression("and")
  //            .arg(new FunctionExpression("eq").arg(v1).arg(v2))
  //            .arg(new FunctionExpression("lt").arg(v3).arg(v4));
  //    assertFalse(c.calculate(e1).asBooleanValue().getValue());
  //
  //    MathExpression e2 =
  //        new FunctionExpression("or")
  //            .arg(new FunctionExpression("eq").arg(v1).arg(v2))
  //            .arg(new FunctionExpression("lt").arg(v3).arg(v4));
  //    assertTrue(c.calculate(e2).asBooleanValue().getValue());
  //
  //    MathExpression e3 =
  //        new FunctionExpression("and")
  //            .arg(new FunctionExpression("eq").arg(v1).arg(v1))
  //            .arg(new FunctionExpression("lt").arg(v3).arg(v4));
  //    assertTrue(c.calculate(e3).asBooleanValue().getValue());
  //
  //    MathExpression e4 =
  //        new FunctionExpression("not")
  //            .arg(
  //                new FunctionExpression("and")
  //                    .arg(new FunctionExpression("eq").arg(v1).arg(v1))
  //                    .arg(new FunctionExpression("lt").arg(v3).arg(v4)));
  //    assertFalse(c.calculate(e4).asBooleanValue().getValue());
  //  }
  //
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

  //  @Test
  //  public void testAge() {
  //    Calculator c1 = new Calculator();
  //    MathExpression ex1 =
  //        new FunctionExpression("diffYears")
  //            .arg(new VariableExpression("a"))
  //            .arg(new VariableExpression("b"));
  //
  //    c1.setVariable("a", new DateTimeValue("1990-08-10"));
  //    c1.setVariable("b", new DateTimeValue("2030-06-16"));
  //
  //    assertEquals(39, c1.calculate(ex1).getValueDecimal().intValue());
  //
  //    Calculator c2 = new Calculator();
  //    MathExpression ex2 =
  //        new FunctionExpression("diffYears")
  //            .arg(new VariableExpression("a"))
  //            .arg(new ConstantExpression("now"));
  //
  //    c2.setVariable("a", new DateTimeValue("1990-08-10"));
  //
  //    assertEquals(
  //        DateUtil.getPeriodInYears(new DateTimeValue("1990-08-10").getValue()).intValue(),
  //        c2.calculate(ex2).getValueDecimal().intValue());
  //  }
  //
  //  @Test
  //  public void testPlusYears() {
  //    Calculator c1 = new Calculator();
  //    MathExpression ex1 =
  //        new FunctionExpression("plusYears")
  //            .arg(new VariableExpression("a"))
  //            .arg(new VariableExpression("b"));
  //
  //    c1.setVariable("a", new DateTimeValue("1990-08-10"));
  //    c1.setVariable("b", 2);
  //
  //    assertEquals(
  //        new DateTimeValue("1992-08-10").getValue(),
  // c1.calculate(ex1).asDateTimeValue().getValue());
  //  }
  //
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
