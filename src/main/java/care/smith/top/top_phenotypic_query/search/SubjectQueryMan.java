package care.smith.top.top_phenotypic_query.search;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Phenotypes;

public class SubjectQueryMan {

  private DataAdapter adapter;

  private Phenotype sexInclusion;
  private Phenotype birthdateInclusion;
  private Phenotype ageInclusion;

  private Phenotype sexExclusion;
  private Phenotype birthdateExclusion;
  private Phenotype ageExclusion;

  private Phenotype sexPhenotypeVariable;
  private Phenotype birthdatePhenotypeVariable;
  private Phenotype agePhenotypeVariable;

  public SubjectQueryMan(DataAdapter adapter, Query query, Entities phenotypes) {
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
    else sexPhenotypeVariable = sexVariable.getSuperPhenotype();
  }

  public void addBirthdateVariable(Phenotype birthdateVariable) {
    if (contains(birthdateVariable, birthdateInclusion, birthdateExclusion)) return;
    if (Phenotypes.isPhenotype(birthdateVariable)) birthdatePhenotypeVariable = birthdateVariable;
    else birthdatePhenotypeVariable = birthdateVariable.getSuperPhenotype();
  }

  public void addAgeVariable(Phenotype ageVariable) {
    if (contains(ageVariable, ageInclusion, ageExclusion)) return;
    if (Phenotypes.isPhenotype(ageVariable)) agePhenotypeVariable = ageVariable;
    else agePhenotypeVariable = ageVariable.getSuperPhenotype();
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
    Phenotype sex = getUnrestrictedPhenotype(sexInclusion, sexExclusion, sexPhenotypeVariable);
    Phenotype bd =
        getUnrestrictedPhenotype(
            birthdateInclusion, birthdateExclusion, birthdatePhenotypeVariable);
    Phenotype age = getUnrestrictedPhenotype(ageInclusion, ageExclusion, agePhenotypeVariable);
    return adapter.execute(new SubjectSearch(null, sex, bd, age, adapter));
  }

  private Phenotype getUnrestrictedPhenotype(Phenotype... phes) {
    for (Phenotype p : phes) {
      if (p == null) continue;
      if (Phenotypes.isPhenotype(p)) return p;
      if (Phenotypes.isRestriction(p)) return p.getSuperPhenotype();
    }
    return null;
  }

  public boolean hasInclusion() {
    return hasParameter(sexInclusion, birthdateInclusion, ageInclusion);
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

  private boolean hasParameter(Phenotype... phes) {
    for (Phenotype p : phes) if (p != null) return true;
    return false;
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
        + ", birthdatePhenotypeVariable="
        + birthdatePhenotypeVariable
        + ", agePhenotypeVariable="
        + agePhenotypeVariable
        + "]";
  }
}
