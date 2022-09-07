package care.smith.top.top_phenotypic_query.adapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.ItemType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class SQLConnection {

  private DataAdapterConfig conf;
  private Connection con;

  public SQLConnection(DataAdapterConfig conf) {
    this.conf = conf;
    try {
      this.con =
          DriverManager.getConnection(
              conf.getConnectionAttribute("url"),
              conf.getConnectionAttribute("user"),
              conf.getConnectionAttribute("password"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public ResultSet execute(
      String query,
      String type,
      Phenotype phe,
      Map<String, String> pheMap,
      DateTimeRestriction dtr) {
    ResultSet rs = new ResultSet();
    try {
      java.sql.ResultSet sqlRS = con.createStatement().executeQuery(query);
      PhenotypeOutput out = conf.getPhenotypeQuery(type).getOutput();
      String sbjCol = out.getSubject();
      String pheCol = out.getPhenotype(pheMap);
      String dateCol = out.getDate(pheMap);
      DataType datatype = phe.getDataType();

      while (sqlRS.next()) {
        String sbj = sqlRS.getString(sbjCol);
        LocalDateTime date = sqlRS.getTimestamp(dateCol).toLocalDateTime();
        Value val = null;
        if (datatype == DataType.BOOLEAN) val = new BooleanValue(sqlRS.getBoolean(pheCol), date);
        else if (datatype == DataType.DATE_TIME)
          val = new DateTimeValue(sqlRS.getTimestamp(pheCol).toLocalDateTime(), date);
        else if (datatype == DataType.NUMBER)
          val = new DecimalValue(sqlRS.getBigDecimal(pheCol), date);
        else val = new StringValue(sqlRS.getString(pheCol), date);
        if (val != null) rs.addValue(sbj, phe.getId(), dtr, val);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  public void close() {
    try {
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void print(java.sql.ResultSet rs) {
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

  public static void main(String[] args) {
    DataAdapterConfig conf = DataAdapterConfig.getInstance("test_files/Simple_SQL_Config.yaml");
    Phenotype phe = new Phenotype().dataType(DataType.NUMBER).itemType(ItemType.OBSERVATION);
    phe.setId("weight");
    String type = "Assessment1";
    Map<String, String> map = ImmutableMap.of("phenotype", "weight");
    String query = conf.getPhenotypeQuery(type).getQueryBuilder(map).baseQuery().build();
    System.out.println(query);

    SQLConnection sql = new SQLConnection(conf);
    System.out.println(sql.execute(query, type, phe, map, null));
  }
}
