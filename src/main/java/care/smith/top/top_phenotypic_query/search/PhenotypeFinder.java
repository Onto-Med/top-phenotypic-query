package care.smith.top.top_phenotypic_query.search;

import java.util.Map;

import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
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
      if (PhenotypeUtil.isComposite(cri.getSubject()))
        new CompositeSearch(query, cri, rs, phenotypes).execute();
    }
    return rs;
  }

  private ResultSet executeSingleSearches() {
    SingleQueryMan man = new SingleQueryMan(adapter);
    SubjectQueryMan sbjMan = new SubjectQueryMan(adapter);
    for (QueryCriterion cri : query.getCriteria()) {
      if (PhenotypeUtil.isSingle(cri.getSubject())) {
        if (config.isAge(cri.getSubject())) sbjMan.setAgeCriterion(cri);
        else if (config.isBirthdate(cri.getSubject())) sbjMan.setBirthdateCriterion(cri);
        else if (config.isSex(cri.getSubject())) sbjMan.setSexCriterion(cri);
        else man.addCriterion(new SingleSearch(query, cri, adapter));
      } else {
        for (String var : ExpressionUtil.getVariables(cri.getSubject().getExpression())) {
          if (config.isAge(cri.getSubject())) sbjMan.addAgeVariable(phenotypes.get(var));
          else if (config.isBirthdate(cri.getSubject()))
            sbjMan.addBirthdateVariable(phenotypes.get(var));
          else if (config.isSex(cri.getSubject())) sbjMan.addSexVariable(phenotypes.get(var));
          else man.addVariable(new SingleSearch(query, cri, phenotypes.get(var), adapter));
        }
      }
    }
    man.setSubjectQueryMan(sbjMan);
    return man.execute();
  }
}
