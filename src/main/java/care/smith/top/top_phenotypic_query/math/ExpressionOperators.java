package care.smith.top.top_phenotypic_query.math;

import java.util.Arrays;
import java.util.List;

import care.smith.top.backend.model.ExpressionMultaryOperator;
import care.smith.top.backend.model.ExpressionOperator;
import care.smith.top.simple_onto_api.c2reasoner.math.Operator;
import care.smith.top.simple_onto_api.c2reasoner.math.Operator.Type;

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
    return Arrays.asList(
        new ExpressionOperator()
            .id("addition")
            .title("+")
            .type(ExpressionOperator.TypeEnum.BINARY)
            .representation(ExpressionOperator.RepresentationEnum.INFIX),
        new ExpressionOperator()
            .id("subtraction")
            .title("-")
            .type(ExpressionOperator.TypeEnum.BINARY)
            .representation(ExpressionOperator.RepresentationEnum.INFIX),
        new ExpressionOperator()
            .id("multiplication")
            .title("*")
            .type(ExpressionOperator.TypeEnum.BINARY)
            .representation(ExpressionOperator.RepresentationEnum.INFIX),
        new ExpressionOperator()
            .id("division")
            .title("/")
            .type(ExpressionOperator.TypeEnum.BINARY)
            .representation(ExpressionOperator.RepresentationEnum.INFIX),
        new ExpressionOperator()
            .id("exponentiation")
            .title("^")
            .type(ExpressionOperator.TypeEnum.BINARY)
            .representation(ExpressionOperator.RepresentationEnum.INFIX),
        new ExpressionMultaryOperator()
            .required(2)
            .id("minimum")
            .title("min")
            .type(ExpressionOperator.TypeEnum.MULTARY)
            .representation(ExpressionOperator.RepresentationEnum.PREFIX),
        new ExpressionMultaryOperator()
            .required(2)
            .id("maximum")
            .title("max")
            .type(ExpressionOperator.TypeEnum.MULTARY)
            .representation(ExpressionOperator.RepresentationEnum.PREFIX),
        new ExpressionOperator()
            .id("entity")
            .title("entity")
            .type(ExpressionOperator.TypeEnum.UNARY)
            .representation(ExpressionOperator.RepresentationEnum.PREFIX),
        new ExpressionOperator()
            .id("constant")
            .title("constant")
            .type(ExpressionOperator.TypeEnum.UNARY)
            .representation(ExpressionOperator.RepresentationEnum.PREFIX));
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
}
