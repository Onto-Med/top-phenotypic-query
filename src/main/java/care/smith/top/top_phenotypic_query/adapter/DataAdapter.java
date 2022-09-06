package care.smith.top.top_phenotypic_query.adapter;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.mapping.DataAdapterMapping;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public abstract class DataAdapter {

  protected DataAdapterConfig conf;
  protected DataAdapterMapping map;

  protected DataAdapter(DataAdapterConfig conf, DataAdapterMapping map) {
    this.conf = conf;
    this.map = map;
  }

  protected DataAdapter(String confFile, String mapFile) {
    this.conf = DataAdapterConfig.getInstance(confFile);
    this.map = DataAdapterMapping.getInstance(mapFile);
  }

  // call terminology server
  // fetch id attributes, column/table names
  // build atomic queries using DataAdapterConfig
  // execute queries and normalize/return ResultSet
  public abstract ResultSet execute(SubjectSearch search);

  public abstract ResultSet execute(SingleSearch search);

  public abstract ResultSet executeAllSubjectsQuery();
}
