package care.smith.top.top_phenotypic_query.tests.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import module java.base;
import module java.sql;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InterpolarObservationTest {
  private Connection connection;

  @BeforeEach
  void createDatabase() throws Exception {
    connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID());
    try (Statement statement = connection.createStatement()) {
      statement.execute("CREATE SCHEMA db2dataprocessor_out");
      statement.execute(
"""
          CREATE TABLE db2dataprocessor_out.v_observation_last_version (
            obs_id VARCHAR, obs_encounter_calculated_ref VARCHAR,
            obs_effectivedatetime TIMESTAMP, analysis_value DOUBLE PRECISION,
            analysis_loinc_code VARCHAR, analysis_unit VARCHAR,
      		obs_valuequantity_code varchar NULL, obs_valuequantity_value float8 NULL,
            analysis_value_status VARCHAR,
            obs_referencerange_low_value DOUBLE PRECISION,
            obs_referencerange_low_code VARCHAR,
            obs_referencerange_high_value DOUBLE PRECISION,
            obs_referencerange_high_code VARCHAR,
            obs_referencerange_type_code VARCHAR)
""");
      statement.execute(
          """
          CREATE TABLE db2dataprocessor_out.v_encounter_last_version (
            enc_id VARCHAR, enc_period_start TIMESTAMP, enc_age_at_admission INTEGER)
          """);
      statement.execute(
          """
          INSERT INTO db2dataprocessor_out.v_encounter_last_version VALUES
          ('case', '2026-01-01 00:00:00', 30),
          ('old-case', '2001-01-01 00:00:00', 42),
          ('new-case', '2026-01-01 00:00:00', 61)
          """);
    }
  }

  @AfterEach
  void closeDatabase() throws Exception {
    connection.close();
  }

  private void insert(
      String id,
      String encounter,
      String date,
      Double value,
      String code,
      String unit,
      String status)
      throws Exception {
    insert(id, encounter, date, value, code, unit, status, null, null, null, null, null);
  }

  private void insert(
      String id,
      String encounter,
      String date,
      Double value,
      String code,
      String unit,
      String status,
      Double refLowVal,
      String refLowCode,
      Double refHighVal,
      String refHighCode,
      String refTypeCode)
      throws Exception {
    try (PreparedStatement statement =
        connection.prepareStatement(
            """
            INSERT INTO db2dataprocessor_out.v_observation_last_version (
              obs_id, obs_encounter_calculated_ref, obs_effectivedatetime,
              analysis_value, analysis_loinc_code, analysis_unit, analysis_value_status,
              obs_referencerange_low_value, obs_referencerange_low_code,
              obs_referencerange_high_value, obs_referencerange_high_code,
              obs_referencerange_type_code
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """)) {
      statement.setString(1, id);
      statement.setString(2, "Encounter/" + encounter);
      statement.setTimestamp(3, java.sql.Timestamp.valueOf(date));
      statement.setObject(4, value);
      statement.setString(5, code);
      statement.setString(6, unit);
      statement.setString(7, status);
      statement.setObject(8, refLowVal);
      statement.setString(9, refLowCode);
      statement.setObject(10, refHighVal);
      statement.setString(11, refHighCode);
      statement.setString(12, refTypeCode);
      statement.executeUpdate();
    }
  }

  private List<String> query(String code) throws Exception {
    String query =
        DataAdapterConfig.getInstanceFromResource(
                "default_adapter_configuration/Default_Interpolar_Adapter.yml")
            .getPhenotypeQuery("observation")
            .getBaseQuery()
            .replace("{codes}", "?");
    List<String> rows = new ArrayList<>();
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, "http://loinc.org|" + code);
      try (ResultSet result = statement.executeQuery()) {
        while (result.next()) {
          rows.add(result.getString("enc_id") + ":" + result.getDouble("va"));
        }
      }
    }
    Collections.sort(rows);
    return rows;
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "converted,1",
        "already_reference_unit,1",
        "missing_unit,0",
        "unsupported_unit,0",
        "conversion_failed,0",
        "unknown,0",
        "NULL,0",
        "'',0"
      },
      nullValues = "NULL")
  void acceptsOnlyConfirmedReferenceValues(String status, int expected) throws Exception {
    insert("a", "case", "2026-01-01 00:00:00", 90.0, "8480-6", "mm[Hg]", status);
    assertEquals(expected, query("8480-6").size());
  }

  @Test
  void rejectsMissingNumericValuesAndOtherCodes() throws Exception {
    insert("a", "case", "2026-01-01 00:00:00", null, "8480-6", "mm[Hg]", "converted");
    insert("b", "case", "2026-01-01 00:00:00", 90.0, "other", "mm[Hg]", "converted");
    assertEquals(List.of(), query("8480-6"));
  }

  @ParameterizedTest
  @CsvSource(
      value = {"a,1", "NULL,2", "'',2"},
      nullValues = "NULL")
  void collapsesRepeatedMeasurementsOnlyWithKnownIdentity(String id, int expected)
      throws Exception {
    for (int i = 0; i < 2; i++) {
      insert(id, "case", "2026-01-01 00:00:00", 90.0, "8480-6", "mm[Hg]", "converted");
    }
    assertEquals(expected, query("8480-6").size());
  }

  @Test
  void retainsDifferentResourcesAndMeasurementFieldsAcrossTheDatabase() throws Exception {
    insert("a", "old-case", "2001-01-01 00:00:00", 90.0, "8480-6", "mm[Hg]", "converted");
    insert("b", "old-case", "2001-01-01 00:00:00", 90.0, "8480-6", "mm[Hg]", "converted");
    insert("a", "new-case", "2026-01-01 00:00:00", 90.0, "8480-6", "mm[Hg]", "converted");
    insert("a", "old-case", "2001-01-02 00:00:00", 90.0, "8480-6", "mm[Hg]", "converted");
    insert("a", "old-case", "2001-01-01 00:00:00", 95.0, "8480-6", "mm[Hg]", "converted");
    insert("a", "old-case", "2001-01-01 00:00:00", 90.0, "8480-6", "other-unit", "converted");
    assertEquals(
        List.of(
            "new-case:90.0",
            "old-case:90.0",
            "old-case:90.0",
            "old-case:90.0",
            "old-case:90.0",
            "old-case:95.0"),
        query("8480-6"));
  }

  @Test
  void preservesAdmissionAge() throws Exception {
    try (Statement statement = connection.createStatement()) {
      statement.execute("TRUNCATE TABLE db2dataprocessor_out.v_encounter_last_version");
      statement.execute(
          """
          INSERT INTO db2dataprocessor_out.v_encounter_last_version VALUES
          ('old-case', '2001-01-01 00:00:00', 42), ('new-case', '2026-01-01 00:00:00', 61),
          ('missing-age', '2026-01-01 00:00:00', NULL)
          """);
    }
    assertEquals(List.of("new-case:61.0", "old-case:42.0"), query("29553-5"));
  }

  @Test
  void selectsSingleNormalOrNullReferenceRangeAndKeepsObservationWithoutMatch() throws Exception {
    // 1. Observation ‘a’ has ‘treatment’ and ‘normal’ -> return only once with 'normal'
    insert(
        "a",
        "case",
        "2026-01-01 00:00:00",
        90.0,
        "8480-6",
        "mm[Hg]",
        "converted",
        10.0,
        "mm[Hg]",
        20.0,
        "mm[Hg]",
        "treatment");
    insert(
        "a",
        "case",
        "2026-01-01 00:00:00",
        90.0,
        "8480-6",
        "mm[Hg]",
        "converted",
        60.0,
        "mm[Hg]",
        80.0,
        "mm[Hg]",
        "normal");

    // 2. Observation ‘b’ has only ‘treatment’ -> Observation is retained, reference range is NULL
    insert(
        "b",
        "old-case",
        "2001-01-01 00:00:00",
        110.0,
        "8480-6",
        "mm[Hg]",
        "converted",
        10.0,
        "mm[Hg]",
        20.0,
        "mm[Hg]",
        "treatment");

    String sql =
        DataAdapterConfig.getInstanceFromResource(
                "default_adapter_configuration/Default_Interpolar_Adapter.yml")
            .getPhenotypeQuery("observation")
            .getBaseQuery()
            .replace("{codes}", "?");

    Map<String, List<Double>> results = new HashMap<>();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, "http://loinc.org|8480-6");
      try (ResultSet rs = statement.executeQuery()) {
        while (rs.next()) {
          String enc = rs.getString("enc_id");
          Double low = (Double) rs.getObject("ref_low_val");
          Double high = (Double) rs.getObject("ref_high_val");
          results.put(enc, Arrays.asList(rs.getDouble("va"), low, high));
        }
      }
    }

    assertEquals(2, results.size());
    assertEquals(Arrays.asList(90.0, 60.0, 80.0), results.get("case"));
    assertEquals(Arrays.asList(110.0, null, null), results.get("old-case"));
  }
}
