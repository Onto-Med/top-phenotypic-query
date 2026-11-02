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
            analysis_loinc_code VARCHAR, analysis_unit VARCHAR, analysis_value_status VARCHAR)
          """);
      statement.execute(
          """
          CREATE TABLE db2dataprocessor_out.v_encounter_last_version (
            enc_id VARCHAR, enc_period_start TIMESTAMP, enc_age_at_admission INTEGER)
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
    try (PreparedStatement statement =
        connection.prepareStatement(
            "INSERT INTO db2dataprocessor_out.v_observation_last_version VALUES (?, ?, ?, ?, ?, ?, ?)")) {
      statement.setString(1, id);
      statement.setString(2, "Encounter/" + encounter);
      statement.setTimestamp(3, java.sql.Timestamp.valueOf(date));
      statement.setObject(4, value);
      statement.setString(5, code);
      statement.setString(6, unit);
      statement.setString(7, status);
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
      statement.execute(
          """
          INSERT INTO db2dataprocessor_out.v_encounter_last_version VALUES
          ('old-case', '2001-01-01 00:00:00', 42), ('new-case', '2026-01-01 00:00:00', 61),
          ('missing-age', '2026-01-01 00:00:00', NULL)
          """);
    }
    assertEquals(List.of("new-case:61.0", "old-case:42.0"), query("29553-5"));
  }
}
