package care.smith.top.top_phenotypic_query.util.builder;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;

public class Que {

  private DataAdapter adapter;
  private DataAdapterConfig config;
  private Query query = new Query();
  private Entity[] entities;
  private String defAggFunc = "Last";

  public Que(String configFilePath, Entity... entities) throws InstantiationException {
    this.config = getConfig(configFilePath);
    this.adapter = DataAdapter.getInstance(config);
    this.entities = entities;
  }

  public static DataAdapter getAdapter(String configFilePath) throws InstantiationException {
    return DataAdapter.getInstance(getConfig(configFilePath));
  }

  public static DataAdapterConfig getConfig(String configFilePath) {
    return DataAdapterConfig.getInstance(getPath(configFilePath));
  }

  private static String getPath(String configFilePath) {
    return Thread.currentThread().getContextClassLoader().getResource(configFilePath).getPath();
  }

  public DataAdapter getAdapter() {
    return adapter;
  }

  public DataAdapterConfig getConfig() {
    return config;
  }

  public Que defAggFunc(String defaultAggregateFunctionId) {
    this.defAggFunc = defaultAggregateFunctionId;
    return this;
  }

  public Que inc(Phenotype p) {
    return cri(p, null, true);
  }

  public Que exc(Phenotype p) {
    return cri(p, null, false);
  }

  public Que inc(Phenotype p, DateTimeRestriction dtr) {
    return cri(p, dtr, true);
  }

  public Que exc(Phenotype p, DateTimeRestriction dtr) {
    return cri(p, dtr, false);
  }

  private Que cri(Phenotype p, DateTimeRestriction dtr, boolean inc) {
    QueryCriterion cri =
        new QueryCriterion()
            .inclusion(inc)
            .defaultAggregationFunctionId(defAggFunc)
            .subjectId(p.getId());
    if (dtr != null) cri.dateTimeRestriction(dtr);
    query.addCriteriaItem(cri);
    return this;
  }

  public ResultSet execute() {
    PhenotypeFinder pf = new PhenotypeFinder(query, entities, adapter);
    ResultSet rs = pf.execute();
    adapter.close();
    return rs;
  }
}
