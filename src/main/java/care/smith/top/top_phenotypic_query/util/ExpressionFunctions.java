package care.smith.top.top_phenotypic_query.util;

import java.util.List;
import java.util.stream.Collectors;

import care.smith.top.backend.model.ExpressionFunction;
import care.smith.top.backend.model.ExpressionFunction.NotationEnum;
import care.smith.top.simple_onto_api.calculator.Calculator;
import care.smith.top.simple_onto_api.calculator.functions.Function;

public class ExpressionFunctions {

  public static List<ExpressionFunction> getFunctions() {
    return new Calculator()
        .getFunctions().stream().map(f -> convert(f)).collect(Collectors.toList());
  }

  public static List<care.smith.top.backend.model.Constant> getConstants() {
    return new Calculator()
        .getConstants().stream().map(c -> convert(c)).collect(Collectors.toList());
  }

  private static ExpressionFunction convert(Function f) {
    ExpressionFunction ef =
        new ExpressionFunction()
            .id(f.getId())
            .title(f.getTitle())
            .maxArgumentNumber(f.getMaxArgumentsNumber())
            .minArgumentNumber(f.getMinArgumentsNumber())
            .notation(NotationEnum.valueOf(f.getNotation().name()));
    return ef;
  }

  private static care.smith.top.backend.model.Constant convert(
      care.smith.top.simple_onto_api.calculator.constants.Constant c) {
    return new care.smith.top.backend.model.Constant().id(c.getId()).title(c.getTitle());
  }
}
