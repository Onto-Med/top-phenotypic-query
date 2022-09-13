package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Set;

import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class SubjectQueryMan {

  private DataAdapter adapter;
  private DataAdapterConfig config;

  private Phenotype sexInclusion;
  private Phenotype birthdateInclusion;
  private Phenotype ageInclusion;

  private Phenotype sexExclusion;
  private Phenotype birthdateExclusion;
  private Phenotype ageExclusion;

  private Set<Phenotype> sexVariables = new HashSet<>();
  private Set<Phenotype> birthdateVariables = new HashSet<>();
  private Set<Phenotype> ageVariables = new HashSet<>();

  public SubjectQueryMan(DataAdapter adapter) {
    this.adapter = adapter;
    this.config = adapter.getConfig();
  }

  public void setSexCriterion(QueryCriterion criterion) {
    if (criterion.isExclusion()) this.sexExclusion = criterion.getSubject();
    else this.sexInclusion = criterion.getSubject();
  }

  public void setBirthdateCriterion(QueryCriterion criterion) {
    if (criterion.isExclusion()) this.birthdateExclusion = criterion.getSubject();
    else this.birthdateInclusion = criterion.getSubject();
  }

  public void setAgeCriterion(QueryCriterion criterion) {
    if (criterion.isExclusion()) this.ageExclusion = criterion.getSubject();
    else this.ageInclusion = criterion.getSubject();
  }

  public Phenotype getSexInclusion() {
    return sexInclusion;
  }

  public void setSexInclusion(Phenotype sexInclusion) {
    this.sexInclusion = sexInclusion;
  }

  public Phenotype getBirthdateInclusion() {
    return birthdateInclusion;
  }

  public void setBirthdateInclusion(Phenotype birthdateInclusion) {
    this.birthdateInclusion = birthdateInclusion;
  }

  public Phenotype getAgeInclusion() {
    return ageInclusion;
  }

  public void setAgeInclusion(Phenotype ageInclusion) {
    this.ageInclusion = ageInclusion;
  }

  public Phenotype getSexExclusion() {
    return sexExclusion;
  }

  public void setSexExclusion(Phenotype sexExclusion) {
    this.sexExclusion = sexExclusion;
  }

  public Phenotype getBirthdateExclusion() {
    return birthdateExclusion;
  }

  public void setBirthdateExclusion(Phenotype birthdateExclusion) {
    this.birthdateExclusion = birthdateExclusion;
  }

  public Phenotype getAgeExclusion() {
    return ageExclusion;
  }

  public void setAgeExclusion(Phenotype ageExclusion) {
    this.ageExclusion = ageExclusion;
  }

  public Set<Phenotype> getSexVariables() {
    return sexVariables;
  }

  public void addSexVariable(Phenotype sexVariable) {
    this.sexVariables.add(sexVariable);
  }

  public void setSexVariables(Set<Phenotype> sexVariables) {
    this.sexVariables = sexVariables;
  }

  public Set<Phenotype> getBirthdateVariables() {
    return birthdateVariables;
  }

  public void addBirthdateVariable(Phenotype birthdateVariable) {
    this.birthdateVariables.add(birthdateVariable);
  }

  public void setBirthdateVariables(Set<Phenotype> birthdateVariables) {
    this.birthdateVariables = birthdateVariables;
  }

  public Set<Phenotype> getAgeVariables() {
    return ageVariables;
  }

  public void addAgeVariable(Phenotype ageVariable) {
    this.ageVariables.add(ageVariable);
  }

  public void setAgeVariables(Set<Phenotype> ageVariables) {
    this.ageVariables = ageVariables;
  }

  public ResultSet execute() {
    return null;
  }
}
