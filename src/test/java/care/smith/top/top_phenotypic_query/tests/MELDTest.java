package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.DefaultSqlWriter;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import org.junit.jupiter.api.Test;

public class MELDTest {

  private static final String CONFIG = "config/Default_SQL_Adapter.yml";

  @Test
  public void test() throws InstantiationException {
    DataAdapterConfig conf = new Que(CONFIG).getConfig();
    DefaultSqlWriter writer = new DefaultSqlWriter(conf);
    writer
        .insertSbj(1, "2001-01-01", "male")
        .insertPhe("2005-01-01", "http://test.org", "a1", "kg", 123.123)
        .insertPhe("2005-01-01", "http://test.org", "a2", 456.456)
        .insertPhe("2005-01-02", "http://test.org", "b1", "abc");
    writer.printSbj();
    writer.printPhe();
    writer.close();
  }
}
