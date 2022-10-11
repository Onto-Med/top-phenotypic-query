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
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Avg;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.First;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Last;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Max;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Min;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

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
    Expression v1 = ValueUtil.getExpressionFalse();
    Expression v2 = ValueUtil.getExpressionTrue();

    C2R c = new C2R();
    Expression e = new Expression().functionId("not").addArgumentsItem(v1);
    assertTrue(ValueUtil.hasValueTrue(c.calculate(e)));

    e = new Expression().functionId("not").addArgumentsItem(v2);
    assertFalse(ValueUtil.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testOr() {
    Expression v1 = ValueUtil.getExpressionFalse();
    Expression v2 = ValueUtil.getExpressionTrue();
    Expression v3 = ValueUtil.getExpressionFalse();

    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("or")
            .addArgumentsItem(v1)
            .addArgumentsItem(v2)
            .addArgumentsItem(v3);
    assertTrue(ValueUtil.hasValueTrue(c.calculate(e)));

    e = new Expression().functionId("or").addArgumentsItem(v1).addArgumentsItem(v3);
    assertFalse(ValueUtil.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testAnd() {
    Expression v1 = ValueUtil.getExpressionTrue();
    Expression v2 = ValueUtil.getExpressionFalse();
    Expression v3 = ValueUtil.getExpressionTrue();

    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("and")
            .addArgumentsItem(v1)
            .addArgumentsItem(v2)
            .addArgumentsItem(v3);
    assertFalse(ValueUtil.hasValueTrue(c.calculate(e)));

    e = new Expression().functionId("and").addArgumentsItem(v1).addArgumentsItem(v3);
    assertTrue(ValueUtil.hasValueTrue(c.calculate(e)));
  }

  @Test
  public void testMinTrue() {
    Expression minTrue = ValueUtil.toExpression(2);
    Expression v1 = ValueUtil.getExpressionTrue();
    Expression v2 = ValueUtil.getExpressionFalse();
    Expression v3 = ValueUtil.getExpressionTrue();

    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("minTrue")
            .addArgumentsItem(minTrue)
            .addArgumentsItem(v1)
            .addArgumentsItem(v2);
    assertFalse(ValueUtil.hasValueTrue(c.calculate(e)));

    e =
        new Expression()
            .functionId("minTrue")
            .addArgumentsItem(minTrue)
            .addArgumentsItem(v1)
            .addArgumentsItem(v2)
            .addArgumentsItem(v3);
    assertTrue(ValueUtil.hasValueTrue(c.calculate(e)));
  }
  //
  //  @Test
  //  public void testList() {
  //    DecimalValue v1 = new DecimalValue(1);
  //    DecimalValue v2 = new DecimalValue(2);
  //    DecimalValue v3 = new DecimalValue(3);
  //    ConstantExpression s1 = new ConstantExpression(v1);
  //    ConstantExpression s2 = new ConstantExpression(v2);
  //    ConstantExpression s3 = new ConstantExpression(v3);
  //
  //    Calculator c = new Calculator();
  //    MathExpression e = new FunctionExpression("list").arg(s1).arg(s2).arg(s3);
  //    Value res = c.calculate(e);
  //
  //    assertTrue(res.isValueList());
  //    assertEquals(List.of(v1, v2, v3), res.asValueList().getValues());
  //  }
  //
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
            .addArgumentsItem(ValueUtil.toExpression(3))
            .addArgumentsItem(ValueUtil.toExpression(5))
            .addArgumentsItem(ValueUtil.toExpression(10));

    assertEquals(
        new BigDecimal("6.00"),
        ExpressionUtil.getValueNumber(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));

    c = new C2R();
    e =
        new Expression()
            .functionId("avg")
            .addArgumentsItem(ValueUtil.toExpression(2, 3, 4))
            .addArgumentsItem(ValueUtil.toExpression(5))
            .addArgumentsItem(ValueUtil.toExpression(10));

    assertEquals(
        new BigDecimal("6.00"),
        ExpressionUtil.getValueNumber(c.calculate(e, Avg.get())).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testAvg2() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("avg")
            .addArgumentsItem(new Expression().entityId("a"))
            .addArgumentsItem(ValueUtil.toExpression(5));

    Value a1 = ValueUtil.toValue(12, LocalDateTime.parse("2020-01-02T00:00"));
    Value a2 = ValueUtil.toValue(3, LocalDateTime.parse("2021-01-02T00:00"));
    Value a3 = ValueUtil.toValue(10, LocalDateTime.parse("2022-01-02T00:00"));
    Value a4 = ValueUtil.toValue(7, LocalDateTime.parse("2019-01-02T00:00"));

    c.setVariable("a", a1, a2, a3, a4);

    assertEquals(
        new BigDecimal("4.00"),
        ExpressionUtil.getValueNumber(c.calculate(e, Min.get())).setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("8.50"),
        ExpressionUtil.getValueNumber(c.calculate(e, Max.get())).setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("6.00"),
        ExpressionUtil.getValueNumber(c.calculate(e, First.get()))
            .setScale(2, RoundingMode.HALF_UP));
    assertEquals(
        new BigDecimal("7.50"),
        ExpressionUtil.getValueNumber(c.calculate(e, Last.get()))
            .setScale(2, RoundingMode.HALF_UP));
  }
  //
  //  @Test
  //  public void testAvg3() {
  //    Calculator c = new Calculator();
  //    MathExpression e = new FunctionExpression("avg").arg(new VariableExpression("a"));
  //
  //    DecimalValue a1 = new DecimalValue(12, LocalDateTime.parse("2020-01-02T00:00"));
  //    DecimalValue a2 = new DecimalValue(3, LocalDateTime.parse("2021-01-02T00:00"));
  //    DecimalValue a3 = new DecimalValue(10, LocalDateTime.parse("2022-01-02T00:00"));
  //    DecimalValue a4 = new DecimalValue(7, LocalDateTime.parse("2019-01-02T00:00"));
  //
  //    c.setVariable("a", a1, a2, a3, a4);
  //
  //    assertEquals(
  //        new BigDecimal("8.00"),
  //        c.calculate(e, Min.get()).getValueDecimal().setScale(2, RoundingMode.HALF_UP));
  //    assertEquals(
  //        new BigDecimal("8.00"),
  //        c.calculate(e, Max.get()).getValueDecimal().setScale(2, RoundingMode.HALF_UP));
  //    assertEquals(
  //        new BigDecimal("8.00"),
  //        c.calculate(e, First.get()).getValueDecimal().setScale(2, RoundingMode.HALF_UP));
  //    assertEquals(
  //        new BigDecimal("8.00"),
  //        c.calculate(e, Last.get()).getValueDecimal().setScale(2, RoundingMode.HALF_UP));
  //  }
  //
  @Test
  public void testMin() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("min")
            .addArgumentsItem(ValueUtil.toExpression(5))
            .addArgumentsItem(ValueUtil.toExpression(3))
            .addArgumentsItem(ValueUtil.toExpression(10));
    assertEquals(
        new BigDecimal("3.00"),
        ExpressionUtil.getValueNumber(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testMax() {
    C2R c = new C2R();
    Expression e =
        new Expression()
            .functionId("max")
            .addArgumentsItem(ValueUtil.toExpression(5))
            .addArgumentsItem(ValueUtil.toExpression(13))
            .addArgumentsItem(ValueUtil.toExpression(10));
    assertEquals(
        new BigDecimal("13.00"),
        ExpressionUtil.getValueNumber(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));
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
        ExpressionUtil.getValueNumber(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));
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

    assertEquals(
        new BigDecimal("14.00"), ExpressionUtil.getValueNumber(c.calculate(e)).setScale(2));
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

    assertEquals(
        new BigDecimal("-125.00"), ExpressionUtil.getValueNumber(c.calculate(e)).setScale(2));
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
        ExpressionUtil.getValueNumber(c.calculate(e)).setScale(2, RoundingMode.HALF_UP));
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
  //  @Test
  //  public void testInSet() {
  //    Calculator c1 = new Calculator();
  //    MathExpression ex1 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("all")));
  //
  //    c1.setVariable("x", 1, 2);
  //    c1.setVariable("s", 1, 2, 3);
  //    c1.setVariable("l", new Number[] {});
  //    assertEquals(new BooleanValue(true).getValue(),
  // c1.calculate(ex1).asBooleanValue().getValue());
  //
  //    Calculator c2 = new Calculator();
  //    MathExpression ex2 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("all")));
  //
  //    c2.setVariable("x", 1, 2, 4);
  //    c2.setVariable("s", 1, 2, 3);
  //    c2.setVariable("l", new Number[] {});
  //    assertEquals(new BooleanValue(false).getValue(),
  // c2.calculate(ex2).asBooleanValue().getValue());
  //
  //    Calculator c3 = new Calculator();
  //    MathExpression ex3 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("some")));
  //
  //    c3.setVariable("x", 5, 6, 2);
  //    c3.setVariable("s", 1, 2, 3);
  //    c3.setVariable("l", new Number[] {});
  //    assertEquals(new BooleanValue(true).getValue(),
  // c3.calculate(ex3).asBooleanValue().getValue());
  //
  //    Calculator c4 = new Calculator();
  //    MathExpression ex4 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("some")));
  //
  //    c4.setVariable("x", 5, 6, 7);
  //    c4.setVariable("s", 1, 2, 3);
  //    c4.setVariable("l", new Number[] {});
  //    assertEquals(new BooleanValue(false).getValue(),
  // c4.calculate(ex4).asBooleanValue().getValue());
  //
  //    Calculator c5 = new Calculator();
  //    MathExpression ex5 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("min")))
  //            .arg(new ConstantExpression(new DecimalValue("2")));
  //
  //    c5.setVariable("x", 5, 6, 2);
  //    c5.setVariable("s", 1, 2, 3);
  //    c5.setVariable("l", new Number[] {});
  //    assertEquals(new BooleanValue(false).getValue(),
  // c5.calculate(ex5).asBooleanValue().getValue());
  //
  //    Calculator c6 = new Calculator();
  //    MathExpression ex6 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("min")))
  //            .arg(new ConstantExpression(new DecimalValue("2")));
  //
  //    c6.setVariable("x", 5, 1, 2);
  //    c6.setVariable("s", 1, 2, 3);
  //    c6.setVariable("l", new Number[] {});
  //    assertEquals(new BooleanValue(true).getValue(),
  // c6.calculate(ex6).asBooleanValue().getValue());
  //  }
  //
  //  @Test
  //  public void testInInterval() {
  //    Calculator c1 = new Calculator();
  //    MathExpression ex1 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("all")));
  //
  //    c1.setVariable("x", 11, 20);
  //    c1.setVariable("s", 10, 20);
  //    c1.setVariable("l", ">", "<=");
  //    assertEquals(new BooleanValue(true).getValue(),
  // c1.calculate(ex1).asBooleanValue().getValue());
  //
  //    Calculator c2 = new Calculator();
  //    MathExpression ex2 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("all")));
  //
  //    c2.setVariable("x", 11, 21);
  //    c2.setVariable("s", 10, 20);
  //    c2.setVariable("l", "gt", "<=");
  //    assertEquals(new BooleanValue(false).getValue(),
  // c2.calculate(ex2).asBooleanValue().getValue());
  //
  //    Calculator c3 = new Calculator();
  //    MathExpression ex3 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("some")));
  //
  //    c3.setVariable("x", 11, 21);
  //    c3.setVariable("s", 10, 20);
  //    c3.setVariable("l", ">", "<=");
  //    assertEquals(new BooleanValue(true).getValue(),
  // c3.calculate(ex3).asBooleanValue().getValue());
  //
  //    Calculator c4 = new Calculator();
  //    MathExpression ex4 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("some")));
  //
  //    c4.setVariable("x", 5, 6);
  //    c4.setVariable("s", 10, 20);
  //    c4.setVariable("l", "gt", "le");
  //    assertEquals(new BooleanValue(false).getValue(),
  // c4.calculate(ex4).asBooleanValue().getValue());
  //
  //    Calculator c5 = new Calculator();
  //    MathExpression ex5 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("min")))
  //            .arg(new ConstantExpression(new DecimalValue("2")));
  //
  //    c5.setVariable("x", 15, 6, 2);
  //    c5.setVariable("s", 10, 20);
  //    c5.setVariable("l", "gt", "le");
  //    assertEquals(new BooleanValue(false).getValue(),
  // c5.calculate(ex5).asBooleanValue().getValue());
  //
  //    Calculator c6 = new Calculator();
  //    MathExpression ex6 =
  //        new FunctionExpression("in")
  //            .arg(new VariableExpression("x"))
  //            .arg(new VariableExpression("s"))
  //            .arg(new VariableExpression("l"))
  //            .arg(new ConstantExpression(new StringValue("min")))
  //            .arg(new ConstantExpression(new DecimalValue("2")));
  //
  //    c6.setVariable("x", 15, 11, 2);
  //    c6.setVariable("s", 10, 20);
  //    c6.setVariable("l", "gt", "le");
  //    assertEquals(new BooleanValue(true).getValue(),
  // c6.calculate(ex6).asBooleanValue().getValue());
  //  }
}
