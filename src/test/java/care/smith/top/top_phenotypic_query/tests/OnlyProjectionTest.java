package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.DefaultSqlWriterDataSource;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.EncDao;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.PheDao;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.SbjDao;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OnlyProjectionTest {

  private static final DataAdapterConfig CONFIG =
      DataAdapterConfig.getInstanceFromResource("config/Default_Data_Source_SQL_Adapter.yml");

  private static final DefaultSqlWriterDataSource WRITER = new DefaultSqlWriterDataSource(CONFIG);

  private static Phenotype acutePancreatitis =
      new Phe(
              "acutePancreatitis",
              "http://fhir.de/CodeSystem/bfarm/icd-10-gm",
              "K85.11",
              "K85.10",
              "K85.00")
          .itemType(ItemType.CONDITION)
          .titleEn("acutePancreatitis")
          .bool()
          .get();

  private static Phenotype lipase =
      new Phe("lipase", "http://loinc.org", "3040-3", "15212-4", "10888-6")
          .itemType(ItemType.OBSERVATION)
          .titleEn("lipase")
          .number("U/L")
          .get();

  private static Phenotype lipaseHigh =
      new Phe("lipaseHigh").titleEn("lipaseHigh").restriction(lipase, Res.gt(180)).get();

  private static Phenotype algorithm =
      new Phe("algorithm")
          .titleEn("algorithm")
          .expression(Or.of(acutePancreatitis, lipaseHigh))
          .get();

  private static final String DATA_SOURCE_ID = "data_source_1";

  @BeforeAll
  static void beforeAll() {
    SbjDao sbj1 =
        SbjDao.get("1", "1932-07-02", "female")
            .encounter(
                EncDao.get("11")
                    .phenotype(PheDao.get("http://loinc.org", "3040-3", "2022-09-12", 179)))
            .encounter(
                EncDao.get("12")
                    .phenotype(PheDao.get("http://loinc.org", "15212-4", "2022-10-27", 182)));
    WRITER.insertSbj(DATA_SOURCE_ID, sbj1);

    SbjDao sbj2 =
        SbjDao.get("2", "1957-08-30", "male")
            .encounter(
                EncDao.get("21")
                    .phenotype(
                        PheDao.get(
                            "http://fhir.de/CodeSystem/bfarm/icd-10-gm",
                            "K85.10",
                            "2022-09-12",
                            true)))
            .encounter(
                EncDao.get("22")
                    .phenotype(
                        PheDao.get(
                            "http://fhir.de/CodeSystem/bfarm/icd-10-gm",
                            "K86.0",
                            "2022-10-27",
                            true)));
    WRITER.insertSbj(DATA_SOURCE_ID, sbj2);
  }

  @AfterAll
  static void afterAll() {
    WRITER.close();
  }

  @Test
  void test() throws InstantiationException {
    Que q = new Que(CONFIG, acutePancreatitis, lipase, lipaseHigh, algorithm).pro(algorithm);

    ResultSet rs = q.execute();

    assertEquals(4, rs.size());
    assertEquals(false, rs.getBooleanValue("11", "algorithm", null));
    assertEquals(true, rs.getBooleanValue("12", "algorithm", null));
    assertEquals(true, rs.getBooleanValue("21", "algorithm", null));
    assertEquals(false, rs.getBooleanValue("22", "algorithm", null));
  }
}
