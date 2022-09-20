package care.smith.top.top_phenotypic_query.tests;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import care.smith.top.backend.model.Restriction;
import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.backend.model.StringRestriction;
import care.smith.top.backend.model.StringValue;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;
import care.smith.top.top_phenotypic_query.util.PhenotypeUtil;

public abstract class AbstractTest {

  private static final DataType DEFAULT_DATA_TYPE = DataType.NUMBER;
  private static final Quantifier DEFAULT_QUANTIFIER = Quantifier.MIN;
  private static final Integer DEFAULT_CARDINALITY = Integer.valueOf(1);
  private static final RestrictionOperator DEFAULT_MIN_OPERATOR =
      RestrictionOperator.GREATER_THAN_OR_EQUAL_TO;
  private static final RestrictionOperator DEFAULT_MAX_OPERATOR = RestrictionOperator.LESS_THAN;

  static DateTimeRestriction getDTR(int year) {
    return new DateTimeRestriction()
        .minOperator(DEFAULT_MIN_OPERATOR)
        .maxOperator(DEFAULT_MAX_OPERATOR)
        .values(
            List.of(
                LocalDateTime.of(year, 1, 1, 0, 0, 0, 0),
                LocalDateTime.of(year + 1, 1, 1, 0, 0, 0, 0)));
  }

  static Phenotype getPhenotype(String name) {
    return getPhenotype(name, null, null, DEFAULT_DATA_TYPE);
  }

  static Phenotype getPhenotype(String name, String codeSystem, String code) {
    return getPhenotype(name, codeSystem, code, DEFAULT_DATA_TYPE);
  }

  static Phenotype getPhenotype(String name, String codeSystem, String code, DataType dataType) {
    Phenotype phenotype =
        (Phenotype)
            new Phenotype()
                .dataType(dataType)
                .itemType(ItemType.OBSERVATION)
                .id(name)
                .entityType(EntityType.SINGLE_PHENOTYPE);
    addCode(phenotype, codeSystem, code);
    return phenotype;
  }

  static Phenotype getPhenotype(String name, Expression exp) {
    return getPhenotype(name, exp, null, null);
  }

  static Phenotype getPhenotype(String name, Expression exp, String codeSystem, String code) {
    Phenotype phenotype =
        (Phenotype)
            new Phenotype().expression(exp).id(name).entityType(EntityType.COMPOSITE_PHENOTYPE);
    addCode(phenotype, codeSystem, code);
    return phenotype;
  }

  static Phenotype getRestriction(
      String name,
      Phenotype parent,
      Number min,
      Number max,
      Quantifier quantifier,
      Integer cardinality) {
    return getRestriction(
        name,
        parent,
        DEFAULT_MIN_OPERATOR,
        min,
        DEFAULT_MAX_OPERATOR,
        max,
        quantifier,
        cardinality);
  }

  static Phenotype getInterval(String name, Phenotype parent, Number min, Number max) {
    return getRestriction(
        name,
        parent,
        DEFAULT_MIN_OPERATOR,
        min,
        DEFAULT_MAX_OPERATOR,
        max,
        DEFAULT_QUANTIFIER,
        DEFAULT_CARDINALITY);
  }

  static Phenotype getIntervalMax(String name, Phenotype parent, Number max) {
    return getRestriction(
        name,
        parent,
        null,
        null,
        DEFAULT_MAX_OPERATOR,
        max,
        DEFAULT_QUANTIFIER,
        DEFAULT_CARDINALITY);
  }

  static Phenotype getIntervalMin(String name, Phenotype parent, Number min) {
    return getRestriction(
        name,
        parent,
        DEFAULT_MIN_OPERATOR,
        min,
        null,
        null,
        DEFAULT_QUANTIFIER,
        DEFAULT_CARDINALITY);
  }

  static Phenotype getRestriction(
      String name,
      Phenotype parent,
      RestrictionOperator minOperator,
      Number min,
      RestrictionOperator maxOperator,
      Number max,
      Quantifier quantifier,
      Integer cardinality) {
    Expression values = new Expression().entityId(parent.getId());
    Expression range = new Expression().function("list");
    Expression limits = new Expression().function("list");

    NumberRestriction restriction = getNumberRestriction();

    if (min != null) {
      restriction.minOperator(minOperator).addValuesItem(new BigDecimal(min.toString()));
      addArgument(range, new BigDecimal(min.toString()));
      addArgument(limits, minOperator.getValue());
    }

    if (max != null) {
      restriction.maxOperator(maxOperator).addValuesItem(new BigDecimal(max.toString()));
      addArgument(range, new BigDecimal(max.toString()));
      addArgument(limits, maxOperator.getValue());
    }

    Expression exp = getInExpression(values, range, limits);
    addQuantifier(restriction, exp, quantifier, cardinality);

    return getRestriction(name, parent, restriction, exp);
  }

