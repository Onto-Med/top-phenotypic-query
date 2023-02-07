package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Queries;
import care.smith.top.top_phenotypic_query.util.Queries.QueryType;

public class SingleQueryMan {

  private static final Logger log = LoggerFactory.getLogger(SingleQueryMan.class);

  private Set<SingleSearch> inclusions = new HashSet<>();
  private Set<SingleSearch> exclusions = new HashSet<>();
  private Set<SingleSearch> variables = new HashSet<>();
  private SubjectQueryMan subjectQueryMan;
  private Query query;
  private Entities phenotypes;

  public SingleQueryMan(SubjectQueryMan subjectQueryMan, Query query, Entities phenotypes) {
    this.subjectQueryMan = subjectQueryMan;
    this.query = query;
    this.phenotypes = phenotypes;
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

  public boolean hasInclusion() {
    return !inclusions.isEmpty();
  }

  public ResultSet execute() {
    QueryType queryType = Queries.getType(query, phenotypes, subjectQueryMan, this);
    ResultSet main = null;
    if (queryType == QueryType.TYPE_1) {
      log.debug("TYPE 1 Single Query");
      main = executeStandardSearch(new ResultSet(), true);
    } else if (queryType == QueryType.TYPE_2) {
      log.debug("TYPE 2 Single Query");
      main = subjectQueryMan.executeVariables(queryType);
      for (SingleSearch var : variables) main = unite(main, var.execute());
      main = executeStandardSearch(main, false);
    } else if (queryType == QueryType.TYPE_3) {
      log.debug("TYPE 3 Single Query");
      main = executeStandardSearch(subjectQueryMan.executeAllSubjectsQuery(), true);
    }
    log.debug(main.toString());

    return main;
  }

  public ResultSet executeStandardSearch(ResultSet main, boolean withVariables) {
    if (subjectQueryMan.hasInclusion()) {
      main = intersect(main, subjectQueryMan.executeInclusion());
      if (main.isEmpty()) return main;
    }

    for (SingleSearch inc : inclusions) {
      main = intersect(main, inc.execute());
      if (main.isEmpty()) return main;
    }

    if (subjectQueryMan.hasSexExclusion()) {
      main = subtract(main, subjectQueryMan.executeSexExclusion());
      if (main.isEmpty()) return main;
    }

    if (subjectQueryMan.hasBirthdateExclusion()) {
      main = subtract(main, subjectQueryMan.executeBirthdateExclusion());
      if (main.isEmpty()) return main;
    }

    for (SingleSearch exc : exclusions) {
      main = subtract(main, exc.execute());
      if (main.isEmpty()) return main;
    }

    if (withVariables) {
      main = insert(main, subjectQueryMan.executeVariables(queryType));
      for (SingleSearch var : variables) main = insert(main, var.execute());
    }

    return main;
  }

  private ResultSet intersect(ResultSet main, ResultSet rs) {
    if (rs.isEmpty() || main.isEmpty()) return rs;
    return main.intersect(rs);
  }

  private ResultSet subtract(ResultSet main, ResultSet rs) {
    if (rs.isEmpty() || main.isEmpty()) return main;
    return main.subtract(rs);
  }

  private ResultSet insert(ResultSet main, ResultSet rs) {
    if (rs.isEmpty() || main.isEmpty()) return main;
    return main.insert(rs);
  }

  private ResultSet unite(ResultSet main, ResultSet rs) {
    if (main.isEmpty()) return rs;
    if (rs.isEmpty()) return main;
    return main.unite(rs);
  }
}
