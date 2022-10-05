package care.smith.top.top_phenotypic_query.search;

import java.util.Map;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;
import care.smith.top.top_phenotypic_query.util.PhenotypeUtil;

public class PhenotypeFinder {

  private Query query;
  private Map<String, Phenotype> phenotypes;
  private DataAdapter adapter;
  private DataAdapterConfig config;

  public PhenotypeFinder(Query query, Map<String, Phenotype> phenotypes, DataAdapter adapter) {
    this.query = query;
    this.phenotypes = phenotypes;
    this.adapter = adapter;
    this.config = adapter.getConfig();
  }

  public ResultSet execute() {
    return executeCompositeSearches(executeSingleSearches());
  }

  private ResultSet executeCompositeSearches(ResultSet rs) {
    for (QueryCriterion cri : query.getCriteria()) {
      if (PhenotypeUtil.isComposite(phenotypes.get(cri.getSubjectId())))
        new CompositeSearch(query, cri, rs, phenotypes).execute();
    }
    return rs;
  }

  private ResultSet executeSingleSearches() {
    SingleQueryMan man = new SingleQueryMan(adapter);
    SubjectQueryMan sbjMan = new SubjectQueryMan(adapter);
    for (QueryCriterion cri : query.getCriteria()) {
      if (PhenotypeUtil.isSingle(phenotypes.get(cri.getSubjectId()))) {
        if (config.isAge(phenotypes.get(cri.getSubjectId()))) sbjMan.setAgeCriterion(cri);
        else if (config.isBirthdate(phenotypes.get(cri.getSubjectId())))
          sbjMan.setBirthdateCriterion(cri);
        else if (config.isSex(phenotypes.get(cri.getSubjectId()))) sbjMan.setSexCriterion(cri);
        else man.addCriterion(new SingleSearch(query, cri, adapter));
      }
    }

    for (QueryCriterion cri : query.getCriteria()) {
      if (PhenotypeUtil.isComposite(phenotypes.get(cri.getSubjectId()))) {
        for (String var :
            ExpressionUtil.getVariables(
                phenotypes.get(cri.getSubjectId()).getExpression(), phenotypes)) {
          Phenotype varPhe = phenotypes.get(var);
          if (PhenotypeUtil.isSingle(varPhe)) {
            if (config.isAge(varPhe)) sbjMan.addAgeVariable(varPhe);
            else if (config.isBirthdate(varPhe)) sbjMan.addBirthdateVariable(varPhe);
            else if (config.isSex(varPhe)) sbjMan.addSexVariable(varPhe);
            else man.addVariable(new SingleSearch(query, cri, varPhe, adapter));
          }
        }
      }
    }
    man.setSubjectQueryMan(sbjMan);
    return man.execute();
  }
}
