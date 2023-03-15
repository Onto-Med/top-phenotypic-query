package care.smith.top.top_phenotypic_query.h2;

public class TextField extends Field {

  public TextField(String name) {
    super(name, "text");
  }

  public Value value(String val) {
    return new Value(val);
  }
}
