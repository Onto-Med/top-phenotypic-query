package care.smith.top.top_phenotypic_query.adapter;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;

import care.smith.top.backend.model.Code;
import care.smith.top.backend.model.CodeSystem;
import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.ItemType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public class SQLAdapter extends DataAdapter {

  private Connection con;

  public SQLAdapter(DataAdapterConfig config) {
    super(config);
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

  @Override
  public ResultSet execute(SingleSearch search) {
    ResultSet rs = new ResultSet();
    try {
      java.sql.ResultSet sqlRS = con.createStatement().executeQuery(search.getQueryString());
      PhenotypeOutput out = search.getOutput();
      String sbjCol = out.getSubject();
      String pheCol = out.getPhenotype();
      String dateCol = out.getDate();
      Phenotype phe = search.getPhenotype();
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
        if (val != null) rs.addValue(sbj, phe.getId(), search.getDateTimeRestriction(), val);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  @Override
  public ResultSet execute(SubjectSearch search) {
    return null;
  }

  @Override
  public ResultSet executeAllSubjectsQuery() {
    return null;
  }

  @Override
  public DataAdapterFormat getFormat() {
    return SQLAdapterFormat.get();
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

  public static void main(String[] args) throws URISyntaxException {
    DataAdapterConfig conf = DataAdapterConfig.getInstance("test_files/Simple_SQL_Config.yaml");
    Phenotype phe = new Phenotype().dataType(DataType.NUMBER).itemType(ItemType.OBSERVATION);
    phe.setId("weight");
    phe.setEntityType(EntityType.SINGLE_PHENOTYPE);
    phe.addCodesItem(
        new Code().code("3141-9").codeSystem(new CodeSystem().uri(new URI("http://loinc.org"))));
    SQLAdapter sql = new SQLAdapter(conf);
    SingleSearch search = new SingleSearch(null, new QueryCriterion().subject(phe), sql);
    System.out.println(sql.execute(search));
  }
}
