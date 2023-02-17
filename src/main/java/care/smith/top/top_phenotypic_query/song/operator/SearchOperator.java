package care.smith.top.top_phenotypic_query.song.operator;

import java.util.List;
import java.util.stream.Collectors;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.song.SONG;
import care.smith.top.top_phenotypic_query.util.Values;

public abstract class SearchOperator {

  private final ExpressionFunction function;

  protected SearchOperator(String title, NotationEnum notation) {
    this.function =
        new ExpressionFunction().id(getClass().getSimpleName()).title(title).notation(notation);
  }

  protected SearchOperator(
      String title, NotationEnum notation, int minArgumentNumber, int maxArgumentNumber) {
    this(title, notation);
    minArgumentNumber(minArgumentNumber);
    maxArgumentNumber(maxArgumentNumber);
  }

  public ExpressionFunction getFunction() {
    return function;
  }

  public String getFunctionId() {
    return function.getId();
  }

  public String toString(List<String> args) {
    if (function.getNotation() == NotationEnum.PREFIX)
      return function.getTitle() + "(" + String.join(", ", args) + ")";
    else if (function.getNotation() == NotationEnum.POSTFIX)
      return "(" + String.join(", ", args) + ")" + function.getTitle();
    return "(" + String.join(" " + function.getTitle() + " ", args) + ")";
  }

  public String toStringValues(List<Value> args) {
    return toString(
        args.stream().map(Values::toStringWithoutDateTime).collect(Collectors.toList()));
  }

  protected void maxArgumentNumber(int num) {
    function.maxArgumentNumber(num);
  }

  protected void minArgumentNumber(int num) {
    function.minArgumentNumber(num);
  }

  public abstract Expression generate(List<Expression> args, SONG song);
}
