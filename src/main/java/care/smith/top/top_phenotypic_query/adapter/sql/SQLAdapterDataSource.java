package care.smith.top.top_phenotypic_query.adapter.sql;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import java.sql.SQLException;

public class SQLAdapterDataSource extends SQLAdapter {

  public SQLAdapterDataSource(DataAdapterConfig config) throws SQLException {
    super(config, "Default_SQL_Adapter_Data_Source");
  }
}
