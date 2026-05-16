package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionTest {
  
  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";
  
  private static final Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();
  private static final Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();
  private static final Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).number().get();
  private static final Phenotype old = new Phe("old").restriction(age, Res.ge(60)).get();
  
  private static final Phenotype con =
          new Phe("con", "http://snomed.info/sct", "135816001", "258157001", "438949009")
                  .itemType(ItemType.CONDITION)
                  .bool()
                  .get();
  
  static Arguments a(UnaryOperator<Que> restriction, String... expected) {
    return Arguments.of(restriction, Set.of(expected));
  }
  
  static Stream<Arguments> data() {
    return Stream.of(
            a(q -> q.inc(con, Res.geLe(DateUtil.parse("2026-04-01"), DateUtil.parse("2026-04-01"))),
                    "HOSP-0002-E2"),
            a(q -> q.inc(con, Res.geLe(DateUtil.parse("2026-04-02"), DateUtil.parse("2026-04-02"))),
                    "HOSP-0003-E3"),
            a(q -> q.inc(con, Res.geLe(DateUtil.parse("2026-04-01"), DateUtil.parse("2026-04-02"))),
                    "HOSP-0002-E2", "HOSP-0003-E3"),
            a(q -> q.inc(con, Res.geLe(DateUtil.parse("2026-04-03"), DateUtil.parse("2026-04-03"))),
                    "HOSP-0004-E4"),
            a(q -> q.inc(con, Res.geLe(DateUtil.parse("2026-04-01"), DateUtil.parse("2026-04-03"))),
                    "HOSP-0002-E2", "HOSP-0003-E3", "HOSP-0004-E4")
    );
  }
  
  @ParameterizedTest
  @MethodSource("data")
  void condition(Function<Que, Que> restrictions, Set<String> expectedSubjectIds) throws InstantiationException {
    ResultSet rs = restrictions.apply(new Que(CONFIG, con))
            .executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/condition.sql")
            .execute();
    assertEquals(expectedSubjectIds, rs.getSubjectIds());
  }
}
