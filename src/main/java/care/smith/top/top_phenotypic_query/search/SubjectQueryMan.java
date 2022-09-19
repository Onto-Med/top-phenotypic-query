package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;
import java.util.Set;

import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.simple_onto_api.calculator.Calculator;
import care.smith.top.simple_onto_api.calculator.expressions.MathExpression;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;
import care.smith.top.top_phenotypic_query.util.PhenotypeUtil;

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

  public void addSexVariable(Phenotype sexVariable) {
    addVariable(
        sexVariable, sexInclusion, sexExclusion, sexPhenotypeVariable, sexRestrictionVariables);
  }

  public void addBirthdateVariable(Phenotype birthdateVariable) {
    addVariable(
        birthdateVariable,
        birthdateInclusion,
        birthdateExclusion,
        birthdatePhenotypeVariable,
        birthdateRestrictionVariables);
  }

  public void addAgeVariable(Phenotype ageVariable) {
    addVariable(
        ageVariable, ageInclusion, ageExclusion, agePhenotypeVariable, ageRestrictionVariables);
  }

  private void addVariable(
      Phenotype var, Phenotype inc, Phenotype exc, Phenotype pheVar, Set<Phenotype> restrVars) {
    if (var.getId().equals(inc.getId())
        || var.getId().equals(exc.getId())
        || (inc.getSuperPhenotype() != null
            && (var.getId().equals(inc.getSuperPhenotype().getId())))) return;
    if (PhenotypeUtil.isPhenotype(var)) pheVar = var;
    else if (PhenotypeUtil.isRestriction(var)) restrVars.add(var);
  }

  public ResultSet executeInclusion() {
    if (sexInclusion == null && birthdateInclusion == null && ageInclusion == null) return null;
    return adapter.execute(
        new SubjectSearch(null, sexInclusion, birthdateInclusion, ageInclusion, adapter));
  }

  public ResultSet executeExclusion() {
    if (sexExclusion == null && birthdateExclusion == null && ageExclusion == null) return null;
    return adapter.execute(
        new SubjectSearch(null, sexExclusion, birthdateExclusion, ageExclusion, adapter));
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
    for (String sbjId : rs.getSubjectIds()) {
      for (Phenotype restr : restrVars) {
        Calculator calc = new Calculator();
        Phenotypes phes = rs.getPhenotypes(sbjId);
        ValueList vals = phes.getValues(pheVar.getId(), null);
        calc.setVariable(pheVar.getId(), vals);
        MathExpression mathExp = ExpressionUtil.convert(restr.getExpression());
        Value res = calc.calculate(mathExp);
        phes.setValues(restr.getId(), null, ValueList.get(res));
      }
    }
  }
}
