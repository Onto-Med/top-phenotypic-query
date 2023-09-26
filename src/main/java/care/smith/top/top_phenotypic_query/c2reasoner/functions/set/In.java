package care.smith.top.top_phenotypic_query.c2reasoner.functions.set;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Aggregator;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;
import java.util.Map;

/**
 * The <b>In-function</b> return 'true' if the given value (1st argument) lies in a given range (2nd
 * argument), otherwise 'false'. The function is automatically generated for all phenotype
 * restrictions and does not have to be defined manually.
 *
 * <table class="striped"><caption>Arguments:</caption>
 * <tr>
 *   <th><b>Arguments</b></th>
 *   <th><b>Arguments data types</b></th>
 *   <th><b>Return data type</b></th>
 *   <th><b>Example</b></th>
 * </tr>
 * <tr>
 *   <td>&lt;value-exp&gt; &lt;restr-exp&gt;</td>
 *   <td>
 *     &lt;value-exp&gt;: number<br>
 *     &lt;restr-exp&gt;: number
 *   </td>
 *   <td>boolean</td>
 *   <td>
 *     <i>In</i>(Body_Temperature, Increased)<br>
 *     The function returns 'true' if the actual temperature value lies in the increased range.
 *   </td>
 * </tr>
 * </table>
 *
 * @author TOP group
 */
public class In extends FunctionEntity {

  private static final In INSTANCE = new In();

  private In() {
    super("in", NotationEnum.PREFIX, 2, 2);
  }

  public static In get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);
    args = c2r.calculate(args);
    if (args == null) return Exp.ofFalse();
    Exceptions.checkArgumentsHaveSameType(getFunction(), args);

    Expression phe = args.get(0);
    Expression res = args.get(1);

    if (res.getValues() != null) {
      Expression val = Aggregator.aggregate(phe, c2r);
      return Exp.of(Values.contains(res.getValues(), Expressions.getValue(val)));
    }

    Restriction r = res.getRestriction();
    List<Value> vals = phe.getValues();
    if (Restrictions.hasInterval(r))
      return calculateInInterval(
          vals, Restrictions.getInterval(r), r.getQuantifier(), r.getCardinality());
    return calculateInSet(vals, Restrictions.getValues(r), r.getQuantifier(), r.getCardinality());
  }

  private Expression calculateInInterval(
      List<Value> vals, Map<RestrictionOperator, Value> inter, Quantifier quan, Integer card) {
    int hits = 0;
    for (Value v : vals) if (Values.contains(inter, v)) hits++;
    return Exp.of(checkQuantifier(vals.size(), hits, quan, card));
  }

  private Expression calculateInSet(
      List<Value> vals, List<Value> set, Quantifier quan, Integer card) {
    int hits = 0;
    for (Value v : vals) if (Values.contains(set, v)) hits++;
    return Exp.of(checkQuantifier(vals.size(), hits, quan, card));
  }

  private boolean checkQuantifier(int size, int hits, Quantifier quan, Integer card) {
    if (quan == Quantifier.ALL && hits == size) return true;
    if (quan == Quantifier.EXACT && card != null && hits == card.intValue()) return true;
    if (quan == Quantifier.MIN && card != null && hits >= card.intValue()) return true;
    if (quan == Quantifier.MAX && card != null && hits <= card.intValue()) return true;
    return false;
  }
}
