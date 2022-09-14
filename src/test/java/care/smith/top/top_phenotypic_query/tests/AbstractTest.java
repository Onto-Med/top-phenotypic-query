package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.backend.model.*;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

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

  static Phenotype getRestriction(String name, Phenotype parent, Integer min, Integer max, EntityType entityType) {
    Expression values = new Expression().entityId(parent.getId());
    Expression range = new Expression().function("list");
    Expression limits = new Expression().function("list");

    NumberRestriction restriction = new NumberRestriction();

    if (min != null) {
      restriction
          .minOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
          .addValuesItem(new BigDecimal(min));
    }

    if (max != null) {
      restriction.maxOperator(RestrictionOperator.LESS_THAN).addValuesItem(new BigDecimal(max));
    }

    return (Phenotype)
        new Phenotype()
            .superPhenotype(parent)
            .restriction(restriction)
            .entityType(entityType)
            .id(name);
  }

  static Phenotype getSingleRestriction(String name, Phenotype parent, Integer min, Integer max) {
    return getRestriction(name, parent, min, max, EntityType.SINGLE_RESTRICTION);
  }

  static Phenotype getCompositeRestriction(String name, Phenotype parent, Integer min, Integer max) {
    return getRestriction(name, parent, min, max, EntityType.COMPOSITE_RESTRICTION);
  }
}
