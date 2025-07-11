package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

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

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

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
            .cleanDB()
            .executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/duration.sql")
            .execute();

    assertEquals(Set.of("HOSP-0001-E-11", "HOSP-0001-E-13"), rs.getSubjectIds());
  }
}
