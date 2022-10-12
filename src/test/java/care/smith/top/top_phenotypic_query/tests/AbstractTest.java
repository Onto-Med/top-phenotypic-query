package care.smith.top.top_phenotypic_query.tests;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import care.smith.top.model.Code;
import care.smith.top.model.CodeSystem;
import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.EntityType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.ItemType;
import care.smith.top.model.NumberRestriction;
import care.smith.top.model.NumberValue;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.StringRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Phenotypes;

public abstract class AbstractTest {

  private static final DataType DEFAULT_DATA_TYPE = DataType.NUMBER;
  private static final Quantifier DEFAULT_QUANTIFIER = Quantifier.MIN;
  private static final Integer DEFAULT_CARDINALITY = Integer.valueOf(1);
  protected static final RestrictionOperator DEFAULT_MIN_OPERATOR =
      RestrictionOperator.GREATER_THAN_OR_EQUAL_TO;
  protected static final RestrictionOperator DEFAULT_MAX_OPERATOR = RestrictionOperator.LESS_THAN;

  protected static Phenotype age = getPhenotype("Age", "http://loinc.org", "30525-0");
  protected static Phenotype young = getInterval("Young", age, 18, 34);
  protected static Phenotype old = getIntervalMin("Old", age, 34);
  protected static Phenotype sex =
      getPhenotype("Sex", "http://loinc.org", "46098-0", DataType.STRING);
  protected static Phenotype female =
      getRestriction("Female", sex, "http://hl7.org/fhir/administrative-gender|female");
  protected static Phenotype weight = getPhenotype("Weight", "http://loinc.org", "3141-9");
  protected static Phenotype height = getPhenotype("Height", "http://loinc.org", "3137-7", "m");
  protected static Phenotype bmi = getPhenotype("BMI", getBMIExpression());
  protected static Phenotype bmi19_25 = getInterval("BMI19_25", bmi, 19, 25);
  protected static Phenotype bmi19_27 = getInterval("BMI19_27", bmi, 19, 27);
  protected static Phenotype bmi25_30 = getInterval("BMI25_30", bmi, 25, 30);
  protected static Phenotype bmi27_30 = getInterval("BMI27_30", bmi, 27, 30);
  protected static Phenotype finding = getPhenotype("Finding", getFindingExpression());
  protected static Phenotype overWeight = getRestriction("Overweight", finding, 1);

  protected static Map<String, Phenotype> phenotypes =
      getPhenotypeMap(
          age,
          young,
          old,
          sex,
          female,
          weight,
          height,
          bmi,
          bmi19_25,
          bmi19_27,
          bmi25_30,
          bmi27_30,
          finding,
          overWeight);

  protected static ExpressionFunction defAgrFunc =
      new ExpressionFunction().id("last").minArgumentNumber(1).notation(NotationEnum.PREFIX);

  protected static DateTimeRestriction getDTR(int year) {
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

  static Phenotype getPhenotype(String name, String codeSystem, String code, String unit) {
    return getPhenotype(name, codeSystem, code, DEFAULT_DATA_TYPE, unit);
  }

  static Phenotype getPhenotype(String name, String codeSystem, String code, DataType dataType) {
    return getPhenotype(name, codeSystem, code, dataType, null);
  }

  static Phenotype getPhenotype(
      String name, String codeSystem, String code, DataType dataType, String unit) {
    Phenotype phenotype =
        (Phenotype)
            new Phenotype()
                .dataType(dataType)
                .itemType(ItemType.OBSERVATION)
                .id(name)
                .entityType(EntityType.SINGLE_PHENOTYPE);
    if (unit != null) phenotype.setUnit(unit);
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
    //    Expression values = new Expression().entityId(parent.getId());
    //    Expression range = new Expression().functionId("list");
    //    Expression limits = new Expression().functionId("list");

    NumberRestriction restriction = getNumberRestriction();

    if (min != null) {
      restriction.minOperator(minOperator).addValuesItem(new BigDecimal(min.toString()));
      //      addArgument(range, new BigDecimal(min.toString()));
      //      addArgument(limits, minOperator.getValue());
    }

    if (max != null) {
      restriction.maxOperator(maxOperator).addValuesItem(new BigDecimal(max.toString()));
      //      addArgument(range, new BigDecimal(max.toString()));
      //      addArgument(limits, maxOperator.getValue());
    }

    //    Expression exp = getInExpression(values, range, limits);
    addQuantifier(restriction, null, quantifier, cardinality);

    return getRestriction(name, parent, restriction, null);
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
    //    Expression values = new Expression().entityId(parent.getId());
    //    Expression range = new Expression().functionId("list");
    //    Expression limits = new Expression().functionId("list");

    StringRestriction restriction = getStringRestriction();

    for (String value : rangeValues) {
      restriction.addValuesItem(value);
      //      addArgument(range, value);
    }

    //    Expression exp = getInExpression(values, range, limits);
    addQuantifier(restriction, null, quantifier, cardinality);

    return getRestriction(name, parent, restriction, null);
  }

  static Phenotype getRestriction(
      String name,
      Phenotype parent,
      Quantifier quantifier,
      Integer cardinality,
      Number... rangeValues) {
    //    Expression values = new Expression().entityId(parent.getId());
    //    Expression range = new Expression().functionId("list");
    //    Expression limits = new Expression().functionId("list");

    NumberRestriction restriction = getNumberRestriction();

    for (Number value : rangeValues) {
      restriction.addValuesItem(new BigDecimal(value.toString()));
      //      addArgument(range, new BigDecimal(value.toString()));
    }

    //    Expression exp = getInExpression(values, range, limits);
    addQuantifier(restriction, null, quantifier, cardinality);

    return getRestriction(name, parent, restriction, null);
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
      //      exp.addArgumentsItem(ExpressionUtil.stringToExpression(quant.getValue()));
      if (card != null) {
        restr.cardinality(card);
        //        exp.addArgumentsItem(ExpressionUtil.numberToExpression(card));
      }
    }
  }

