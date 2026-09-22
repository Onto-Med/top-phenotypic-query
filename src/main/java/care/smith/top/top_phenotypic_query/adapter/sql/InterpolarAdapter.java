package care.smith.top.top_phenotypic_query.adapter.sql;

import module java.sql;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;

public class InterpolarAdapter extends SQLAdapter {

  public InterpolarAdapter(DataAdapterConfig config) throws SQLException {
    super(config, "Default_Interpolar_Adapter");
  }
}
