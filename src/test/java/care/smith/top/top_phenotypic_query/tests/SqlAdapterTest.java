package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class SqlAdapterTest {

  static final String ADAPTER_CONFIG_FILE = "config/SQL_Adapter_Test.yml";

  @Test
  void testAdapterConnection() throws SQLException {
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource(ADAPTER_CONFIG_FILE);
    assertNotNull(configFile);

    DataAdapterConfig adapterConfig = DataAdapterConfig.getInstance(configFile.getPath());
    assertNotNull(adapterConfig);

    String url = adapterConfig.getConnectionAttribute("url");
    String user = adapterConfig.getConnectionAttribute("user");
    String password = adapterConfig.getConnectionAttribute("password");

    assertNotNull(url);
    assertNotNull(user);
    assertNotNull(password);

    Connection con = DriverManager.getConnection(url, user, password);
    assertNotNull(con);
    assertTrue(con.isValid(0));

    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT count(*) FROM subject");

    assertTrue(rs.next());
    assertEquals(rs.getInt(1), 0);
  }
}
