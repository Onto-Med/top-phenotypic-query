package care.smith.top.top_phenotypic_query.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;

public class DB {

  private Connection con;

  public DB(DataAdapterConfig config) {
    try {
      this.con =
          DriverManager.getConnection(
              config.getConnectionAttribute("url"),
              config.getConnectionAttribute("user"),
              config.getConnectionAttribute("password"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void execute(Table tab) {
    try {
      System.out.println(tab.getCreateStatement());
      con.createStatement().execute(tab.getCreateStatement());
      System.out.println(tab.getInsertStatement());
      con.createStatement().execute(tab.getInsertStatement());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
