package care.smith.top.top_phenotypic_query.adapter;

import care.smith.top.top_phenotypic_query.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PropertySearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public abstract class DataAdapter {

  protected DataAdapterConfig conf;

  protected DataAdapter(DataAdapterConfig conf) {
    this.conf = conf;
  }

  public abstract ResultSet findSubjects(SubjectSearch search);

  public abstract ResultSet findProperties(PropertySearch search);
}
