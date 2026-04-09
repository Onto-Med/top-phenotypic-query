package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeAnalysisSpec {

  @JsonProperty private List<Integer> timeIntervals = new ArrayList<>();
  @JsonProperty private Map<String, List<List<String>>> phenotypes = new HashMap<>();

  public List<Integer> getTimeIntervals() {
    return timeIntervals;
  }

  public TimeAnalysisSpec timeIntervals(List<Integer> timeIntervals) {
    this.timeIntervals = timeIntervals;
    return this;
  }

  public Map<String, List<List<String>>> getPhenotypes() {
    return phenotypes;
  }

  public TimeAnalysisSpec phenotypes(Map<String, List<List<String>>> phenotypes) {
    this.phenotypes = phenotypes;
    return this;
  }

  @Override
  public String toString() {
    StringBuffer config =
        new StringBuffer("timeIntervals: ")
            .append(timeIntervals)
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
