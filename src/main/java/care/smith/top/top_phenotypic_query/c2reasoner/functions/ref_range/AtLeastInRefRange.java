package care.smith.top.top_phenotypic_query.c2reasoner.functions.ref_range;

import module java.base;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.ucum.UnitConverter;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

/**
 * The function <b>AtLeastInRefRange</b> checks whether phenotype values (1st argument) fall within
 * the reference range. The 2nd argument (optional, default: 1) specifies the minimum number of
 * values that must fall within the reference range.
 *
 * <table class="striped"><caption>Return values:</caption>
 * <tr>
 *   <th><b>Return value</b></th>
 *   <th><b>Description</b></th>
 * </tr>
 * <tr>
 *   <td><b>1</b></td>
 *   <td>The defined minimum number of values falls within the reference range.</td>
 * </tr>
 * <tr>
 *   <td><b>0</b></td>
 *   <td>The defined minimum number of values does not fall within the reference range.</td>
 * </tr>
 * <tr>
 *   <td><b>-1</b></td>
 *   <td>No values available.</td>
 * </tr>
 * <tr>
 *   <td><b>-2</b></td>
 *   <td>No reference range available.</td>
 * </tr>
 * <tr>
 *   <td><b>-3</b></td>
 *   <td>Conversion of measurement units not possible.</td>
 * </tr>
 * </table>
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>
 *     &lt;phe&gt;<br>
 *     &lt;num&gt;
 *   </td>
 *   <td>
 *     phenotype class<br>
 *     number
 *   </td>
 *   <td>number</td>
 *   <td>
 *     <i>AtLeastInRefRange</i>(Haemoglobin, 3)<br>
 *     The function returns '1' if at least three Haemoglobin values fall within the reference range.
 *   </td>
 * </tr>
 * <tr>
 *   <td>
 *     &lt;phe&gt;
 *   </td>
 *   <td>
 *     phenotype class
 *   </td>
 *   <td>number</td>
 *   <td>
 *     <i>AtLeastInRefRange</i>(Haemoglobin)<br>
 *     The function returns '1' if at least one Haemoglobin value falls within the reference range.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class AtLeastInRefRange extends FunctionEntity {

  private static final AtLeastInRefRange INSTANCE = new AtLeastInRefRange();

  private AtLeastInRefRange() {
    super("atLeastInRefRange", NotationEnum.PREFIX, 1, 2);
  }

  public static AtLeastInRefRange get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype phe, Number numVals) {
    return of(Exp.of(phe), Exp.of(numVals));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculateCheckValues(args);
    if (args == null) return Exp.of(-1);
    Exceptions.checkArgumentsType(getFunction(), DataType.NUMBER, args);

    List<Value> vals = args.get(0).getValues();
    int num = 1;
    if (args.size() > 1) num = Expressions.getNumberValue(args.get(1)).intValue();

    int hits = 0;
    boolean valueExists = false;
    boolean referenceExists = true;
    boolean conversionPossible = true;

    for (Value v : vals) {
      BigDecimal origVal = Values.getFieldNumberValue(v, "origVal");
      if (origVal == null) continue;
      valueExists = true;

      BigDecimal refLowVal = Values.getFieldNumberValue(v, "refLowVal");
      BigDecimal refHighVal = Values.getFieldNumberValue(v, "refHighVal");
      if (refLowVal == null && refHighVal == null) {
        referenceExists = false;
        continue;
      }

      String origUnit = Values.getFieldStringValue(v, "origUnit");
      String refLowUnit = Values.getFieldStringValue(v, "refLowUnit");
      String refHighUnit = Values.getFieldStringValue(v, "refHighUnit");

      Result res =
          new RefRange(origVal, origUnit, refLowVal, refLowUnit, refHighVal, refHighUnit).inRange();

      if (res == Result.IN_RANGE) {
        hits++;
        if (hits >= num) return Exp.of(1);
      } else if (res == Result.CONVERSION_NOT_POSSIBLE) conversionPossible = false;
    }

    if (!valueExists) return Exp.of(-1);
    if (!referenceExists) return Exp.of(-2);
    if (!conversionPossible) return Exp.of(-3);

    return Exp.of(0);
  }

  private record RefRange(
      BigDecimal origVal,
      String origUnit,
      BigDecimal refLowVal,
      String refLowUnit,
      BigDecimal refHighVal,
      String refHighUnit) {

    private String getLowUnit() {
      return (refLowUnit != null) ? refLowUnit : refHighUnit;
    }

    private String getHighUnit() {
      return (refHighUnit != null) ? refHighUnit : refLowUnit;
    }

    private boolean conversionRequired() {
      return origUnit != null
          && ((refLowUnit != null && !UnitConverter.equals(origUnit, refLowUnit))
              || (refHighUnit != null && !UnitConverter.equals(origUnit, refHighUnit)));
    }

    private Result inRange() {
      if (!conversionRequired()) {
        if (refLowVal != null && origVal.compareTo(refLowVal) < 0) return Result.NOT_IN_RANGE;
        if (refHighVal != null && origVal.compareTo(refHighVal) > 0) return Result.NOT_IN_RANGE;
        return Result.IN_RANGE;
      }

      if (refLowVal != null) {
        Optional<BigDecimal> conv =
            UnitConverter.get().convertDirect(origVal, origUnit, getLowUnit());
        if (conv.isEmpty()) return Result.CONVERSION_NOT_POSSIBLE;
        if (origVal.compareTo(conv.get()) < 0) return Result.NOT_IN_RANGE;
      }

      if (refHighVal != null) {
        Optional<BigDecimal> conv =
            UnitConverter.get().convertDirect(origVal, origUnit, getHighUnit());
        if (conv.isEmpty()) return Result.CONVERSION_NOT_POSSIBLE;
        if (origVal.compareTo(conv.get()) < 0) return Result.NOT_IN_RANGE;
      }

      return Result.IN_RANGE;
    }
  }

  private enum Result {
    IN_RANGE,
    NOT_IN_RANGE,
    CONVERSION_NOT_POSSIBLE
  }
}
