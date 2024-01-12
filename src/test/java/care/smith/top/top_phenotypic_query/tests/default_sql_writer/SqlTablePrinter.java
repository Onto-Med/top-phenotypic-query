package care.smith.top.top_phenotypic_query.tests.default_sql_writer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class SqlTablePrinter {

  private Connection con;

  public SqlTablePrinter(Connection con) {
    this.con = con;
  }

  protected void print(String table) {
    try {
      Statement st =
          con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      ResultSet rs = st.executeQuery("SELECT * FROM " + table);
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnsNumber = rsmd.getColumnCount();
      int[] columnsWidths = new int[columnsNumber];
      Arrays.fill(columnsWidths, 0);

      for (int i = 0; i < columnsNumber; i++) {
        int length = rsmd.getColumnName(i + 1).length();
        if (length > columnsWidths[i]) columnsWidths[i] = length;
      }

      while (rs.next()) {
        for (int i = 0; i < columnsNumber; i++) {
          String value = rs.getString(i + 1);
          if (value == null) continue;
          int length = value.length();
          if (length > columnsWidths[i]) columnsWidths[i] = length;
        }
      }

      String borderLine = getBorderLine(columnsNumber, columnsWidths);

      System.out.println(borderLine);
      printHeader(columnsNumber, columnsWidths, rsmd, borderLine);
      rs.first();
      do printRecord(columnsNumber, columnsWidths, rs, borderLine);
      while (rs.next());
      printHeader(columnsNumber, columnsWidths, rsmd, borderLine);
      System.out.println();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private String getBorderLine(int columnsNumber, int[] columnsWidths) {
    StringBuffer bl = new StringBuffer("+");
    for (int i = 0; i < columnsNumber; i++) bl.append("-".repeat(columnsWidths[i])).append("+");
    return bl.toString();
  }

  private void printHeader(
      int columnsNumber, int[] columnsWidths, ResultSetMetaData rsmd, String borderLine)
      throws SQLException {
    for (int i = 0; i < columnsNumber; i++) printValue(rsmd.getColumnName(i + 1), columnsWidths[i]);
    System.out.println("|");
    System.out.println(borderLine);
  }

  private void printRecord(int columnsNumber, int[] columnsWidths, ResultSet rs, String borderLine)
      throws SQLException {
    for (int i = 0; i < columnsNumber; i++) {
      String value = rs.getString(i + 1);
      if (value == null) value = "";
      printValue(value, columnsWidths[i]);
    }
    System.out.println("|");
    System.out.println(borderLine);
  }

  private void printValue(String value, int columnWidth) {
    System.out.print("|" + value + " ".repeat(columnWidth - value.length()));
  }
}
