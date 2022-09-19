package care.smith.top.top_phenotypic_query.adapter;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public abstract class DataAdapter {

  protected DataAdapterConfig config;

  protected DataAdapter(DataAdapterConfig config) {
    this.config = config;
  }

  protected DataAdapter(String configFile) {
    this.config = DataAdapterConfig.getInstance(configFile);
  }

  public DataAdapterConfig getConfig() {
    return config;
  }

  // call terminology server
  // fetch id attributes, column/table names
  // build atomic queries using DataAdapterConfig
  // execute queries and normalize/return ResultSet
  public abstract ResultSet execute(SubjectSearch search);

  public abstract ResultSet execute(SingleSearch search);

  public abstract ResultSet executeAllSubjectsQuery();

  public abstract DataAdapterFormat getFormat();

  public abstract void close();
}
