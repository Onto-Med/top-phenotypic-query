package care.smith.top.top_phenotypic_query.tests;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import care.smith.top.backend.model.Code;
import care.smith.top.backend.model.CodeSystem;
import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.Expression;
import care.smith.top.backend.model.ExpressionValue;
import care.smith.top.backend.model.ItemType;
import care.smith.top.backend.model.NumberRestriction;
import care.smith.top.backend.model.NumberValue;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Quantifier;
import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.backend.model.StringValue;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;

public abstract class AbstractTest {
  static DateTimeRestriction getDTR(int year) {
    return new DateTimeRestriction()
        .minOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
        .maxOperator(RestrictionOperator.LESS_THAN)
        .values(
            List.of(
                LocalDateTime.of(year, 1, 1, 0, 0, 0, 0),
                LocalDateTime.of(year + 1, 1, 1, 0, 0, 0, 0)));
  }

  static Phenotype getSinglePhenotype(String name, String codeSystem, String code)
      throws URISyntaxException {
    Phenotype phenotype =
        (Phenotype)
            new Phenotype().dataType(DataType.NUMBER).itemType(ItemType.OBSERVATION).id(name);
    if (codeSystem != null && code != null)
      phenotype.addCodesItem(
          new Code().code(code).codeSystem(new CodeSystem().uri(new URI(codeSystem))));
    return phenotype;
  }

  static Phenotype getCompositePhenotype(
      String name, Expression exp, String codeSystem, String code) throws URISyntaxException {
    Phenotype phenotype = (Phenotype) new Phenotype().expression(exp).id(name);
    if (codeSystem != null && code != null)
      phenotype.addCodesItem(
          new Code().code(code).codeSystem(new CodeSystem().uri(new URI(codeSystem))));
    return phenotype;
  }

  static Phenotype getRestriction(
      String name,
      Phenotype parent,
      Integer min,
      Integer max,
      EntityType entityType,
      Quantifier quantifier,
      Integer cardinality) {
    Expression values = new Expression().entityId(parent.getId());
    Expression range = new Expression().function("list");
    Expression limits = new Expression().function("list");

    NumberRestriction restriction = new NumberRestriction();

    if (min != null) {
      restriction
          .minOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
          .addValuesItem(new BigDecimal(min));

      range.addArgumentsItem(
          new Expression()
              .value(
                  new ExpressionValue().value(new NumberValue().value(BigDecimal.valueOf(min)))));
      limits.addArgumentsItem(
          new Expression().value(new ExpressionValue().value(new StringValue().value(">="))));
    }

    if (max != null) {
      restriction.maxOperator(RestrictionOperator.LESS_THAN).addValuesItem(new BigDecimal(max));

      range.addArgumentsItem(
          new Expression()
              .value(
                  new ExpressionValue().value(new NumberValue().value(BigDecimal.valueOf(max)))));
      limits.addArgumentsItem(
          new Expression().value(new ExpressionValue().value(new StringValue().value("<"))));
    }

    Expression exp =
        new Expression()
            .function("in")
            .addArgumentsItem(values)
            .addArgumentsItem(range)
            .addArgumentsItem(limits);

    if (quantifier != null) {
      restriction.quantifier(quantifier);
      exp.addArgumentsItem(ExpressionUtil.stringToExpression(quantifier.getValue()));
      if (cardinality != null) {
        restriction.cardinality(cardinality);
        exp.addArgumentsItem(ExpressionUtil.numberToExpression(cardinality));
      }
    }

    return (Phenotype)
        new Phenotype()
            .superPhenotype(parent)
            .restriction(restriction)
            .expression(exp)
            .entityType(entityType)
            .id(name);
  }

  static Phenotype getSingleRestriction(String name, Phenotype parent, Integer min, Integer max) {
    return getRestriction(name, parent, min, max, EntityType.SINGLE_RESTRICTION, null, null);
  }

  static Phenotype getSingleRestriction(
      String name,
      Phenotype parent,
      Integer min,
      Integer max,
      Quantifier quantifier,
      Integer cardinality) {
    return getRestriction(
        name, parent, min, max, EntityType.SINGLE_RESTRICTION, quantifier, cardinality);
  }

  static Phenotype getCompositeRestriction(
      String name, Phenotype parent, Integer min, Integer max) {
    return getRestriction(name, parent, min, max, EntityType.COMPOSITE_RESTRICTION, null, null);
  }
}
