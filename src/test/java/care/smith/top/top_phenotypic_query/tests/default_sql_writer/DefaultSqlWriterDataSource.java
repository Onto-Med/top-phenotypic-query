package care.smith.top.top_phenotypic_query.tests.default_sql_writer;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSqlWriterDataSource {

  private Connection con;

  private static final String CREATE_SBJ =
      "CREATE TABLE subject (\r\n"
          + "    data_source_id	text NOT NULL,\r\n"
          + "    subject_id		text NOT NULL,\r\n"
          + "    birth_date		timestamp,\r\n"
          + "    sex        	text,\r\n"
          + "    PRIMARY KEY (data_source_id, subject_id)\r\n"
          + ")";

  private static final String CREATE_ENC =
      "CREATE TABLE encounter (\r\n"
          + "    data_source_id	 text NOT NULL,\r\n"
          + "    subject_id      text NOT NULL,\r\n"
          + "    encounter_id    text NOT NULL,\r\n"
          + "    type		     text,\r\n"
          + "    start_date_time timestamp,\r\n"
          + "    end_date_time   timestamp,\r\n"
          + "    PRIMARY KEY (data_source_id, encounter_id)\r\n"
          + ")";

  private static final String CREATE_PHE =
      "CREATE TABLE subject_resource (\r\n"
          + "    data_source_id	 	 text NOT NULL,\r\n"
          + "    subject_id      	 text,\r\n"
          + "    encounter_id    	 text,\r\n"
          + "    subject_resource_id text NOT NULL,\r\n"
          + "    code_system     	 text NOT NULL,\r\n"
          + "    code            	 text NOT NULL,\r\n"
          + "    date_time    	     timestamp,\r\n"
          + "    start_date_time     timestamp,\r\n"
          + "    end_date_time  	 timestamp,\r\n"
          + "    unit            	 text,\r\n"
          + "    number_value    	 numeric(20,3),\r\n"
          + "    text_value      	 text,\r\n"
          + "    date_time_value 	 timestamp,\r\n"
          + "    boolean_value   	 boolean,\r\n"
          + "    PRIMARY KEY (data_source_id, subject_resource_id)\r\n"
          + ")";

  private SqlTablePrinter tablePrinter;

  private Logger log = LoggerFactory.getLogger(DefaultSqlWriterDataSource.class);

  public DefaultSqlWriterDataSource(DataAdapterConfig config) {
    try {
      con =
          DriverManager.getConnection(
              config.getConnectionAttribute("url"),
              config.getConnectionAttribute("user"),
              config.getConnectionAttribute("password"));
      tablePrinter = new SqlTablePrinter(con);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    execute("DROP ALL OBJECTS");
    execute(CREATE_SBJ);
    execute(CREATE_ENC);
    execute(CREATE_PHE);
  }

  public void insertSbj(String dataSourceId, SbjDao sbj) {
    execute(
        "INSERT INTO subject VALUES ("
            + String.join(
                ", ",
                quote(dataSourceId),
                quote(sbj.getSubjectId()),
                quote(sbj.getBirthDate()),
                quote(sbj.getSex()))
            + ")");

    for (EncDao enc : sbj.getEncounters()) {
      insertEnc(dataSourceId, sbj.getSubjectId(), enc);
      for (PheDao phe : enc.getPhenotypes())
        insertPhe(dataSourceId, sbj.getSubjectId(), enc.getEncounterId(), phe);
    }
  }

  private void insertEnc(String dataSourceId, String subjectId, EncDao enc) {
    execute(
        "INSERT INTO encounter VALUES ("
            + String.join(
                ", ",
                quote(dataSourceId),
                quote(subjectId),
                quote(enc.getEncounterId()),
                quote(enc.getType()),
                quote(enc.getStartDateTime()),
                quote(enc.getEndDateTime()))
            + ")");
  }

  private void insertPhe(String dataSourceId, String subjectId, String encounterId, PheDao phe) {
    if (phe.getPhenotypeId() == null) phe.phenotypeId(UUID.randomUUID().toString());
    if (phe.getDate() == null && phe.getStartDate() == null && phe.getEndDate() == null)
      phe.date(DateUtil.format(LocalDateTime.now()));

    execute(
        "INSERT INTO subject_resource VALUES ("
            + String.join(
                ", ",
                quote(dataSourceId),
                quote(subjectId),
                quote(encounterId),
                quote(phe.getPhenotypeId()),
                quote(phe.getCodeSystem()),
                quote(phe.getCode()),
                quote(phe.getDate()),
                quote(phe.getStartDate()),
                quote(phe.getEndDate()),
                quote(phe.getUnit()),
                toStr(phe.getNumberValue()),
                quote(phe.getTextValue()),
                quote(phe.getDateValue()),
                toStr(phe.getBooleanValue()))
            + ")");
  }

  private String quote(String s) {
    if (s == null) return "null";
    return "'" + s + "'";
  }

  private String toStr(Object o) {
    if (o == null) return "null";
    return o.toString();
  }

  private void execute(String sql) {
    log.debug("execute sql statement:{}{}", System.lineSeparator(), sql);
    try {
      Statement stmt = con.createStatement();
      stmt.execute(sql);
      stmt.close();
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

  public void printSbj() {
    tablePrinter.print("subject");
  }

  public void printEnc() {
    tablePrinter.print("encounter");
  }

  public void printPhe() {
    tablePrinter.print("subject_resource");
  }
}
