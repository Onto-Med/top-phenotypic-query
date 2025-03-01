package care.smith.top.top_phenotypic_query.adapter.sql;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import java.sql.SQLException;

public class InterpolarAdapterDataSource extends SQLAdapter {

  public InterpolarAdapterDataSource(DataAdapterConfig config) throws SQLException {
    super(config, "Default_Interpolar_Adapter_Data_Source");
  }
}
