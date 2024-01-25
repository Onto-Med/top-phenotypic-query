package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Switch;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Count;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Ln;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Multiply;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Sum;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Gt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Lt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.DefaultSqlWriter;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MELDTest {

  private static final DataAdapterConfig CONFIG =
      DataAdapterConfig.getInstanceFromResource("config/Default_SQL_Adapter.yml");

  private static final DefaultSqlWriter WRITER = new DefaultSqlWriter(CONFIG);

  private static Phenotype age =
      new Phe("age").itemType(ItemType.SUBJECT_AGE).titleEn("Age").number().get();

  private static Phenotype old = new Phe("old").titleEn("Old").restriction(age, Res.ge(12)).get();
  private static Phenotype young =
      new Phe("young").titleEn("Young").restriction(age, Res.lt(12)).get();

  private static Phenotype crea =
      new Phe("crea", "http://loinc.org", "2160-0")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Creatinine")
          .number("mg/dL")
          .get();

  private static Phenotype creaHigh =
      new Phe("creaHigh").titleEn("Crea High").restriction(crea, Res.gt(2)).get();
  private static Phenotype creaLow =
      new Phe("creaLow").titleEn("Crea Low").restriction(crea, Res.le(2)).get();

  private static Phenotype bili =
      new Phe("bili", "http://loinc.org", "42719-5")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Bilirubin")
          .number("mg/dL")
          .get();

  private static Phenotype inr =
      new Phe("inr", "http://loinc.org", "6301-6", "34714-6", "38875-1")
          .itemType(ItemType.OBSERVATION)
          .titleEn("INR")
          .number()
          .get();

  private static Phenotype diaInt =
      new Phe(
              "diaInt",
              "http://fhir.de/CodeSystem/bfarm/ops",
              "8-853.3",
              "8-854.2",
              "8-855.3",
              "8-857.0")
          .itemType(ItemType.PROCEDURE)
          .titleEn("Dialysis (intermittent)")
          .bool()
          .get();

  private static Phenotype diaCon =
      new Phe(
              "diaCon",
              "http://fhir.de/CodeSystem/bfarm/ops",
              "8-853.14",
              "8-854.61",
              "8-855.71",
              "8-857.21")
          .itemType(ItemType.PROCEDURE)
          .titleEn("Dialysis (continuous)")
          .bool()
          .get();

  private static Phenotype med =
      new Phe("med", "http://fhir.de/CodeSystem/bfarm/atc", "B01AA", "B01AF")
          .itemType(ItemType.MEDICATION)
          .titleEn("Med")
          .bool()
          .get();

  private static Phenotype biliAdj =
      new Phe("biliAdj")
          .titleEn("Bili Adj")
          .expression(If.of(Lt.of(Exp.of(bili), Exp.of(1)), Exp.of(1), Exp.of(bili)))
          .get();

  private static Phenotype inrAdj =
      new Phe("inrAdj")
          .titleEn("Inr Adj")
          .expression(If.of(Lt.of(Exp.of(inr), Exp.of(1)), Exp.of(1), Exp.of(inr)))
          .get();

  private static Phenotype diaAdj =
      new Phe("diaAdj")
          .titleEn("Dia Adj")
          .expression(
              Or.of(
                  Exists.of(Filter.of(Exp.of(diaCon), Exp.of(7), Exp.of(crea))),
                  Ge.of(Count.of(Filter.of(Exp.of(diaInt), Exp.of(7), Exp.of(crea))), Exp.of(2))))
          .get();

  private static Phenotype creaAdj =
      new Phe("creaAdj")
          .titleEn("Crea Adj")
          .expression(
              Switch.of(
                  Gt.of(Exp.of(crea), Exp.of(4)),
                  Exp.of(4),
                  Exp.of(diaAdj),
                  Exp.of(4),
                  Lt.of(Exp.of(crea), Exp.of(1)),
                  Exp.of(1),
                  Exp.of(crea)))
          .get();

  private static Phenotype meld =
      new Phe("meld")
          .titleEn("MELD")
          .expression(
              Sum.of(
                  Multiply.of(Exp.of(9.57), Ln.of(creaAdj)),
                  Multiply.of(Exp.of(3.78), Ln.of(biliAdj)),
                  Multiply.of(Exp.of(11.2), Ln.of(inrAdj)),
                  Exp.of(6.43)))
          .get();

  private static Phenotype meld0 =
      new Phe("meld0").titleEn("MELD0").restriction(meld, Res.lt(7.5)).get();
  private static Phenotype meld1 =
      new Phe("meld1").titleEn("MELD1").restriction(meld, Res.geLt(7.5, 10)).get();
  private static Phenotype meld2 =
      new Phe("meld2").titleEn("MELD2").restriction(meld, Res.geLt(10, 15)).get();
  private static Phenotype meld3 =
      new Phe("meld3").titleEn("MELD3").restriction(meld, Res.ge(15)).get();

  @BeforeAll
  static void beforeAll() {
    WRITER
        .insertSbj(0, "2001-01-01", "male")
        .insertPhe("2020-01-01", crea, 0.1)
        .insertPhe("2019-01-02", crea, 2.1)
        .insertPhe("2020-01-01", bili, 0.2)
        .insertPhe("2020-01-01", inr, 0.3);

    WRITER
        .insertSbj(1, "2001-01-01", "male")
        .insertPhe("2020-01-01", crea, 1.1)
        .insertPhe("2020-01-01", bili, 1.2)
        .insertPhe("2020-01-01", inr, 1.3);

    WRITER
        .insertSbj(2, "2001-01-01", "female")
        .insertPhe("2020-01-01", crea, 2.1)
        .insertPhe("2020-01-01", bili, 2.2)
        .insertPhe("2020-01-01", inr, 2.3);

    WRITER
        .insertSbj(3, "1951-01-01", "male")
        .insertPhe("2020-01-01", crea, 3.1)
        .insertPhe("2020-01-01", bili, 3.2)
        .insertPhe("2020-01-01", inr, 3.3)
        .insertPhe("2020-01-01", med, true);

    WRITER
        .insertSbj(4, "1951-01-01", "female")
        .insertPhe("2020-01-01", crea, 0.1)
        .insertPhe("2020-01-01", bili, 0.2)
        .insertPhe("2020-01-01", inr, 0.3)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(8), diaInt, true)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(6), diaInt, true);

    WRITER
        .insertSbj(5, "1951-01-01", "male")
        .insertPhe("2020-01-01", crea, 2.1)
        .insertPhe("2020-01-01", bili, 2.2);

    WRITER
        .insertSbj(6, "1951-01-01", "male")
        .insertPhe("2020-01-01", crea, 2.1)
        .insertPhe("2020-01-01", inr, 2.3);

    WRITER
        .insertSbj(7, "1951-01-01", "male")
        .insertPhe("2020-01-01", bili, 2.2)
        .insertPhe("2020-01-01", inr, 2.3);

    WRITER
        .insertSbj(8, "1951-01-01", "male")
        .insertPhe("2020-01-01", bili, 3.2)
        .insertPhe("2020-01-01", inr, 3.3)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(5), diaInt, true)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(6), diaInt, true);

    WRITER
        .insertSbj(9, "1951-01-01", "female")
        .insertPhe("2020-01-01", crea, 0.1)
        .insertPhe("2020-01-01", bili, 0.2)
        .insertPhe("2020-01-01", inr, 0.3)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(7), diaInt, true)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(6), diaInt, true);

    WRITER
        .insertSbj(10, "1951-01-01", "female")
        .insertPhe("2020-01-01", crea, 0.1)
        .insertPhe("2020-01-01", bili, 0.2)
        .insertPhe("2020-01-01", inr, 0.3)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(7), diaCon, true);

    WRITER
        .insertSbj(11, "1951-01-01", "female")
        .insertPhe("2020-01-01", crea, 0.1)
        .insertPhe("2020-01-01", bili, 0.2)
        .insertPhe("2020-01-01", inr, 0.3)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(8), diaCon, true);

    WRITER
        .insertSbj(12, LocalDateTime.now().minusYears(10), "female")
        .insertPhe("2020-01-01", crea, 0.1)
        .insertPhe("2020-01-01", bili, 0.2)
        .insertPhe("2020-01-01", inr, 0.3)
        .insertPhe(DateUtil.parse("2020-01-01").minusDays(7), diaCon, true);

    WRITER
        .insertSbj(13, LocalDateTime.now().minusYears(10), "male")
        .insertPhe("2020-01-01", crea, 1.1)
        .insertPhe("2020-01-01", bili, 1.2)
        .insertPhe("2020-01-01", inr, 1.3);

    //    WRITER.printSbj();
    //    WRITER.printPhe();
  }

  @AfterAll
  static void afterAll() {
    WRITER.close();
  }

  @Test
  void testMELD0() throws InstantiationException {
    ResultSet rs = search(meld0, null, null);
    assertEquals(Set.of("0", "4", "11"), rs.getSubjectIds());
    assertEquals(new BigDecimal("6.430"), rs.getNumberValue("0", "meld", null));
    assertEquals(new BigDecimal("6.430"), rs.getNumberValue("4", "meld", null));
    assertEquals(new BigDecimal("6.430"), rs.getNumberValue("11", "meld", null));
  }

  @Test
  void testMELD2a() throws InstantiationException {
    ResultSet rs = search(meld2, null, null);
    assertEquals(Set.of("1", "13"), rs.getSubjectIds());
    assertEquals(new BigDecimal("10.96977366744444"), rs.getNumberValue("1", "meld", null));
    assertEquals(new BigDecimal("10.96977366744444"), rs.getNumberValue("13", "meld", null));
  }

  @Test
  void testMELD2b() throws InstantiationException {
    ResultSet rs = search(meld2, null, old);
    assertEquals(Set.of("1"), rs.getSubjectIds());
    assertEquals(new BigDecimal("10.96977366744444"), rs.getNumberValue("1", "meld", null));
  }

  @Test
  void testMELD3a() throws InstantiationException {
    ResultSet rs = search(meld3, null, null);
    assertEquals(Set.of("2", "3", "9", "10", "12"), rs.getSubjectIds());
    assertEquals(new BigDecimal("25.83929138811024"), rs.getNumberValue("2", "meld", null));
    assertEquals(new BigDecimal("35.02615991492657"), rs.getNumberValue("3", "meld", null));
    assertEquals(new BigDecimal("19.69683703591735"), rs.getNumberValue("9", "meld", null));
    assertEquals(new BigDecimal("19.69683703591735"), rs.getNumberValue("10", "meld", null));
    assertEquals(new BigDecimal("19.69683703591735"), rs.getNumberValue("12", "meld", null));
  }

  @Test
  void testMELD3b() throws InstantiationException {
    ResultSet rs = search(meld3, med, null);
    assertEquals(Set.of("2", "9", "10", "12"), rs.getSubjectIds());
    assertEquals(new BigDecimal("25.83929138811024"), rs.getNumberValue("2", "meld", null));
    assertEquals(new BigDecimal("19.69683703591735"), rs.getNumberValue("9", "meld", null));
    assertEquals(new BigDecimal("19.69683703591735"), rs.getNumberValue("10", "meld", null));
    assertEquals(new BigDecimal("19.69683703591735"), rs.getNumberValue("12", "meld", null));
  }

  @Test
  void testMELD3c() throws InstantiationException {
    ResultSet rs = search(meld3, med, old);
    assertEquals(Set.of("2", "9", "10"), rs.getSubjectIds());
    assertEquals(new BigDecimal("25.83929138811024"), rs.getNumberValue("2", "meld", null));
    assertEquals(new BigDecimal("19.69683703591735"), rs.getNumberValue("9", "meld", null));
    assertEquals(new BigDecimal("19.69683703591735"), rs.getNumberValue("10", "meld", null));
  }

  private ResultSet search(Phenotype inc, Phenotype exc, Phenotype ageInc)
      throws InstantiationException {
    Que q =
        new Que(
                CONFIG, age, old, young, crea, creaHigh, creaLow, bili, inr, creaAdj, biliAdj,
                inrAdj, diaInt, diaCon, diaAdj, med, meld, meld0, meld1, meld2, meld3)
            .pro(young)
            .pro(old)
            .inc(inc);
    if (ageInc != null) q.inc(ageInc);
    if (exc != null) q.exc(exc);

    ResultSet rs = q.execute();

    //    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.out.println(new CSV().toStringWideTable(rs, q.getEntities(), q.getQuery()));
    //    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    return rs;
  }
}
