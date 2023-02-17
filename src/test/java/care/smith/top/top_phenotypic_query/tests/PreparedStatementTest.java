package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.data_adapter.sql.SQLAdapter;
import care.smith.top.top_phenotypic_query.data_adapter.sql.SQLAdapterSettings;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreparedStatementTest extends AbstractTest {

  private SQLAdapter adapter;

  @BeforeAll
  void setup() throws SQLException {
    URL configFile =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test6.yml");
    assertNotNull(configFile);
    adapter = new SQLAdapter(configFile.getPath());
    assertNotNull(adapter);
  }

  @Test
  public void test1() throws SQLException {
    SubjectSearch search = new SubjectSearch(null, femaleOrMale, birthDateYoung, null, adapter);
    SQLAdapterSettings settings = SQLAdapterSettings.get();

    String pqExpected =
        "SELECT subject_id, birth_date, sex FROM subject\n"
            + "WHERE TRUE\n"
            + "AND sex IN (?,?)\n"
            + "AND birth_date >= ?";
    String pqActual = settings.createSubjectPreparedQuery(search);
    assertEquals(pqExpected, pqActual);

    String psExpected =
        "SELECT subject_id, birth_date, sex FROM subject\n"
            + "WHERE TRUE\n"
            + "AND sex IN (?,?)\n"
            + "AND birth_date >= ? {1: 'female', 2: 'male', 3: TIMESTAMP '2000-01-01 00:00:00'}";
    String psActual =
        settings.getSubjectPreparedStatement(pqActual, adapter.getConnection(), search).toString();
    assertEquals(psExpected, psActual.substring(psActual.indexOf("SELECT")));
  }

  @Test
  public void test2() throws InstantiationException, SQLException {
    QueryCriterion qc = new QueryCriterion().dateTimeRestriction(getDTR(2000));
    SingleSearch search = new SingleSearch(null, qc, heavy, adapter, false);
    SQLAdapterSettings settings = SQLAdapterSettings.get();

    String pqExpected =
        "SELECT subject_id, created_at, weight FROM assessment1\n"
            + "WHERE weight IS NOT NULL\n"
            + "AND weight >= ?\n"
            + "AND weight < ?\n"
            + "AND created_at >= ?\n"
            + "AND created_at < ?";
    String pqActual = settings.createSinglePreparedQuery(search);
    assertEquals(pqExpected, pqActual);

    String psExpected =
        "SELECT subject_id, created_at, weight FROM assessment1\n"
            + "WHERE weight IS NOT NULL\n"
            + "AND weight >= ?\n"
            + "AND weight < ?\n"
            + "AND created_at >= ?\n"
            + "AND created_at < ? {1: CAST(100 AS NUMERIC(3)), 2: CAST(500 AS NUMERIC(3)), 3: TIMESTAMP '2000-01-01 00:00:00', 4: TIMESTAMP '2001-01-01 00:00:00'}";
    String psActual =
        settings.getSinglePreparedStatement(pqActual, adapter.getConnection(), search).toString();
    assertEquals(psExpected, psActual.substring(psActual.indexOf("SELECT")));
  }
}
