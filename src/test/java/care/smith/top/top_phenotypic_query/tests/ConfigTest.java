package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;

public class ConfigTest {

  @Test
  public void testSubjectQuery() {
    DataAdapterConfig conf = DataAdapterConfig.getInstance("test_files/Simple_SQL_Config.yaml");

    String expected =
        "SELECT subject_id, birth_date, sex FROM subject\n"
            + "WHERE TRUE\n"
            + "AND sex IN ('m', 'w')\n"
            + "AND birth_date >= '1990-01-01'::date\n"
            + "AND birth_date < '2000-01-01'::date";

    String actual =
        conf.getSubjectQuery()
            .getQueryBuilder()
            .baseQuery()
            .sexRange("'m', 'w'")
            .birthdateRangeLimit(">=", "1990-01-01")
            .birthdateRangeLimit("<", "2000-01-01")
            .build();

    assertEquals(expected, actual);
  }

  @Test
  public void testPhenotypeQuery() {
    DataAdapterConfig conf = DataAdapterConfig.getInstance("test_files/Simple_SQL_Config.yaml");

    System.out.println(conf);

    String expected =
        "SELECT subject_id, created_at, weight FROM assessment1\n"
            + "WHERE weight IS NOT NULL\n"
            + "AND weight >= 100\n"
            + "AND weight < 200\n"
            + "AND created_at >= '1990-01-01'::date\n"
            + "AND created_at < '2000-01-01'::date\n"
            + "AND subject_id IN ('1', '2', '3')";

    Map<String, String> mapping = ImmutableMap.of("phenotype", "weight");

    String actual =
        conf.getPhenotypeQuery("Assessment1")
            .getQueryBuilder(mapping)
            .baseQuery()
            .valueRangeLimit(">=", "100")
            .valueRangeLimit("<", "200")
            .dateRangeLimit(">=", "1990-01-01")
            .dateRangeLimit("<", "2000-01-01")
            .subjects("'1', '2', '3'")
            .build();

    assertEquals(expected, actual);
  }
}
