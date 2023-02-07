package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import care.smith.top.model.Phenotype;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Queries.QueryType;

public class SubjectQueryMan {

  private DataAdapter adapter;

  private Phenotype sexInclusion;
  private Phenotype birthdateInclusion;
  private Phenotype ageInclusion;

  private Phenotype sexExclusion;
  private Phenotype birthdateExclusion;
  private Phenotype ageExclusion;

  private Phenotype sexPhenotypeVariable;
  private Set<Phenotype> sexRestrictionVariables = new HashSet<>();
  private Phenotype birthdatePhenotypeVariable;
  private Set<Phenotype> birthdateRestrictionVariables = new HashSet<>();
  private Phenotype agePhenotypeVariable;
  private Set<Phenotype> ageRestrictionVariables = new HashSet<>();

  public SubjectQueryMan(DataAdapter adapter) {
    this.adapter = adapter;
  }

  public void setSexCriterion(QueryCriterion criterion, Phenotype phenotype) {
    if (criterion.isInclusion()) this.sexInclusion = phenotype;
    else this.sexExclusion = phenotype;
  }

  public void setBirthdateCriterion(QueryCriterion criterion, Phenotype phenotype) {
    if (criterion.isInclusion()) this.birthdateInclusion = phenotype;
    else this.birthdateExclusion = phenotype;
  }

  public void setAgeCriterion(QueryCriterion criterion, Phenotype phenotype) {
    if (criterion.isInclusion()) this.ageInclusion = phenotype;
    else this.ageExclusion = phenotype;
  }

  public void addSexVariable(Phenotype sexVariable) {
    if (contains(sexVariable, sexInclusion, sexExclusion)) return;
    if (Phenotypes.isPhenotype(sexVariable)) sexPhenotypeVariable = sexVariable;
    else if (Phenotypes.isRestriction(sexVariable)) {
      sexRestrictionVariables.add(sexVariable);
      sexPhenotypeVariable = sexVariable.getSuperPhenotype();
    }
  }

  public void addBirthdateVariable(Phenotype birthdateVariable) {
    if (contains(birthdateVariable, birthdateInclusion, birthdateExclusion)) return;
    if (Phenotypes.isPhenotype(birthdateVariable)) birthdatePhenotypeVariable = birthdateVariable;
    else if (Phenotypes.isRestriction(birthdateVariable)) {
      birthdateRestrictionVariables.add(birthdateVariable);
      birthdatePhenotypeVariable = birthdateVariable.getSuperPhenotype();
    }
  }

  public void addAgeVariable(Phenotype ageVariable) {
    if (contains(ageVariable, ageInclusion, ageExclusion)) return;
    if (Phenotypes.isPhenotype(ageVariable)) agePhenotypeVariable = ageVariable;
    else if (Phenotypes.isRestriction(ageVariable)) {
      ageRestrictionVariables.add(ageVariable);
      agePhenotypeVariable = ageVariable.getSuperPhenotype();
    }
  }

  private boolean contains(Phenotype var, Phenotype inc, Phenotype exc) {
    if (inc != null && var.getId().equals(inc.getId())) return true;
    if (exc != null && var.getId().equals(exc.getId())) return true;
    if (inc != null
        && inc.getSuperPhenotype() != null
        && (var.getId().equals(inc.getSuperPhenotype().getId()))) return true;
    return false;
  }

  public ResultSet executeInclusion() {
    if (!hasInclusion()) return null;
    Phenotype sex = (sexInclusion != null) ? sexInclusion : sexPhenotypeVariable;
    Phenotype bd = (birthdateInclusion != null) ? birthdateInclusion : birthdatePhenotypeVariable;
    Phenotype age = (ageInclusion != null) ? ageInclusion : agePhenotypeVariable;
    return adapter.execute(new SubjectSearch(null, sex, bd, age, adapter));
  }

  public ResultSet executeSexExclusion() {
    if (sexExclusion == null) return null;
    return adapter.execute(new SubjectSearch(null, sexExclusion, null, null, adapter));
  }

  public ResultSet executeBirthdateExclusion() {
    if (birthdateExclusion == null && ageExclusion == null) return null;
    return adapter.execute(
        new SubjectSearch(null, null, birthdateExclusion, ageExclusion, adapter));
  }

