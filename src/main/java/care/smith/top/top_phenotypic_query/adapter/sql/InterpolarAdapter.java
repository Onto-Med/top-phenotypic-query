package care.smith.top.top_phenotypic_query.adapter.sql;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import java.sql.SQLException;

public class InterpolarAdapter extends SQLAdapter {

  public InterpolarAdapter(DataAdapterConfig config) throws SQLException {
    super(config, "Default_Interpolar_Adapter");
  }
}
