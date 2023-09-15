package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Switch;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Ln;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Multiply;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Sum;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Gt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Lt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class MELDTest {

  private static final String CONFIG = "config/INTERPOLAR_DB_Adapter.yml";

  private static Phenotype crea =
      new Phe("crea", "http://loinc.org", "2160-0")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
          .number("mg/dL")
          .get();

  private static Phenotype bili =
      new Phe("bili", "http://loinc.org", "42719-5")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Bilirubin.total [Mass/volume] in Blood")
          .number("mg/dL")
          .get();

  private static Phenotype inr =
      new Phe("inr", "http://loinc.org", "6301-6", "34714-6", "38875-1")
          .itemType(ItemType.OBSERVATION)
          .titleEn("International Normalized Ratio")
          .number()
          .get();

  private static Phenotype dia =
      new Phe(
              "dia",
              "http://fhir.de/CodeSystem/bfarm/ops",
              "8-853",
              "8-854",
              "8-855",
              "8-857",
              "8-85a")
          .itemType(ItemType.PROCEDURE)
          .titleEn("Dialysis")
          .bool()
          .get();

  private static Phenotype med =
      new Phe("med", "http://fhir.de/CodeSystem/bfarm/atc", "B01AA", "B01AF")
          .itemType(ItemType.MEDICATION)
          .bool()
          .get();

  private static Phenotype biliClean =
      new Phe("biliClean")
          .expression(If.of(Lt.of(Exp.of(bili), Exp.of(1)), Exp.of(1), Exp.of(bili)))
          .get();

  private static Phenotype inrClean =
      new Phe("inrClean")
          .expression(If.of(Lt.of(Exp.of(inr), Exp.of(1)), Exp.of(1), Exp.of(inr)))
          .get();

  private static Phenotype existDia0_7 =
      new Phe("existDia0_7").expression(Exists.of(Filter.of(Exp.of(dia), Exp.of(7)))).get();

  private static Phenotype creaClean =
      new Phe("creaClean")
          .expression(
              Switch.of(
                  Gt.of(Exp.of(crea), Exp.of(3)),
                  Exp.of(3),
                  Exp.of(existDia0_7),
                  Exp.of(3),
                  Lt.of(Exp.of(crea), Exp.of(1)),
                  Exp.of(1),
                  Exp.of(crea)))
          .get();

  private static Phenotype meld =
      new Phe("meld")
          .expression(
              Sum.of(
                  Multiply.of(Exp.of(9.57), Ln.of(creaClean)),
                  Multiply.of(Exp.of(3.78), Ln.of(biliClean)),
                  Multiply.of(Exp.of(11.2), Ln.of(inrClean)),
                  Exp.of(6.43)))
          .get();

  private static Phenotype meld0 = new Phe("meld0").restriction(meld, Res.lt(7.5)).get();
  private static Phenotype meld1 = new Phe("meld1").restriction(meld, Res.geLt(7.5, 10)).get();
  private static Phenotype meld2 = new Phe("meld2").restriction(meld, Res.geLt(10, 15)).get();
  private static Phenotype meld3 = new Phe("meld3").restriction(meld, Res.ge(15)).get();

  @Test
  void meld1() throws InstantiationException {
    assertEquals(Set.of("99"), search(meld0, null));
  }

  @Test
  void meld2() throws InstantiationException {
    assertEquals(Set.of("11"), search(meld2, null));
  }

  @Test
  void meld3() throws InstantiationException {
    assertEquals(Set.of("22", "33", "44", "88"), search(meld3, null));
  }

  @Test
  void meld4() throws InstantiationException {
    assertEquals(Set.of("22", "44", "88"), search(meld3, med));
  }

  private Set<String> search(Phenotype inc, Phenotype exc) throws InstantiationException {
    Que q =
        new Que(
                CONFIG,
                crea,
                bili,
                inr,
                creaClean,
                biliClean,
                inrClean,
                dia,
                existDia0_7,
                med,
                meld,
                meld0,
                meld1,
                meld2,
                meld3)
            .inc(inc);

    if (exc != null) q.exc(exc);

    ResultSet rs =
        q.cleanDB()
            .executeSqlFromResources("INTERPOLAR_DB/db.sql", "INTERPOLAR_DB/meld.sql")
            .execute();
    //    System.out.println(rs);

    return rs.getSubjectIds();
  }
}
