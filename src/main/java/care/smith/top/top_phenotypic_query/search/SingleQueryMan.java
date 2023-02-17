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
  private SubjectQueryMan sbjQueryMan;
  private Query query;
  private Entities phenotypes;

  public SingleQueryMan(SubjectQueryMan subjectQueryMan, Query query, Entities phenotypes) {
    this.sbjQueryMan = subjectQueryMan;
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

  //  public ResultSet execute() {
  //    QueryType queryType = Queries.getType(query, phenotypes, subjectQueryMan, this);
  //    ResultSet main = null;
  //    if (queryType == QueryType.TYPE_1) {
  //      log.debug("TYPE 1 Single Query");
  //      main = executeStandardSearch(new ResultSet(), true);
  //    } else if (queryType == QueryType.TYPE_2) {
  //      log.debug("TYPE 2 Single Query");
  //      main = subjectQueryMan.executeVariables(queryType);
  //      for (SingleSearch var : variables) main = unite(main, var.execute());
  //      main = executeStandardSearch(main, false);
  //    } else if (queryType == QueryType.TYPE_3) {
  //      log.debug("TYPE 3 Single Query");
  //      main = executeStandardSearch(subjectQueryMan.executeAllSubjectsQuery(), true);
  //    }
  //    log.debug(main.toString());
  //
  //    return main;
  //  }
  public ResultSet execute() {
    QueryType queryType = Queries.getType(query, phenotypes, sbjQueryMan, this);
    ResultSet main = null;

    if (queryType == QueryType.TYPE_1) main = executeType1Query();
    else if (queryType == QueryType.TYPE_2) main = executeType2Query();
    else if (queryType == QueryType.TYPE_3) main = executeType3Query();
    else main = executeType4Query();

    return main;
  }

  private ResultSet executeType1Query() {
    log.debug("TYPE 1 Single Query");

    ResultSet main = sbjQueryMan.executeAllSubjectsQuery();
    if (main.isEmpty()) return main;

    main = executeSingleInclusions(main);
    if (main.isEmpty()) return main;

    main = executeSingleExclusions(main);
    if (main.isEmpty()) return main;

    main = sbjQueryMan.calculateExclusion(main);
    if (main.isEmpty()) return main;

    main = executeSingleVariables(main, true);

    log.debug(main.toString());

    return main;
  }

  private ResultSet executeType2Query() {
    log.debug("TYPE 2 Single Query");

    ResultSet main = sbjQueryMan.executeInclusion();
    if (main.isEmpty()) return main;

    main = executeSingleInclusions(main);
    if (main.isEmpty()) return main;

    main = executeSingleExclusions(main);
    if (main.isEmpty()) return main;

    main = sbjQueryMan.calculateExclusion(main);
    if (main.isEmpty()) return main;

    main = executeSingleVariables(main, true);

    log.debug(main.toString());

    return main;
  }

  private ResultSet executeType3Query() {
    log.debug("TYPE 3 Single Query");

    ResultSet main = executeSingleInclusions(new ResultSet());
    if (main.isEmpty()) return main;

    main = executeSingleExclusions(main);
    if (main.isEmpty()) return main;

    main = executeSubjectExclusions(main);
    if (main.isEmpty()) return main;

    main = executeSingleVariables(main, true);

    main = executeSubjectVariables(main, true);

    log.debug(main.toString());

    return main;
  }

  private ResultSet executeType4Query() {
    log.debug("TYPE 4 Single Query");

    ResultSet main = executeSingleVariables(new ResultSet(), false);

    main = executeSubjectVariables(main, false);

    main = executeSingleExclusions(main);
    if (main.isEmpty()) return main;

    main = executeSubjectExclusions(main);
    if (main.isEmpty()) return main;

    log.debug(main.toString());

    return main;
  }

  private ResultSet executeSingleInclusions(ResultSet main) {
    for (SingleSearch inc : inclusions) {
      if (main.isEmpty()) {
        main = inc.execute();
        continue;
      }
      main = intersect(main, inc.execute());
      if (main.isEmpty()) return main;
    }
    return main;
  }

  private ResultSet executeSingleExclusions(ResultSet main) {
    for (SingleSearch exc : exclusions) {
      main = subtract(main, exc.execute());
      if (main.isEmpty()) return main;
    }
    return main;
  }

  private ResultSet executeSubjectExclusions(ResultSet main) {
    if (sbjQueryMan.hasSexExclusion()) {
      main = subtract(main, sbjQueryMan.executeSexExclusion());
      if (main.isEmpty()) return main;
    }

    if (sbjQueryMan.hasAgeExclusion()) {
      main = subtract(main, sbjQueryMan.executeAgeExclusion());
      if (main.isEmpty()) return main;
    }

    if (sbjQueryMan.hasBirthdateExclusion())
      return subtract(main, sbjQueryMan.executeBirthdateExclusion());

    return main;
  }

  private ResultSet executeSingleVariables(ResultSet main, boolean insert) {
    if (insert) for (SingleSearch var : variables) main = insert(main, var.execute());
    else for (SingleSearch var : variables) main = unite(main, var.execute());
    return main;
  }

  private ResultSet executeSubjectVariables(ResultSet main, boolean insert) {
    if (sbjQueryMan.hasSexRestrictionVariable()) {
      ResultSet rs = sbjQueryMan.executeSexRestrictionVariable();
      if (insert) main = insert(main, rs);
      else main = unite(main, rs);
    }

    if (sbjQueryMan.hasAgeRestrictionVariable()) {
      ResultSet rs = sbjQueryMan.executeAgeRestrictionVariable();
      if (insert) main = insert(main, rs);
      else main = unite(main, rs);
    }

    if (sbjQueryMan.hasBirthdateRestrictionVariable()) {
      ResultSet rs = sbjQueryMan.executeBirthdateRestrictionVariable();
      if (insert) main = insert(main, rs);
      else main = unite(main, rs);
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
