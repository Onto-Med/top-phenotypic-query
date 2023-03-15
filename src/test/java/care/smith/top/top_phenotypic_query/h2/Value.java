package care.smith.top.top_phenotypic_query.h2;

import java.time.LocalDateTime;

import care.smith.top.top_phenotypic_query.util.DateUtil;

public class Value {
  private String val;

  protected Value(String val) {
    this.val = "'" + val + "'";
  }

  protected Value(Number val) {
    this.val = val.toString();
  }

  protected Value(LocalDateTime val) {
    this.val = "'" + DateUtil.format(val) + "'";
  }

  protected Value(Boolean val) {
    this.val = val.toString();
  }

  @Override
  public String toString() {
    return val;
  }
}
