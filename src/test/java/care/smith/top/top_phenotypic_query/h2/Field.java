package care.smith.top.top_phenotypic_query.h2;

public class Field {

  protected String name;
  protected String datatype;
  protected String props;
  protected boolean notNull = false;

  protected Field(String name, String datatype) {
    this.name = name;
    this.datatype = datatype;
  }

  protected Field(String name) {
    this.name = name;
  }

  public Field props(String properties) {
    this.props = properties;
    return this;
  }

  public Field notNull() {
    this.notNull = true;
    return this;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    String field = name + " " + datatype;
    if (notNull) field += " NOT NULL";
    if (props != null && !props.isBlank()) field += " " + props;
    return field;
  }
}
