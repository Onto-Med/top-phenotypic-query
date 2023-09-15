package care.smith.top.top_phenotypic_query.c2reasoner.functions.encounter;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import java.util.List;

public class EncType extends FunctionEntity {

  private static EncType INSTANCE = new EncType();

  private EncType() {
    super("encType", NotationEnum.PREFIX);
    minArgumentNumber(2);
  }

  public static EncType get() {
    return INSTANCE;
  }

  public static Expression of(List<Expression> args) {
    return Exp.function(get().getClass().getSimpleName(), args);
  }

  public static Expression of(Expression... args) {
    return of(List.of(args));
  }

  public static Expression of(Phenotype... args) {
    return of(Exp.toList(args));
  }

  @Override
  public Expression calculate(List<Expression> args, C2R c2r) {
    Exceptions.checkArgumentsNumber(getFunction(), args);

    Expression phe = c2r.calculate(args.get(0));
    if (!Expressions.hasValues(phe)) return Exp.ofFalse();

    for (int i = 1; i < args.size(); i++) {
      Expression enc = args.get(i);
      Exceptions.checkArgumentItemType(getFunction(), ItemType.ENCOUNTER, enc, i + 1, c2r);

      List<Value> encTypes = c2r.getValues(enc.getEntityId());
      if (encTypes == null || encTypes.isEmpty()) continue;

      for (Value pheVal : phe.getValues())
        for (Value encTypeVal : encTypes) if (check(pheVal, encTypeVal)) return Exp.ofTrue();
    }

    return Exp.ofFalse();
  }

  private boolean check(Value pheVal, Value encTypeVal) {
    if (pheVal.getFields() == null) return false;
    Value pheEncIdVal = pheVal.getFields().get("encounterId");
    if (pheEncIdVal == null) return false;
    String pheEncId = Values.getStringValue(pheEncIdVal);
    if (pheEncId == null) return false;

    if (encTypeVal.getFields() == null) return false;
    Value encIdVal = encTypeVal.getFields().get("id");
    if (encIdVal == null) return false;
    String encId = Values.getStringValue(encIdVal);
    if (encId == null) return false;

    return pheEncId.equals(encId);
  }
}
