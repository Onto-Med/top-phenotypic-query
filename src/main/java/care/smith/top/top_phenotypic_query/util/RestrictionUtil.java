package care.smith.top.top_phenotypic_query.util;

import care.smith.top.backend.model.DateTimeRestriction;

public class RestrictionUtil {

  public static StringBuffer toString(DateTimeRestriction dtr) {
    StringBuffer sb = new StringBuffer("[ ");
    if (dtr.getMinOperator() != null)
      sb.append(dtr.getMinOperator()).append(dtr.getValues().get(0)).append(" ");
    if (dtr.getMaxOperator() != null)
      sb.append(dtr.getMaxOperator()).append(dtr.getValues().get(1)).append(" ");
    return sb.append("]");
  }
}
