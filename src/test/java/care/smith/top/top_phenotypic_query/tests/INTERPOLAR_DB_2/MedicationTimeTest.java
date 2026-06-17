package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class MedicationTimeTest {

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

  private static final Phenotype medadm =
      new Phe("medadm", "http://fhir.de/CodeSystem/bfarm/atc", "atc1", "atc2")
          .itemType(ItemType.MEDICATION_ADMINISTRATION)
          .bool()
          .get();

  private static final Phenotype medstmt =
      new Phe("medstmt", "http://fhir.de/CodeSystem/bfarm/atc", "atc1", "atc2")
          .itemType(ItemType.MEDICATION_STATEMENT)
          .bool()
          .get();

  static Arguments a(Phenotype phe, UnaryOperator<Que> restriction, String... expected) {
    return Arguments.of(phe, restriction, Set.of(expected));
  }

  static Stream<Arguments> data() {
    return Stream.of(
        a(
            medadm,
            q ->
                q.inc(medadm, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-01"))),
            "HOSP-0004-E-1",
            "HOSP-0011-E-1",
            "HOSP-0012-E-1"),
        a(
            medadm,
            q ->
                q.inc(medadm, Res.geLe(DateUtil.parse("2020-01-02"), DateUtil.parse("2020-01-02"))),
            "HOSP-0004-E-1",
            "HOSP-0012-E-1",
            "HOSP-0014-E-1"),
        a(
            medadm,
            q ->
                q.inc(medadm, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-02"))),
            "HOSP-0004-E-1",
            "HOSP-0011-E-1",
            "HOSP-0012-E-1",
            "HOSP-0014-E-1"),
        a(
            medadm,
            q ->
                q.inc(medadm, Res.geLe(DateUtil.parse("2020-01-03"), DateUtil.parse("2020-01-03"))),
            "HOSP-0004-E-1",
            "HOSP-0013-E-1",
            "HOSP-0014-E-1"),
        a(
            medadm,
            q ->
                q.inc(medadm, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-03"))),
            "HOSP-0004-E-1",
            "HOSP-0011-E-1",
            "HOSP-0012-E-1",
            "HOSP-0013-E-1",
            "HOSP-0014-E-1"),
        a(
            medadm,
            q -> q.inc(medadm, Res.ge(DateUtil.parse("2020-01-03"))),
            "HOSP-0004-E-1",
            "HOSP-0013-E-1",
            "HOSP-0014-E-1"),
        a(
            medadm,
            q -> q.inc(medadm, Res.le(DateUtil.parse("2020-01-04"))),
            "HOSP-0004-E-1",
            "HOSP-0011-E-1",
            "HOSP-0012-E-1",
            "HOSP-0013-E-1",
            "HOSP-0014-E-1"),
        a(
            medstmt,
            q ->
                q.inc(
                    medstmt, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-01"))),
            "HOSP-0031-E-1",
            "HOSP-0032-E-1"),
        a(
            medstmt,
            q ->
                q.inc(
                    medstmt, Res.geLe(DateUtil.parse("2020-01-02"), DateUtil.parse("2020-01-02"))),
            "HOSP-0032-E-1",
            "HOSP-0034-E-1"),
        a(
            medstmt,
            q ->
                q.inc(
                    medstmt, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-02"))),
            "HOSP-0031-E-1",
            "HOSP-0032-E-1",
            "HOSP-0034-E-1"),
        a(
            medstmt,
            q ->
                q.inc(
                    medstmt, Res.geLe(DateUtil.parse("2020-01-03"), DateUtil.parse("2020-01-03"))),
            "HOSP-0033-E-1",
            "HOSP-0034-E-1"),
        a(
            medstmt,
            q ->
                q.inc(
                    medstmt, Res.geLe(DateUtil.parse("2020-01-01"), DateUtil.parse("2020-01-03"))),
            "HOSP-0031-E-1",
            "HOSP-0032-E-1",
            "HOSP-0033-E-1",
            "HOSP-0034-E-1"),
        a(
            medstmt,
            q -> q.inc(medstmt, Res.ge(DateUtil.parse("2020-01-03"))),
            "HOSP-0033-E-1",
            "HOSP-0034-E-1"),
        a(
            medstmt,
            q -> q.inc(medstmt, Res.le(DateUtil.parse("2020-01-04"))),
            "HOSP-0031-E-1",
            "HOSP-0032-E-1",
            "HOSP-0033-E-1",
            "HOSP-0034-E-1"));
  }

  @ParameterizedTest
  @MethodSource("data")
  void medicationAdministration(
      Phenotype phe, Function<Que, Que> restrictions, Set<String> expectedSubjectIds)
      throws InstantiationException {
    ResultSet rs =
        restrictions
            .apply(new Que(CONFIG, phe))
            .executeSqlFromResources(
                "INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/medication_time.sql")
            .execute();
    assertEquals(expectedSubjectIds, rs.getSubjectIds());
  }
}
