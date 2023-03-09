package care.smith.top.top_phenotypic_query.search;

import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Phenotypes;

public class PhenotypeFinder {

  private PhenotypeQuery query;
  private Entities phenotypes;
  private DataAdapter adapter;
  private DataAdapterConfig config;

  public PhenotypeFinder(PhenotypeQuery query, Entity[] entities, DataAdapter adapter) {
    this.query = query;
    this.adapter = adapter;
    this.config = adapter.getConfig();
    this.phenotypes = Entities.of(entities).deriveAdditionalProperties(config);
  }

  public Entities getPhenotypes() {
    return phenotypes;
  }

  public ResultSet execute() {
    return executeCompositeSearches(executeSingleSearches());
  }

  private ResultSet executeCompositeSearches(ResultSet rs) {
    for (QueryCriterion cri : query.getCriteria()) {
      Phenotype phe = phenotypes.getPhenotype(cri.getSubjectId());
      if (Phenotypes.isComposite(phe)) new CompositeSearch(query, cri, rs, phenotypes).execute();
    }
    return rs;
  }

  private ResultSet executeSingleSearches() {
    SubjectQueryMan sbjMan = new SubjectQueryMan(adapter);
    SingleQueryMan man = new SingleQueryMan(sbjMan, query, phenotypes);
    for (QueryCriterion cri : query.getCriteria()) {
      Phenotype phe = phenotypes.getPhenotype(cri.getSubjectId());
      if (Phenotypes.isSingle(phe)) {
        if (config.isAge(phe)) sbjMan.setAgeCriterion(cri, phe);
        else if (config.isBirthdate(phe)) sbjMan.setBirthdateCriterion(cri, phe);
        else if (config.isSex(phe)) sbjMan.setSexCriterion(cri, phe);
        else man.addCriterion(new SingleSearch(query, cri, phe, adapter, true));
      }
    }

    for (QueryCriterion cri : query.getCriteria()) {
      Phenotype phe = phenotypes.getPhenotype(cri.getSubjectId());
      if (Phenotypes.isComposite(phe)) {
        for (String var : Expressions.getVariables(phe.getExpression(), phenotypes)) {
          Phenotype varPhe = phenotypes.getPhenotype(var);
          if (Phenotypes.isSingle(varPhe)) {
            if (config.isAge(varPhe)) sbjMan.addAgeVariable(varPhe);
            else if (config.isBirthdate(varPhe)) sbjMan.addBirthdateVariable(varPhe);
            else if (config.isSex(varPhe)) sbjMan.addSexVariable(varPhe);
            else man.addVariable(new SingleSearch(query, cri, varPhe, adapter, false));
          }
        }
      }
    }
    return man.execute();
  }
}