  //  private static void addArgument(Expression exp, BigDecimal val) {
  //    exp.addArgumentsItem(new Expression().value(new NumberValue().value(val)));
  //  }
  //
  //  private static void addArgument(Expression exp, String val) {
  //    exp.addArgumentsItem(new Expression().value(new StringValue().value(val)));
  //  }

  private static EntityType getRestrictionType(Phenotype parent) {
    return Phenotypes.isSinglePhenotype(parent)
        ? EntityType.SINGLE_RESTRICTION
        : EntityType.COMPOSITE_RESTRICTION;
  }

  //  private static Expression getInExpression(
  //      Expression values, Expression range, Expression limits) {
  //    return new Expression()
  //        .functionId("in")
  //        .addArgumentsItem(values)
  //        .addArgumentsItem(range)
  //        .addArgumentsItem(limits);
  //  }

  private static Phenotype getRestriction(
      String name, Phenotype parent, Restriction restr, Expression exp) {
    return (Phenotype)
        new Phenotype()
            .superPhenotype(parent)
            .restriction(restr)
            //            .expression(exp)
            .entityType(getRestrictionType(parent))
            .id(name);
  }

  private static NumberRestriction getNumberRestriction() {
    return (NumberRestriction) new NumberRestriction().type(DataType.NUMBER);
  }

  private static StringRestriction getStringRestriction() {
    return (StringRestriction) new StringRestriction().type(DataType.STRING);
  }

  protected static Map<String, Phenotype> getPhenotypeMap(Phenotype... phenotypes) {
    return Stream.of(phenotypes).collect(Collectors.toMap(Phenotype::getId, Function.identity()));
  }

  static Expression getBMIExpression() {
    return new Expression()
        .functionId("divide")
        .addArgumentsItem(new Expression().entityId("Weight"))
        .addArgumentsItem(
            new Expression()
                .functionId("power")
                .addArgumentsItem(new Expression().entityId("Height"))
                .addArgumentsItem(getValue(2)));
  }

  static Expression getFindingExpression() {
    Expression youngAndBmi19_25 =
        new Expression()
            .functionId("and")
            .addArgumentsItem(new Expression().entityId("Young"))
            .addArgumentsItem(new Expression().entityId("BMI19_25"));
    Expression oldAndBmi19_27 =
        new Expression()
            .functionId("and")
            .addArgumentsItem(new Expression().entityId("Old"))
            .addArgumentsItem(new Expression().entityId("BMI19_27"));
    Expression normalWeight =
        new Expression()
            .functionId("or")
            .addArgumentsItem(youngAndBmi19_25)
            .addArgumentsItem(oldAndBmi19_27);

    Expression youngAndBmi25_30 =
        new Expression()
            .functionId("and")
            .addArgumentsItem(new Expression().entityId("Young"))
            .addArgumentsItem(new Expression().entityId("BMI25_30"));
    Expression oldAndBmi27_30 =
        new Expression()
            .functionId("and")
            .addArgumentsItem(new Expression().entityId("Old"))
            .addArgumentsItem(new Expression().entityId("BMI27_30"));
    Expression overweight =
        new Expression()
            .functionId("or")
            .addArgumentsItem(youngAndBmi25_30)
            .addArgumentsItem(oldAndBmi27_30);

    return new Expression()
        .functionId("switch")
        .addArgumentsItem(normalWeight)
        .addArgumentsItem(getValue(0))
        .addArgumentsItem(overweight)
        .addArgumentsItem(getValue(1))
        .addArgumentsItem(getValue(-1));
  }

  protected static Value getValue(String pheName, Phenotypes phes) {
    return phes.getValues(pheName, getDTR(2000)).getValues().get(0);
  }

  static Expression getValue(int value) {
    NumberValue v = new NumberValue().value(BigDecimal.valueOf(value));
    v.setDataType(DataType.NUMBER);
    return new Expression().value(v);
  }
}
