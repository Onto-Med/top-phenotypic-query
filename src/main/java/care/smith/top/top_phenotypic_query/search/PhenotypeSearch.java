package care.smith.top.top_phenotypic_query.search;

import java.sql.SQLException;

import care.smith.top.model.Query;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public abstract class PhenotypeSearch {

  private Query query;

  protected PhenotypeSearch(Query query) {
    this.query = query;
  }

  public Query getQuery() {
    return query;
  }

  public abstract ResultSet execute() throws SQLException;
}
