package care.smith.top.top_phenotypic_query.c2reasoner.functions;

import java.util.List;
import java.util.stream.Collectors;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.util.Values;

public abstract class FunctionEntity {

  private ExpressionFunction function;

  protected FunctionEntity(String title, NotationEnum notation) {
    this.function =
        new ExpressionFunction().id(getClass().getSimpleName()).title(title).notation(notation);
  }

  protected FunctionEntity(
      String title, NotationEnum notation, int minArgumentNumber, int maxArgumentNumber) {
    this(title, notation);
    minArgumentNumber(minArgumentNumber);
    maxArgumentNumber(maxArgumentNumber);
  }

  protected void minArgumentNumber(int num) {
    function.minArgumentNumber(num);
  }

  protected void maxArgumentNumber(int num) {
    function.maxArgumentNumber(num);
  }

  public String getFunctionId() {
    return function.getId();
  }

  public ExpressionFunction getFunction() {
    return function;
  }

  public abstract Expression calculate(List<Expression> args, C2R c2r);

  public String toString(List<String> args) {
    if (function.getNotation() == NotationEnum.PREFIX)
      return function.getTitle() + "(" + String.join(", ", args) + ")";
    else if (function.getNotation() == NotationEnum.POSTFIX)
      return "(" + String.join(", ", args) + ")" + function.getTitle();
    return "(" + String.join(" " + function.getTitle() + " ", args) + ")";
  }

  public String toStringValues(List<Value> args) {
    return toString(
        args.stream().map(v -> Values.toStringWithoutDateTime(v)).collect(Collectors.toList()));
  }

  //  public String toStringDates(List<Value> args) {
  //    return toString(
  //        args.stream().map(v -> Values.toStringDateTime(v)).collect(Collectors.toList()));
  //  }
}
