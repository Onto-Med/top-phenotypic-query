package care.smith.top.top_phenotypic_query.h2;

public class NumberField extends Field {

  public NumberField(String name) {
    super(name, "numeric");
  }

  public Value value(Number val) {
    return new Value(val);
  }
}
