package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Phenotypes;

public class SingleQueryMan {

  private static final Logger log = LoggerFactory.getLogger(SingleQueryMan.class);

  private Set<SingleSearch> inclusions = new HashSet<>();
  private Set<SingleSearch> exclusions = new HashSet<>();
  private Set<SingleSearch> variables = new HashSet<>();
  private SubjectQueryMan subjectQueryMan;

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
    if (inclusions.contains(variable) || exclusions.contains(variable)) return;

    Phenotype varPhe = variable.getPhenotype();
    if (Phenotypes.isSinglePhenotype(varPhe)) {
      for (SingleSearch inc : inclusions) {
        Phenotype incPhe = inc.getPhenotype();
        if (Phenotypes.isSingleRestriction(incPhe)
            && incPhe.getSuperPhenotype().getId().equals(varPhe.getId())) return;
      }
    }

    variables.add(variable);
  }

  public void setSubjectQueryMan(SubjectQueryMan man) {
    this.subjectQueryMan = man;
  }

  public ResultSet execute() {
    ResultSet rs = null;

    if (!subjectQueryMan.hasInclusion() && inclusions.isEmpty()) {
      rs = subjectQueryMan.executeAllSubjectsQuery();
      if (rs.isEmpty()) return rs;
    } else {
      if (subjectQueryMan.hasInclusion()) {
        rs = subjectQueryMan.executeInclusion();
        if (rs.isEmpty()) return rs;
      }
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

    if (subjectQueryMan.hasSexExclusion()) {
      ResultSet sexExc = subjectQueryMan.executeSexExclusion();
      if (!sexExc.isEmpty()) rs = rs.subtract(sexExc);
    }

    if (subjectQueryMan.hasBirthdateExclusion()) {
      ResultSet bdExc = subjectQueryMan.executeBirthdateExclusion();
      if (!bdExc.isEmpty()) rs = rs.subtract(bdExc);
    }

    for (SingleSearch exc : exclusions) {
      ResultSet res = exc.execute();
      if (res.isEmpty()) continue;
      rs = rs.subtract(res);
      if (rs.isEmpty()) return rs;
    }

    if ((!subjectQueryMan.hasInclusion() && !inclusions.isEmpty())
        && subjectQueryMan.hasVariables()) {
      ResultSet sbjVars = subjectQueryMan.executeAllSubjectsQuery();
      if (!sbjVars.isEmpty()) rs = rs.insert(sbjVars);
    }

    for (SingleSearch var : variables) {
      ResultSet res = var.execute();
      if (res.isEmpty()) continue;
      rs = rs.insert(res);
    }

    log.debug(rs.toString());

    return rs;
  }
}
