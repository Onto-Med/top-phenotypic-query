package care.smith.top.simple_onto_api.calculator.functions.arithmetic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Power extends Function {

  private static Power instance = null;

  private Power() {
    super("power", "^", Notation.INFIX);
    minArgumentsNumber(2);
    maxArgumentsNumber(2);
  }

  public static Power get() {
    if (instance == null) instance = new Power();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DECIMAL, values);

    BigDecimal v1 = Aggregator.aggregate(values.get(0), defaultAggregateFunction).getValueDecimal();
    BigDecimal v2 = values.get(1).getValueDecimal();

    int signOf2 = v2.signum();
    double dn1 = v1.doubleValue();
    v2 = v2.multiply(new BigDecimal(signOf2)); // n2 is now positive
    BigDecimal remainderOf2 = v2.remainder(BigDecimal.ONE);
    BigDecimal n2IntPart = v2.subtract(remainderOf2);
    BigDecimal intPow = v1.pow(n2IntPart.intValueExact(), mc);
    BigDecimal doublePow = BigDecimal.valueOf(Math.pow(dn1, remainderOf2.doubleValue()));

    BigDecimal result = intPow.multiply(doublePow, mc);
    if (signOf2 == -1)
      result = BigDecimal.ONE.divide(result, mc.getPrecision(), RoundingMode.HALF_UP);

    return new DecimalValue(result);
  }
}