  public ResultSet executeAllSubjectsQuery() {
    Phenotype sex = getParameter(sexInclusion, sexExclusion, sexPhenotypeVariable);
    Phenotype bd = getParameter(birthdateInclusion, birthdateExclusion, birthdatePhenotypeVariable);
    Phenotype age = getParameter(ageInclusion, ageExclusion, agePhenotypeVariable);
    return adapter.execute(new SubjectSearch(null, sex, bd, age, adapter));
  }

  public ResultSet executeVariables(QueryType queryType) {
    if (queryType == QueryType.TYPE_3 || !hasVariables() || hasInclusion()) return new ResultSet();
    if ((sexRestrictionVariables.isEmpty()
            && birthdateRestrictionVariables.isEmpty()
            && ageRestrictionVariables.isEmpty())
        || sexRestrictionVariables.size() > 1
        || birthdateRestrictionVariables.size() + ageRestrictionVariables.size() > 1)
      return executeAllSubjectsQuery();
    return executeRestrictedVariables();
  }

  private ResultSet executeRestrictedVariables() {
    ResultSet rs = new ResultSet();
    rs = uniteVariables(rs, executeSexVariable());
    rs = uniteVariables(rs, executeBirthdateVariable());
    return rs;
  }

  private ResultSet executeBirthdateVariable() {
    Phenotype age = agePhenotypeVariable;
    Phenotype bd = birthdatePhenotypeVariable;
    if (!ageRestrictionVariables.isEmpty()) age = ageRestrictionVariables.iterator().next();
    if (!birthdateRestrictionVariables.isEmpty())
      bd = birthdateRestrictionVariables.iterator().next();
    return adapter.execute(new SubjectSearch(null, null, bd, age, adapter));
  }

  private ResultSet executeSexVariable() {
    Phenotype sex = sexPhenotypeVariable;
    if (!sexRestrictionVariables.isEmpty()) sex = sexRestrictionVariables.iterator().next();
    return adapter.execute(new SubjectSearch(null, sex, null, null, adapter));
  }

  private ResultSet uniteVariables(ResultSet rs, ResultSet varRs) {
    if (varRs.isEmpty()) return rs;
    if (rs.isEmpty()) return varRs;
    return rs.unite(varRs);
  }

  public boolean hasInclusion() {
    return hasParameter(sexInclusion, birthdateInclusion, ageInclusion);
  }

  public boolean hasExclusion() {
    return hasSexExclusion() || hasBirthdateExclusion();
  }

  public boolean hasSexExclusion() {
    return hasParameter(sexExclusion);
  }

  public boolean hasBirthdateExclusion() {
    return hasParameter(birthdateExclusion, ageExclusion);
  }

  public boolean hasVariables() {
    return hasParameter(sexPhenotypeVariable, birthdatePhenotypeVariable, agePhenotypeVariable);
  }

  public boolean hasParameter() {
    return hasInclusion() || hasSexExclusion() || hasBirthdateExclusion() || hasVariables();
  }

  private boolean hasParameter(Phenotype... phes) {
    for (Phenotype p : phes) if (p != null) return true;
    return false;
  }

  // has only unrestricted or more than two restricted parameters of the same unrestricted phenotype
  public boolean hasComplexParameters() {
    Phenotype sex = getParameter(sexInclusion, sexExclusion, sexPhenotypeVariable);
    Phenotype bd = getParameter(birthdateInclusion, birthdateExclusion, birthdatePhenotypeVariable);
    Phenotype age = getParameter(ageInclusion, ageExclusion, agePhenotypeVariable);
    int sexRestrNum = countRestrictions(sexInclusion, sexExclusion, sexRestrictionVariables);
    int bdRestrNum =
        countRestrictions(birthdateInclusion, birthdateExclusion, birthdateRestrictionVariables);
    int ageRestrNum = countRestrictions(ageInclusion, ageExclusion, ageRestrictionVariables);
    return (((sex != null || bd != null || age != null)
            && (sexRestrNum + bdRestrNum + ageRestrNum) == 0)
        || sexRestrNum > 1
        || bdRestrNum + ageRestrNum > 1);
  }

