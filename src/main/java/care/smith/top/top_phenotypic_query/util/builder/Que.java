package care.smith.top.top_phenotypic_query.util.builder;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.data_adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.data_adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;

public class Que {

  private DataAdapter adapter;
  private DataAdapterConfig config;
  private Query query = new Query();
  private Entity[] entities;

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

  public Query getQuery() {
    return query;
  }

  public Que inc(Phenotype p) {
    return cri(true, p, null, null);
  }

  public Que inc(Phenotype p, DateTimeRestriction dtr) {
    return cri(true, p, dtr, null);
  }

  public Que inc(Phenotype p, String defAggFunc) {
    return cri(true, p, null, defAggFunc);
  }

  public Que inc(Phenotype p, DateTimeRestriction dtr, String defAggFunc) {
    return cri(true, p, dtr, defAggFunc);
  }

  public Que exc(Phenotype p) {
    return cri(false, p, null, null);
  }

  public Que exc(Phenotype p, DateTimeRestriction dtr) {
    return cri(false, p, dtr, null);
  }

  public Que exc(Phenotype p, String defAggFunc) {
    return cri(false, p, null, defAggFunc);
  }

  public Que exc(Phenotype p, DateTimeRestriction dtr, String defAggFunc) {
    return cri(false, p, dtr, defAggFunc);
  }

  private Que cri(boolean inc, Phenotype p, DateTimeRestriction dtr, String defAggFunc) {
    QueryCriterion cri = new QueryCriterion().inclusion(inc).subjectId(p.getId());
    if (defAggFunc != null) cri.defaultAggregationFunctionId(defAggFunc);
    if (dtr != null) cri.dateTimeRestriction(dtr);
    query.addCriteriaItem(cri);
    return this;
  }

  public PhenotypeFinder getFinder() {
    return new PhenotypeFinder(query, entities, adapter);
  }

  public ResultSet execute() {
    PhenotypeFinder pf = getFinder();
    ResultSet rs = pf.execute();
    adapter.close();
    return rs;
  }
}
