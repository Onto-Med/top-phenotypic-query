package care.smith.top.top_phenotypic_query.adapter;

import care.smith.top.top_phenotypic_query.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public abstract class DataAdapter {

  protected DataAdapterConfig conf;

  protected DataAdapter(DataAdapterConfig conf) {
    this.conf = conf;
  }

  // call terminology server
  // fetch id attributes, column/table names
  // build atomic queries using DataAdapterConfig
  // execute queries and normalize/return ResultSet
  public abstract ResultSet execute(SubjectSearch search);

  public abstract ResultSet execute(SingleSearch search);
}
