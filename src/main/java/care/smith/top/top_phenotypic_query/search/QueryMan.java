package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Set;

import care.smith.top.top_phenotypic_query.result.ResultSet;

public class QueryMan {

  private Set<SingleSearch> inclusions = new HashSet<>();
  private Set<SingleSearch> exclusions = new HashSet<>();
  private Set<SingleSearch> variables = new HashSet<>();

  public Set<SingleSearch> getInclusionCriteria() {
    return inclusions;
  }

  public Set<SingleSearch> getExclusionCriteria() {
    return exclusions;
  }

  public Set<SingleSearch> getVariables() {
    return variables;
  }

  public void addCriterion(SingleSearch criterion) {
    if (criterion.isInclusion()) inclusions.add(criterion);
    else if (criterion.isExclusion()) exclusions.add(criterion);
  }

  public void addVariable(SingleSearch variable) {
    if (!inclusions.contains(variable) && !exclusions.contains(variable)) variables.add(variable);
  }

  public ResultSet execute() {
    return null;
  }
}
