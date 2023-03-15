package care.smith.top.top_phenotypic_query.h2;

public class FKField extends Field {

  public FKField(String name, Table refTab) {
    super(name, "bigint");
    notNull = true;
    props = "REFERENCES " + refTab.getName();
  }

  public Value value(int val) {
    return new Value(val);
  }
}
