package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeAnalysisSpec {

  @JsonProperty private List<Integer> periods = new ArrayList<>();
  @JsonProperty private Map<String, List<List<String>>> phenotypes = new HashMap<>();

  protected List<Integer> getPeriods() {
    return periods;
  }

  protected TimeAnalysisSpec periods(List<Integer> periods) {
    this.periods = periods;
    return this;
  }

  protected Map<String, List<List<String>>> getPhenotypes() {
    return phenotypes;
  }

  protected TimeAnalysisSpec phenotypes(Map<String, List<List<String>>> phenotypes) {
    this.phenotypes = phenotypes;
    return this;
  }

  @Override
  public String toString() {
    StringBuffer config =
        new StringBuffer("periods: ")
            .append(periods)
            .append(System.lineSeparator())
            .append(System.lineSeparator());
    for (Map.Entry<String, List<List<String>>> alg : phenotypes.entrySet()) {
      config.append(alg.getKey()).append(System.lineSeparator());
      for (List<String> phens : alg.getValue())
        config.append("  ").append(phens).append(System.lineSeparator());
      config.append(System.lineSeparator());
    }
    return config.toString();
  }
}