  private Phenotype getParameter(Phenotype... phes) {
    for (Phenotype p : phes) {
      if (p == null) continue;
      if (Phenotypes.isPhenotype(p)) return p;
      if (Phenotypes.isRestriction(p)) return p.getSuperPhenotype();
    }
    return null;
  }

  private int countRestrictions(Phenotype inc, Phenotype exc, Set<Phenotype> restrVars) {
    Set<String> restrictions = restrVars.stream().map(Phenotype::getId).collect(Collectors.toSet());
    if (inc != null) restrictions.add(inc.getId());
    if (exc != null) restrictions.add(exc.getId());
    return restrictions.size();
  }

  public ResultSet calculateInclusion(ResultSet main) {
    if (main == null || !hasInclusion()) return null;

    Entities phes = getInclusionEntities();
    for (String sbjId : new HashSet<>(main.getSubjectIds())) {
      if (fulfillInclusion(phes, sbjId, sexInclusion, main))
        if (fulfillInclusion(phes, sbjId, birthdateInclusion, main))
          fulfillInclusion(phes, sbjId, ageInclusion, main);
    }

    return main;
  }

  public ResultSet calculateExclusion(ResultSet main) {
    if (main == null || !hasExclusion()) return null;

    Entities phes = getExclusionEntities();
    for (String sbjId : new HashSet<>(main.getSubjectIds())) {
      if (!fulfillExclusion(phes, sbjId, sexExclusion, main))
        if (!fulfillExclusion(phes, sbjId, birthdateExclusion, main))
          fulfillExclusion(phes, sbjId, ageExclusion, main);
    }

    return main;
  }

  private boolean fulfillInclusion(Entities phes, String sbjId, Phenotype inc, ResultSet main) {
    if (inc == null) return true;
    boolean res = calculateCriterion(phes, main.getPhenotypes(sbjId), inc);
    if (!res) main.removeSubject(sbjId);
    return res;
  }

  private boolean fulfillExclusion(Entities phes, String sbjId, Phenotype exc, ResultSet main) {
    if (exc == null) return false;
    boolean res = calculateCriterion(phes, main.getPhenotypes(sbjId), exc);
    if (res) main.removeSubject(sbjId);
    return res;
  }

  private boolean calculateCriterion(Entities phes, SubjectPhenotypes sbjPhes, Phenotype cri) {
    return Expressions.getBooleanValue(new C2R().phenotypes(phes).values(sbjPhes).calculate(cri));
  }

  private Entities getInclusionEntities() {
    Entities e = Entities.of();
    if (sexInclusion != null) e.add(sexInclusion, sexInclusion.getSuperPhenotype());
    if (birthdateInclusion != null)
      e.add(birthdateInclusion, birthdateInclusion.getSuperPhenotype());
    if (ageInclusion != null) e.add(ageInclusion, ageInclusion.getSuperPhenotype());
    return e;
  }

  private Entities getExclusionEntities() {
    Entities e = Entities.of();
    if (sexExclusion != null) e.add(sexExclusion, sexExclusion.getSuperPhenotype());
    if (birthdateExclusion != null)
      e.add(birthdateExclusion, birthdateExclusion.getSuperPhenotype());
    if (ageExclusion != null) e.add(ageExclusion, ageExclusion.getSuperPhenotype());
    return e;
  }

  @Override
  public String toString() {
    return "SubjectQueryMan [sexInclusion="
        + sexInclusion
        + ", birthdateInclusion="
        + birthdateInclusion
        + ", ageInclusion="
        + ageInclusion
        + ", sexExclusion="
        + sexExclusion
        + ", birthdateExclusion="
        + birthdateExclusion
        + ", ageExclusion="
        + ageExclusion
        + ", sexPhenotypeVariable="
        + sexPhenotypeVariable
        + ", sexRestrictionVariables="
        + sexRestrictionVariables
        + ", birthdatePhenotypeVariable="
        + birthdatePhenotypeVariable
        + ", birthdateRestrictionVariables="
        + birthdateRestrictionVariables
        + ", agePhenotypeVariable="
        + agePhenotypeVariable
        + ", ageRestrictionVariables="
        + ageRestrictionVariables
        + "]";
  }
}
