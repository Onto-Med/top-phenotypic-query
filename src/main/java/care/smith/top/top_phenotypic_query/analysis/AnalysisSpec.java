package care.smith.top.top_phenotypic_query.analysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class AnalysisSpec {

  @JsonProperty private List<String> phenotypes;
  @JsonProperty private int time;

  public List<String> getPhenotypes() {
    return phenotypes;
  }

  public AnalysisSpec phenotypes(List<String> phenotypes) {
    this.phenotypes = phenotypes;
    return this;
  }

  public AnalysisSpec addPhenotype(String phenotype) {
    if (phenotypes == null) phenotypes = new ArrayList<>();
    phenotypes.add(phenotype);
    return this;
  }

  public int getTime() {
    return time;
  }

  public AnalysisSpec time(int time) {
    this.time = time;
    return this;
  }

  @Override
  public String toString() {
    return String.format("{phenotypes=%s, time=%s}", phenotypes, time);
  }
}
