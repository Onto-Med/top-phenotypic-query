package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Set;

import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class SingleQueryMan {

  private Set<SingleSearch> inclusions = new HashSet<>();
  private Set<SingleSearch> exclusions = new HashSet<>();
  private Set<SingleSearch> variables = new HashSet<>();
  private SubjectSearch subjectSearch;

  private DataAdapter adapter;

  public SingleQueryMan(DataAdapter adapter) {
    this.adapter = adapter;
  }

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

  public void setSubjectSearch(SubjectSearch subjectSearch) {
    this.subjectSearch = subjectSearch;
  }

  public ResultSet execute() {
    ResultSet rs = null;

    if (inclusions.isEmpty()) {
      rs = adapter.executeAllSubjectsQuery();
      if (rs.isEmpty()) return rs;
    } else {
      for (SingleSearch inc : inclusions) {
        ResultSet res = inc.execute();
        if (res.isEmpty()) return res;
        if (rs == null) rs = res;
        else {
          rs = rs.intersect(res);
          if (rs.isEmpty()) return rs;
        }
      }
    }

    for (SingleSearch exc : exclusions) {
      ResultSet res = exc.execute();
      if (res.isEmpty()) continue;
      rs = rs.subtract(res);
      if (rs.isEmpty()) return rs;
    }

    for (SingleSearch var : variables) {
      ResultSet res = var.execute();
      if (res.isEmpty()) continue;
      rs = rs.insert(res);
    }

    return rs;
  }
}
