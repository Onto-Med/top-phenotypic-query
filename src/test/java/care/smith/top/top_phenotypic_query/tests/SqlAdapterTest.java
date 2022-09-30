package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import care.smith.top.backend.model.ExpressionFunction;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.sql.SQLAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;

public class SqlAdapterTest extends AbstractTest {
  static final String DB_URL = "jdbc:h2:mem:test0-db;INIT=RUNSCRIPT FROM 'classpath:schema.sql'";
  static final String DB_USER = "user";
  static final String DB_PASS = "password";

  Connection con;
  Map<String, Phenotype> phenotypes;

  @BeforeEach
  void setup() throws SQLException {
    con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    assertNotNull(con);
    assertTrue(con.isValid(0));

    Statement stmt = con.createStatement();
    java.sql.ResultSet rs = stmt.executeQuery("SELECT count(*) FROM subject");

    assertTrue(rs.next());
    assertEquals(3, rs.getInt(1));

    Phenotype height = getPhenotype("height", "http://loinc.org", "3137-7");
    Phenotype tall = getIntervalMin("tall", height, 200);

    phenotypes = getPhenotypeMap(height, tall);
  }

  @AfterEach
  void cleanup() throws SQLException {
    con.close();
    assertTrue(con.isClosed());
  }

  @Test
  void testAdapterConnection() {
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test.yml");
    assertNotNull(configFile);

    DataAdapterConfig config = DataAdapterConfig.getInstance(configFile.getPath());
    assertNotNull(config);

    SQLAdapter adapter = new SQLAdapter(config);
    assertNotNull(adapter);

    Phenotype tall = phenotypes.get("tall");
    assertNotNull(tall);

    QueryCriterion cri =
        new QueryCriterion()
            .exclusion(false)
            .defaultAggregationFunction(new ExpressionFunction().id("last").minArgumentNumber(1))
            .subject(tall);

    SingleSearch search = new SingleSearch(null, cri, adapter);
    assertNotNull(search);

    ResultSet rs = search.execute();
    assertNotNull(rs);
    assertEquals(1, rs.getSubjectIds().size());
  }
}
