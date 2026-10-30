package care.smith.top.top_phenotypic_query.tests.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import module java.base;
import module java.sql;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InterpolarIcdPrefixTest {
  private static final String ICD = "http://fhir.de/CodeSystem/bfarm/icd-10-gm";
  private static final String OTHER = "http://snomed.info/sct";

  static Arguments icd(String model, String data, boolean match) {
    return Arguments.of(new String[] {ICD + "|" + model}, ICD, data, match);
  }

  static Stream<Arguments> cases() {
    return Stream.of(
        icd("F05.-", "F05", true),
        icd("F05.-", "F05.1", true),
        icd("F05.", "F05.1", true),
        icd("F05", "F05.1 L", true),
        icd("F00.1*", "F00.1", true),
        icd("F00.1", "F00.1*", true),
        icd("E11.2-", "E11.21", true),
        icd("E11.21†", "E11.21", true),
        icd("E11.21+", "E11.21 A", true),
        icd("E11.21!", "E11.21 V links", true),
        icd("E11.21 R", "E11.21", true),
        icd("E11.21", "E11.21 arbitrary suffix", true),
        icd("E11.21", "E11.22", false),
        icd("E11.21", "E11.2", false),
        icd("E11.2", "E11", false),
        icd("F05", "F06.1", false),
        icd("F05", "prefix F05", false),
        icd("F05", null, false),
        icd("", "F05.1", false),
        icd("*", "F05.1", false),
        icd("invalid", "F05.1", false),
        icd("F0", "F05.1", false),
        Arguments.of(new String[] {ICD + "|F05"}, OTHER, "F05.1", false),
        Arguments.of(new String[] {OTHER + "|F05"}, OTHER, "F05.1", false),
        Arguments.of(new String[] {OTHER + "|F05*"}, OTHER, "F05", false),
        Arguments.of(new String[] {OTHER + "|F05*"}, OTHER, "F05*", true),
        Arguments.of(new String[] {ICD + "|F05"}, null, "F05", false),
        Arguments.of(
            new String[] {ICD + "|F05.-", ICD + "|F05", ICD + "|F05.1*"}, ICD, "F05.1 L", true),
        Arguments.of(
            new String[] {ICD + "|invalid", ICD + "|F05.1*", OTHER + "|123"}, ICD, "F05.1", true),
        Arguments.of(new String[] {ICD + "|F05.1*", OTHER + "|123"}, OTHER, "123", true));
  }

  @ParameterizedTest
  @MethodSource("cases")
  void matches(String[] models, String system, String code, boolean expected) throws Exception {
    try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:icd_prefix")) {
      verify(connection, models, system, code, expected);
    }
  }

  static void verify(
      Connection connection, String[] models, String system, String code, boolean expected)
      throws Exception {
    try (Statement statement = connection.createStatement()) {
      statement.execute("CREATE SCHEMA IF NOT EXISTS db2dataprocessor_out");
      statement.execute("DROP TABLE IF EXISTS db2dataprocessor_out.v_condition_last_version");
      statement.execute(
          """
          CREATE TABLE db2dataprocessor_out.v_condition_last_version (
            con_encounter_calculated_ref VARCHAR(100), con_code_system VARCHAR(100),
            con_code_code VARCHAR(100), con_onsetperiod_start TIMESTAMP,
            con_onsetperiod_end TIMESTAMP, con_onsetdatetime TIMESTAMP,
            con_recordeddate TIMESTAMP)
          """);
    }
    try (PreparedStatement insert =
        connection.prepareStatement(
            """
        INSERT INTO db2dataprocessor_out.v_condition_last_version
          (con_encounter_calculated_ref, con_code_system, con_code_code)
        VALUES ('Encounter/test', ?, ?)
        """)) {
      insert.setString(1, system);
      insert.setString(2, code);
      insert.executeUpdate();
    }
    String query =
        DataAdapterConfig.getInstanceFromResource(
                "default_adapter_configuration/Default_Interpolar_Adapter.yml")
            .getPhenotypeQuery("condition")
            .getBaseQuery()
            .replace("{codes}", String.join(",", Collections.nCopies(models.length, "?")));
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      for (int i = 0; i < models.length; i++) statement.setString(i + 1, models[i]);
      try (java.sql.ResultSet result = statement.executeQuery()) {
        int count = 0;
        while (result.next()) {
          assertEquals("test", result.getString("enc_id"));
          count++;
        }
        assertEquals(
            expected ? 1 : 0, count, Arrays.toString(models) + " / " + system + "|" + code);
      }
    }
  }
}
