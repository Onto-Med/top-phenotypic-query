package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.Duration;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Vals;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class DurationTest {

  private static final String CONFIG = "config/INTERPOLAR_DB_Adapter.yml";

  private static Phenotype enc = new Phe("encounter").itemType(ItemType.ENCOUNTER).string().get();
  private static Phenotype imp = new Phe("inpatient").restriction(enc, Res.of("IMP")).get();
  private static Phenotype dur =
      new Phe("duration").expression(Ge.of(Duration.of(Vals.of(imp)), Exp.of(3))).get();

  @Test
  void test() throws InstantiationException {
    ResultSet rs =
        new Que(CONFIG, enc, imp, dur)
            .inc(imp)
            .inc(dur)
            .executeSqlFromResources("INTERPOLAR_DB/db.sql", "INTERPOLAR_DB/duration.sql")
            .execute();

    assertEquals(Set.of("11", "13"), rs.getSubjectIds());
  }
}
