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

public class ProcedureTest {

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

  private static final Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();
  private static final Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();
  private static final Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).number().get();
  private static final Phenotype old = new Phe("old").restriction(age, Res.ge(60)).get();

  private static final Phenotype proc =
      new Phe("proc", "http://fhir.de/CodeSystem/bfarm/ops", "8-853.3", "8-855.71", "8-857.21")
          .itemType(ItemType.PROCEDURE)
          .bool()
          .get();

  static Arguments a(UnaryOperator<Que> restriction, String... expected) {
    return Arguments.of(restriction, Set.of(expected));
  }

  static Stream<Arguments> data() {
    return Stream.of(
        a(
            q -> q.inc(proc, Res.geLe(DateUtil.parse("2026-04-01"), DateUtil.parse("2026-04-01"))),
            "HOSP-0001-E1",
            "HOSP-0002-E2"),
        a(
            q -> q.inc(proc, Res.geLe(DateUtil.parse("2026-04-02"), DateUtil.parse("2026-04-02"))),
            "HOSP-0002-E2",
            "HOSP-0003-E3"),
        a(
            q -> q.inc(proc, Res.geLe(DateUtil.parse("2026-04-01"), DateUtil.parse("2026-04-02"))),
            "HOSP-0001-E1",
            "HOSP-0002-E2",
            "HOSP-0003-E3"),
        a(
            q -> q.inc(proc, Res.geLe(DateUtil.parse("2026-04-03"), DateUtil.parse("2026-04-03"))),
            "HOSP-0004-E4"),
        a(
            q -> q.inc(proc, Res.geLe(DateUtil.parse("2026-04-01"), DateUtil.parse("2026-04-03"))),
            "HOSP-0001-E1",
            "HOSP-0002-E2",
            "HOSP-0003-E3",
            "HOSP-0004-E4"),
        a(q -> q.inc(proc, Res.ge(DateUtil.parse("2026-04-07"))), "HOSP-0005-E5", "HOSP-0007-E7"),
        a(
            q -> q.inc(proc, Res.le(DateUtil.parse("2026-04-05"))),
            "HOSP-0001-E1",
            "HOSP-0002-E2",
            "HOSP-0003-E3",
            "HOSP-0004-E4",
            "HOSP-0005-E5",
            "HOSP-0006-E6"));
  }

  @ParameterizedTest
  @MethodSource("data")
  void procedure(Function<Que, Que> restrictions, Set<String> expectedSubjectIds)
      throws InstantiationException {
    ResultSet rs =
        restrictions
            .apply(new Que(CONFIG, proc))
            .executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/procedure.sql")
            .execute();
    assertEquals(expectedSubjectIds, rs.getSubjectIds());
  }
}
