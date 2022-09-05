package care.smith.top.top_phenotypic_query.adapter;

import java.sql.Connection;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.mapping.DataAdapterMapping;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.SQLUtil;

public class SimpleSQLAdapter extends DataAdapter {

  private Connection con;

  public SimpleSQLAdapter(DataAdapterConfig conf, DataAdapterMapping map) {
    super(conf, map);
    con = SQLUtil.connect(conf);
  }

  public SimpleSQLAdapter(String confFile, String mapFile) {
    super(confFile, mapFile);
    con = SQLUtil.connect(conf);
  }

  @Override
  public ResultSet execute(SubjectSearch search) {

    return null;
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    return null;
  }

  public java.sql.ResultSet execute(String query) {
    return SQLUtil.execute(con, query);
  }

  public void close() {
    SQLUtil.close(con);
  }

  public static void main(String[] args) {
    SimpleSQLAdapter a =
        new SimpleSQLAdapter(
            "test_files/Simple_SQL_Config.yaml", "test_files/Simple_SQL_Mapping.yaml");
    SQLUtil.print(a.execute("SELECT * FROM assessment1"));
  }
}
