package care.smith.top.simple_onto_api.calculator.functions.arithmetic;

import java.util.List;

import care.smith.top.simple_onto_api.calculator.Exceptions;
import care.smith.top.simple_onto_api.calculator.functions.Function;
import care.smith.top.simple_onto_api.calculator.functions.aggregate.Aggregator;
import care.smith.top.simple_onto_api.model.enums.Datatype;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;

public class Add extends Function {

  private static Add instance = null;

  private Add() {
    super("add", "+", Notation.INFIX);
    minArgumentsNumber(2);
    maxArgumentsNumber(2);
  }

  public static Add get() {
    if (instance == null) instance = new Add();
    return instance;
  }

  @Override
  public Value calculate(List<Value> values, Function defaultAggregateFunction) {
    Exceptions.checkArgumentsNumber(this, values);
    Exceptions.checkArgumentsType(this, Datatype.DECIMAL, values);
    values = Aggregator.aggregate(values, defaultAggregateFunction);
    return new DecimalValue(values.get(0).getValueDecimal().add(values.get(1).getValueDecimal()));
  }
}
