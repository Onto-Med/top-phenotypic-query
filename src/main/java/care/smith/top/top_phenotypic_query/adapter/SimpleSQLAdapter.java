package care.smith.top.top_phenotypic_query.adapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public class SimpleSQLAdapter extends DataAdapter {

  private Connection con;

  public SimpleSQLAdapter(DataAdapterConfig conf) {
    super(conf);
    connect();
  }

  public SimpleSQLAdapter(String confFile) {
    super(confFile);
    connect();
  }

  private void connect() {
    try {
      con =
          DriverManager.getConnection(
              conf.getConnectionAttribute("url"),
              conf.getConnectionAttribute("user"),
              conf.getConnectionAttribute("password"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public ResultSet execute(SubjectSearch search) {

    return null;
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    return null;
  }

  private void executeStatement() {
    Statement st;
    try {
      st = con.createStatement();
      java.sql.ResultSet rs = st.executeQuery("SELECT * FROM assessment1");
      printResultSet(rs);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static final void printResultSet(java.sql.ResultSet rs) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnsNumber = rsmd.getColumnCount();
    while (rs.next()) {
      for (int i = 1; i <= columnsNumber; i++) {
        if (i > 1) System.out.print(" | ");
        System.out.print(rs.getString(i));
      }
      System.out.println("");
    }
    for (int i = 1; i <= columnsNumber; i++) {
      if (i > 1) System.out.print(" | ");
      System.out.print(rsmd.getColumnName(i));
    }
    System.out.println("");
  }

  public void close() {
    try {
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new SimpleSQLAdapter("test_files/Simple_SQL_Config.yaml").executeStatement();
  }
}
