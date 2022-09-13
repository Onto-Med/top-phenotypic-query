package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class SqlAdapterTest {
  static final String DB_URL = "jdbc:h2:mem:test-db;INIT=RUNSCRIPT FROM 'classpath:schema.sql'";
  static final String DB_USER = "user";
  static final String DB_PASS = "password";

  Connection con;

  @BeforeEach
  void setup() throws SQLException {
    con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    assertNotNull(con);
    assertTrue(con.isValid(0));

    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT count(*) FROM subject");

    assertTrue(rs.next());
    assertEquals(rs.getInt(1), 0);
  }

  @AfterEach
  void cleanup() throws SQLException {
    con.close();
    assertTrue(con.isClosed());
  }

  @Test
  void testAdapterConnection() throws SQLException {
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test.yml");
    assertNotNull(configFile);

    DataAdapterConfig adapterConfig = DataAdapterConfig.getInstance(configFile.getPath());
    assertNotNull(adapterConfig);

    // TODO: init adapter and test query runs
  }
}
