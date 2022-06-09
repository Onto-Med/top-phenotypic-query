package care.smith.top.top_phenotypic_query.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import care.smith.top.backend.model.ExpressionMultaryOperator;
import care.smith.top.backend.model.ExpressionOperator;
import care.smith.top.simple_onto_api.c2reasoner.math.Operator;
import care.smith.top.simple_onto_api.c2reasoner.math.Operator.Type;
import care.smith.top.simple_onto_api.c2reasoner.math.evalex.EvalEx;

public class ExpressionOperators {

  public static List<ExpressionOperator> getCombinedPhenotypeOperators() {
    return Arrays.asList(
        new ExpressionMultaryOperator()
            .required(2)
            .id("intersection")
            .title("and")
            .type(ExpressionOperator.TypeEnum.MULTARY)
            .representation(ExpressionOperator.RepresentationEnum.PREFIX),
        new ExpressionMultaryOperator()
            .required(2)
            .id("union")
            .title("or")
            .type(ExpressionOperator.TypeEnum.MULTARY)
            .representation(ExpressionOperator.RepresentationEnum.PREFIX),
        new ExpressionOperator()
            .id("complement")
            .title("not")
            .type(ExpressionOperator.TypeEnum.UNARY)
            .representation(ExpressionOperator.RepresentationEnum.PREFIX),
        new ExpressionOperator()
            .id("entity")
            .title("entity")
            .type(ExpressionOperator.TypeEnum.UNARY)
            .representation(ExpressionOperator.RepresentationEnum.PREFIX));
  }

  public static List<ExpressionOperator> getDerivedPhenotypeOperators() {
    List<ExpressionOperator> ops = new ArrayList<>();
    for (Operator op : new EvalEx().getOperators()) ops.add(convert(op));
    return ops;
  }

  public static List<care.smith.top.backend.model.Constant> getConstants() {
    List<care.smith.top.backend.model.Constant> cons = new ArrayList<>();
    for (care.smith.top.simple_onto_api.c2reasoner.math.Constant co : new EvalEx().getConstants())
      cons.add(convert(co));
    return cons;
  }

  private static ExpressionOperator convert(Operator o) {
    ExpressionOperator eo = null;
    if (o.getType() == Type.MULTARY) eo = new ExpressionMultaryOperator().required(o.getRequired());
    else eo = new ExpressionOperator();
    eo.setId(o.getId());
    eo.setTitle(o.getTitle());
    eo.setRepresentation(
        ExpressionOperator.RepresentationEnum.valueOf(o.getRepresentation().name()));
    eo.setType(ExpressionOperator.TypeEnum.valueOf(o.getType().name()));
    return eo;
  }

  private static care.smith.top.backend.model.Constant convert(
      care.smith.top.simple_onto_api.c2reasoner.math.Constant c) {
    return new care.smith.top.backend.model.Constant().id(c.getId()).title(c.getTitle());
  }
}