  static Phenotype getRestriction(String name, Phenotype parent, String... rangeValues) {
    return getRestriction(name, parent, DEFAULT_QUANTIFIER, DEFAULT_CARDINALITY, rangeValues);
  }

  static Phenotype getRestriction(String name, Phenotype parent, Number... rangeValues) {
    return getRestriction(name, parent, DEFAULT_QUANTIFIER, DEFAULT_CARDINALITY, rangeValues);
  }

  static Phenotype getRestriction(
      String name,
      Phenotype parent,
      Quantifier quantifier,
      Integer cardinality,
      String... rangeValues) {
    Expression values = new Expression().entityId(parent.getId());
    Expression range = new Expression().function("list");
    Expression limits = new Expression().function("list");

    StringRestriction restriction = getStringRestriction();

    for (String value : rangeValues) {
      restriction.addValuesItem(value);
      addArgument(range, value);
    }

    Expression exp = getInExpression(values, range, limits);
    addQuantifier(restriction, exp, quantifier, cardinality);

    return getRestriction(name, parent, restriction, exp);
  }

  static Phenotype getRestriction(
      String name,
      Phenotype parent,
      Quantifier quantifier,
      Integer cardinality,
      Number... rangeValues) {
    Expression values = new Expression().entityId(parent.getId());
    Expression range = new Expression().function("list");
    Expression limits = new Expression().function("list");

    NumberRestriction restriction = getNumberRestriction();

    for (Number value : rangeValues) {
      restriction.addValuesItem(new BigDecimal(value.toString()));
      addArgument(range, new BigDecimal(value.toString()));
    }

    Expression exp = getInExpression(values, range, limits);
    addQuantifier(restriction, exp, quantifier, cardinality);

    return getRestriction(name, parent, restriction, exp);
  }

  private static void addCode(Phenotype phenotype, String codeSystem, String code) {
    if (codeSystem != null && code != null)
      phenotype.addCodesItem(
          new Code().code(code).codeSystem(new CodeSystem().uri(URI.create(codeSystem))));
  }

  private static void addQuantifier(
      Restriction restr, Expression exp, Quantifier quant, Integer card) {
    if (quant != null) {
      restr.quantifier(quant);
      exp.addArgumentsItem(ExpressionUtil.stringToExpression(quant.getValue()));
      if (card != null) {
        restr.cardinality(card);
        exp.addArgumentsItem(ExpressionUtil.numberToExpression(card));
      }
    }
  }

  private static void addArgument(Expression exp, BigDecimal val) {
    exp.addArgumentsItem(
        new Expression().value(new ExpressionValue().value(new NumberValue().value(val))));
  }

  private static void addArgument(Expression exp, String val) {
    exp.addArgumentsItem(
        new Expression().value(new ExpressionValue().value(new StringValue().value(val))));
  }

  private static EntityType getRestrictionType(Phenotype parent) {
    return PhenotypeUtil.isSinglePhenotype(parent)
        ? EntityType.SINGLE_RESTRICTION
        : EntityType.COMPOSITE_RESTRICTION;
  }

  private static Expression getInExpression(
      Expression values, Expression range, Expression limits) {
    return new Expression()
        .function("in")
        .addArgumentsItem(values)
        .addArgumentsItem(range)
        .addArgumentsItem(limits);
  }

  private static Phenotype getRestriction(
      String name, Phenotype parent, Restriction restr, Expression exp) {
    return (Phenotype)
        new Phenotype()
            .superPhenotype(parent)
            .restriction(restr)
            .expression(exp)
            .entityType(getRestrictionType(parent))
            .id(name);
  }

  private static NumberRestriction getNumberRestriction() {
    return (NumberRestriction) new NumberRestriction().type(DataType.NUMBER);
  }

  private static StringRestriction getStringRestriction() {
    return (StringRestriction) new StringRestriction().type(DataType.STRING);
  }

  static Map<String, Phenotype> getPhenotypeMap(Phenotype... phenotypes) {
    return Stream.of(phenotypes).collect(Collectors.toMap(Phenotype::getId, Function.identity()));
  }
}
