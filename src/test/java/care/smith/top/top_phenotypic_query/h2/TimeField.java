package care.smith.top.top_phenotypic_query.h2;

import java.time.LocalDateTime;

public class TimeField extends Field {

  public TimeField(String name) {
    super(name, "timestamp with time zone");
    notNull = true;
    props = "DEFAULT CURRENT_TIMESTAMP";
  }

  public Value value(LocalDateTime val) {
    return new Value(val);
  }
}
