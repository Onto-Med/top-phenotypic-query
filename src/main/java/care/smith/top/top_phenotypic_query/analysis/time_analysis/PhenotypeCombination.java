package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.util.Values;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PhenotypeCombination extends ArrayList<Value> {

  private static final long serialVersionUID = 1L;

  protected BigDecimal getTimeInterval() {
    for (Value v : this) if (Values.hasNoDateTime(v)) return null;

    BigDecimal time = null;

    for (int i = 0; i < size() - 1; i++) {
      for (int j = i + 1; j < size(); j++) {
        BigDecimal newTime = Values.getDistanceInHours(get(i), get(j));
        if (time == null || newTime.compareTo(time) > 0) time = newTime;
      }
    }

    return time;
  }
}
