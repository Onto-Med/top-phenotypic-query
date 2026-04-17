package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Values;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PhenoCombiTime extends ArrayList<Value> {

  private static final long serialVersionUID = 1L;
  private int[] combiIndices;
  private List<List<Value>> sbjRecord;

  protected PhenoCombiTime(int[] combiIndices, List<List<Value>> sbjRecord) {
    this.combiIndices = combiIndices;
    this.sbjRecord = sbjRecord;
  }

  protected BigDecimal getTimeInterval() {
    for (int i = 0; i < combiIndices.length; i++) {
      Value val = sbjRecord.get(i).get(combiIndices[i]);
      if (Values.hasNoDateTime(val)) return null;
      add(val);
    }
    return getTI();
  }

  private BigDecimal getTI() {
    BigDecimal time = null;
    for (int i = 0; i < size() - 1; i++) {
      for (int j = i + 1; j < size(); j++) {
        BigDecimal newTime = Values.getDistanceInHours(get(i), get(j));
        if (time == null || newTime.compareTo(time) > 0) time = newTime;
      }
    }
    return time;
  }

  @Override
  public String toString() {
    return stream()
        .map(v -> DateUtil.format(v.getStartDateTime()) + "|" + DateUtil.format(v.getEndDateTime()))
        .toList()
        .toString();
  }
}
