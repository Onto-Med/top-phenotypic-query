package care.smith.top.top_phenotypic_query.c2reasoner.functions.encounter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import care.smith.top.model.DataType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.c2reasoner.Exceptions;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class EncAge extends FunctionEntity {

  private static EncAge INSTANCE = new EncAge();

  private EncAge() {
    super("encAge", NotationEnum.PREFIX, 2, 2);
  }

  public static EncAge get() {
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

    Expression bd = args.get(0);
    Exceptions.checkArgumentItemType(getFunction(), ItemType.SUBJECT_BIRTH_DATE, bd, 1, c2r);
    bd = c2r.calculate(bd);
    if (!Expressions.hasValues(bd)) return null;
    Exceptions.checkArgumentType(getFunction(), DataType.DATE_TIME, bd);
    LocalDateTime birthdate = Expressions.getDateTimeValue(bd);

    Expression enc = args.get(1);
    Exceptions.checkArgumentItemType(getFunction(), ItemType.ENCOUNTER, enc, 2, c2r);

    List<Value> encTypes = c2r.getValues(enc.getEntityId());
    if (encTypes == null || encTypes.isEmpty()) return null;

    Optional<LocalDateTime> encStart =
        encTypes.stream()
            .sorted(Values.VALUE_DATE_COMPARATOR)
            .skip(encTypes.size() - 1)
            .findFirst()
            .map(v -> v.getStartDateTime());

    if (encStart.isEmpty()) return null;

    return Exp.of(DateUtil.getPeriodInYears(birthdate, encStart.get()));
  }
}
