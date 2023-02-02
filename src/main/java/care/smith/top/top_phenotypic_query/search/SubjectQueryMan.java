package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Set;

import care.smith.top.model.Phenotype;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
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
    if (sexInclusion == null && birthdateInclusion == null && ageInclusion == null) return null;
    return adapter.execute(
        new SubjectSearch(null, sexInclusion, birthdateInclusion, ageInclusion, adapter));
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

  public ResultSet executeVariables() {
    ResultSet rs =
        adapter.execute(
            new SubjectSearch(
                null,
                sexPhenotypeVariable,
                birthdatePhenotypeVariable,
                agePhenotypeVariable,
                adapter));
    if (rs == null || rs.isEmpty()) return null;
    checkRestrictions(rs, sexPhenotypeVariable, sexRestrictionVariables);
    checkRestrictions(rs, birthdatePhenotypeVariable, birthdateRestrictionVariables);
    checkRestrictions(rs, agePhenotypeVariable, ageRestrictionVariables);
    return rs;
  }

  private void checkRestrictions(ResultSet rs, Phenotype pheVar, Set<Phenotype> restrVars) {
    if (pheVar == null || restrVars.isEmpty()) return;
    for (SubjectPhenotypes sbjPhes : rs.getPhenotypes()) {
      for (Phenotype restr : restrVars) {
        Entities phes = Entities.of(pheVar, restr);
        C2R calc = new C2R().phenotypes(phes).values(sbjPhes);
        calc.calculate(restr);
      }
    }
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
