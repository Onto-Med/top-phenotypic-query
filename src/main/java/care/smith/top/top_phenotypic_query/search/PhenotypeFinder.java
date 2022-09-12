package care.smith.top.top_phenotypic_query.search;

import java.util.Map;

import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;

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
      EntityType type = cri.getSubject().getEntityType();
      if (type == EntityType.COMPOSITE_PHENOTYPE || type == EntityType.COMPOSITE_RESTRICTION)
        new CompositeSearch(query, cri, rs, phenotypes).execute();
    }
    return rs;
  }

  private ResultSet executeSingleSearches() {
    SingleQueryMan man = new SingleQueryMan(adapter);
    SubjectSearch sbj = new SubjectSearch(adapter);
    for (QueryCriterion cri : query.getCriteria()) {
      EntityType type = cri.getSubject().getEntityType();
      if (type == EntityType.SINGLE_PHENOTYPE || type == EntityType.SINGLE_RESTRICTION) {
        if (config.isAge(cri.getSubject())) sbj.setAgeCriterion(cri);
        else if (config.isBirthdate(cri.getSubject())) sbj.setBirthdateCriterion(cri);
        else if (config.isSex(cri.getSubject())) sbj.setSexCriterion(cri);
        else man.addCriterion(new SingleSearch(query, cri, adapter));
      } else {
        for (String var : ExpressionUtil.getVariables(cri.getSubject().getExpression())) {
          if (config.isAge(cri.getSubject())) sbj.addAgeVariable(phenotypes.get(var));
          else if (config.isBirthdate(cri.getSubject()))
            sbj.addBirthdateVariable(phenotypes.get(var));
          else if (config.isSex(cri.getSubject())) sbj.addSexVariable(phenotypes.get(var));
          else man.addVariable(new SingleSearch(query, cri, phenotypes.get(var), adapter));
        }
      }
    }
    man.setSubjectSearch(sbj);
    return man.execute();
  }
}
