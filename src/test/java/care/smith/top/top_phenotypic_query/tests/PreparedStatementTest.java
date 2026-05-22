package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.sql.SQLAdapter;
import care.smith.top.top_phenotypic_query.adapter.sql.SQLAdapterSettings;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreparedStatementTest extends AbstractTest {
  
  private DataAdapter adapter;
  
  @BeforeAll
  void setup() throws InstantiationException {
    adapter = DataAdapter.getInstanceFromResource("config/SQL_Adapter_Test6.yml");
    assertNotNull(adapter);
  }
  
  @Test
  public void test1() throws SQLException {
    SubjectSearch search =
            new SubjectSearch(Que.get(), femaleOrMale, birthDateYoung, null, adapter);
    SQLAdapterSettings settings = SQLAdapterSettings.get();
    
    String pqExpected =
            "SELECT subject_id, birth_date, sex FROM subject\n" +
                    "WHERE TRUE\n" +
                    "AND sex IN (:sex0,:sex1)\n" +
                    "AND birth_date >= :birthdate0";
    String pqActual = settings.createSubjectPreparedQuery(search);
    assertEquals(pqExpected, pqActual);
    
    String psExpected =
            "SELECT subject_id, birth_date, sex FROM subject\n" +
                    "WHERE TRUE\n" +
                    "AND sex IN (:sex0,:sex1)\n" +
                    "AND birth_date >= :birthdate0, bindings={named:{sex0:female,sex1:male,birthdate0:2000-01-01 00:00:00.0}}]";
    String psActual =
            settings
                    .getSubjectSqlQuery(pqActual, ((SQLAdapter) adapter).getConnection(), search)
                    .toString();
    assertEquals(psExpected, psActual.substring(psActual.indexOf("SELECT")));
  }
  
  @Test
  public void test2() throws InstantiationException, SQLException {
    QueryCriterion qc = (QueryCriterion) new QueryCriterion().dateTimeRestriction(getDTR(2000));
    SingleSearch search = new SingleSearch(Que.get(), qc, heavy, adapter);
    SQLAdapterSettings settings = SQLAdapterSettings.get();
    
    String pqExpected =
            "SELECT subject_id, created_at, weight FROM assessment1\n" +
                    "WHERE weight IS NOT NULL\n" +
                    "AND weight >= :restriction0\n" +
                    "AND weight < :restriction1\n" +
                    "AND created_at >= :date0\n" +
                    "AND created_at < :date1";
    String pqActual = settings.createSinglePreparedQuery(search);
    assertEquals(pqExpected, pqActual);
    
    String psExpected =
            "SELECT subject_id, created_at, weight FROM assessment1\n" +
                    "WHERE weight IS NOT NULL\n" +
                    "AND weight >= :restriction0\n" +
                    "AND weight < :restriction1\n" +
                    "AND created_at >= :date0\n" +
                    "AND created_at < :date1, bindings={named:{restriction0:100,restriction1:500,date0:2000-01-01 00:00:00.0,date1:2001-01-01 00:00:00.0}}]";
    String psActual =
            settings
                    .getSingleSqlQuery(pqActual, ((SQLAdapter) adapter).getConnection(), search)
                    .toString();
    assertEquals(psExpected, psActual.substring(psActual.indexOf("SELECT")));
  }
}
