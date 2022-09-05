package care.smith.top.top_phenotypic_query.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;

public class SQLUtil {

  public static Connection connect(DataAdapterConfig conf) {
    try {
      return DriverManager.getConnection(
          conf.getConnectionAttribute("url"),
          conf.getConnectionAttribute("user"),
          conf.getConnectionAttribute("password"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static ResultSet execute(Connection con, String query) {
    try {
      return con.createStatement().executeQuery(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void close(Connection con) {
    try {
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void print(ResultSet rs) {
    try {
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
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
