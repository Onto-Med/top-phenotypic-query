package care.smith.top.top_phenotypic_query.c2reasoner.constants;

import care.smith.top.model.Constant;
import care.smith.top.model.DataType;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.model.StringConstant;
import care.smith.top.model.StringValue;
import care.smith.top.model.Value;

public class Le extends ConstantEntity {

  private static final Le INSTANCE = new Le();

  private Le() {
    super(createConstant(), createValue());
  }

  private static Constant createConstant() {
    return new StringConstant().id("le").title("<=").dataType(DataType.STRING);
  }

  private static Value createValue() {
    return new StringValue()
        .value(RestrictionOperator.LESS_THAN_OR_EQUAL_TO.getValue())
        .dataType(DataType.STRING);
  }

  public static Le get() {
    return INSTANCE;
  }
}
