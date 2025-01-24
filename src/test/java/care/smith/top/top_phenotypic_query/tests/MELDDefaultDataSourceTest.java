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
import care.smith.top.top_phenotypic_query.c2reasoner.functions.encounter.EncAge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Filter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.DefaultSqlWriterDataSource;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.EncDao;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.PheDao;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.SbjDao;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MELDDefaultDataSourceTest {

  private static final DataAdapterConfig CONFIG =
      DataAdapterConfig.getInstanceFromResource("config/Default_Data_Source_SQL_Adapter.yml");

  private static final DefaultSqlWriterDataSource WRITER = new DefaultSqlWriterDataSource(CONFIG);

  private static Phenotype birthDate =
      new Phe("birthDate")
          .itemType(ItemType.SUBJECT_BIRTH_DATE)
          .titleEn("Birth date")
          .dateTime()
          .get();

  private static Phenotype enc =
      new Phe("encounter").itemType(ItemType.ENCOUNTER).titleEn("Encounter").string().get();
  private static Phenotype inp =
      new Phe("inpatient").titleEn("Inpatient").restriction(enc, Res.of("INP")).get();
  private static Phenotype amb =
      new Phe("ambulatory").titleEn("Ambulatory").restriction(enc, Res.of("AMB")).get();

  private static Phenotype encAge =
      new Phe("encAge").titleEn("Encounter age").expression(EncAge.of(birthDate, enc)).get();
  private static Phenotype encAgeGe17 =
      new Phe("encAgeGe17").titleEn("Encounter age >= 17").restriction(encAge, Res.ge(17)).get();

  private static Phenotype age =
      new Phe("age").itemType(ItemType.SUBJECT_AGE).titleEn("Age").number().get();

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

  private static final String DATA_SOURCE_ID = "data_source_1";

  @BeforeAll
  static void beforeAll() {
    SbjDao sbjYoung =
        SbjDao.get("sbjYoung", "2001-01-01", "male")
            .encounter(
                EncDao.get("sbjYoung_enc0", "INP", "2019-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 0.1))
                    .phenotype(PheDao.get(crea, "2019-01-02", 2.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 0.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 0.3)))
            .encounter(
                EncDao.get("sbjYoung_enc1", "AMB", "2018-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 1.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 1.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 1.3)))
            .encounter(
                EncDao.get("sbjYoung_enc2", "INP", "2019-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 2.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 2.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 2.3)));

    WRITER.insertSbj(DATA_SOURCE_ID, sbjYoung);

    SbjDao sbjOld =
        SbjDao.get("sbjOld", "1951-01-01", "female")
            .encounter(
                EncDao.get("sbjOld_enc3", "AMB", "2018-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 3.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 3.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 3.3))
                    .phenotype(PheDao.get(med, "2020-01-01", true)))
            .encounter(
                EncDao.get("sbjOld_enc4", "INP", "2019-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 0.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 0.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 0.3))
                    .phenotype(PheDao.get(diaInt, DateUtil.parse("2020-01-01").minusDays(8), true))
                    .phenotype(PheDao.get(diaInt, DateUtil.parse("2020-01-01").minusDays(6), true)))
            .encounter(
                EncDao.get("sbjOld_enc5", "AMB", "2018-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 2.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 2.2)))
            .encounter(
                EncDao.get("sbjOld_enc6", "INP", "2019-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 2.1))
                    .phenotype(PheDao.get(inr, "2020-01-01", 2.3)))
            .encounter(
                EncDao.get("sbjOld_enc7", "AMB", "2018-01-01", "2020-01-02")
                    .phenotype(PheDao.get(bili, "2020-01-01", 2.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 2.3)))
            .encounter(
                EncDao.get("sbjOld_enc8", "INP", "2019-01-01", "2020-01-02")
                    .phenotype(PheDao.get(bili, "2020-01-01", 3.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 3.3))
                    .phenotype(PheDao.get(diaInt, DateUtil.parse("2020-01-01").minusDays(5), true))
                    .phenotype(PheDao.get(diaInt, DateUtil.parse("2020-01-01").minusDays(6), true)))
            .encounter(
                EncDao.get("sbjOld_enc9", "AMB", "2018-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 0.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 0.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 0.3))
                    .phenotype(PheDao.get(diaInt, DateUtil.parse("2020-01-01").minusDays(7), true))
                    .phenotype(PheDao.get(diaInt, DateUtil.parse("2020-01-01").minusDays(6), true)))
            .encounter(
                EncDao.get("sbjOld_enc10", "INP", "2019-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 0.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 0.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 0.3))
                    .phenotype(PheDao.get(diaCon, DateUtil.parse("2020-01-01").minusDays(7), true)))
            .encounter(
                EncDao.get("sbjOld_enc11", "AMB", "2018-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 0.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 0.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 0.3))
                    .phenotype(
                        PheDao.get(diaCon, DateUtil.parse("2020-01-01").minusDays(8), true)));

    WRITER.insertSbj(DATA_SOURCE_ID, sbjOld);

    SbjDao sbjVeryYoung =
        SbjDao.get("sbjVeryYoung", "2015-01-01", "female")
            .encounter(
                EncDao.get("sbjVeryYoung_enc12", "INP", "2019-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 0.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 0.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 0.3))
                    .phenotype(PheDao.get(diaCon, DateUtil.parse("2020-01-01").minusDays(7), true)))
            .encounter(
                EncDao.get("sbjVeryYoung_enc13", "AMB", "2018-01-01", "2020-01-02")
                    .phenotype(PheDao.get(crea, "2020-01-01", 1.1))
                    .phenotype(PheDao.get(bili, "2020-01-01", 1.2))
                    .phenotype(PheDao.get(inr, "2020-01-01", 1.3)));

    WRITER.insertSbj(DATA_SOURCE_ID, sbjVeryYoung);

    WRITER.printSbj();
    WRITER.printEnc();
    WRITER.printPhe();
  }

  @AfterAll
  static void afterAll() {
    WRITER.close();
  }

  @Test
  void testMELD0() throws InstantiationException {
    ResultSet rs = search(meld0, null, null);
    assertEquals(Set.of("sbjYoung_enc0", "sbjOld_enc4", "sbjOld_enc11"), rs.getSubjectIds());
    assertEquals(new BigDecimal("6.430"), rs.getNumberValue("sbjYoung_enc0", "meld", null));
    assertEquals(new BigDecimal("6.430"), rs.getNumberValue("sbjOld_enc4", "meld", null));
    assertEquals(new BigDecimal("6.430"), rs.getNumberValue("sbjOld_enc11", "meld", null));
  }

  @Test
  void testMELD2a() throws InstantiationException {
    ResultSet rs = search(meld2, null, null);
    assertEquals(Set.of("sbjYoung_enc1", "sbjVeryYoung_enc13"), rs.getSubjectIds());
    assertEquals(
        new BigDecimal("10.96977366744444"), rs.getNumberValue("sbjYoung_enc1", "meld", null));
    assertEquals(
        new BigDecimal("10.96977366744444"), rs.getNumberValue("sbjVeryYoung_enc13", "meld", null));
  }

  @Test
  void testMELD2b() throws InstantiationException {
    ResultSet rs = search(meld2, null, encAgeGe17);
    assertEquals(Set.of("sbjYoung_enc1"), rs.getSubjectIds());
    assertEquals(
        new BigDecimal("10.96977366744444"), rs.getNumberValue("sbjYoung_enc1", "meld", null));
  }

  @Test
  void testMELD3a() throws InstantiationException {
    ResultSet rs = search(meld3, null, null);
    assertEquals(
        Set.of("sbjYoung_enc2", "sbjOld_enc3", "sbjOld_enc9", "sbjOld_enc10", "sbjVeryYoung_enc12"),
        rs.getSubjectIds());
    assertEquals(
        new BigDecimal("25.83929138811024"), rs.getNumberValue("sbjYoung_enc2", "meld", null));
    assertEquals(
        new BigDecimal("35.02615991492657"), rs.getNumberValue("sbjOld_enc3", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjOld_enc9", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjOld_enc10", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjVeryYoung_enc12", "meld", null));
  }

  @Test
  void testMELD3b() throws InstantiationException {
    ResultSet rs = search(meld3, amb, null);
    assertEquals(Set.of("sbjYoung_enc2", "sbjOld_enc10", "sbjVeryYoung_enc12"), rs.getSubjectIds());
    assertEquals(
        new BigDecimal("25.83929138811024"), rs.getNumberValue("sbjYoung_enc2", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjOld_enc10", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjVeryYoung_enc12", "meld", null));
  }

  @Test
  void testMELD3c() throws InstantiationException {
    ResultSet rs = search(meld3, med, null);
    assertEquals(
        Set.of("sbjYoung_enc2", "sbjOld_enc9", "sbjOld_enc10", "sbjVeryYoung_enc12"),
        rs.getSubjectIds());
    assertEquals(
        new BigDecimal("25.83929138811024"), rs.getNumberValue("sbjYoung_enc2", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjOld_enc9", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjOld_enc10", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjVeryYoung_enc12", "meld", null));
  }

  @Test
  void testMELD3d() throws InstantiationException {
    ResultSet rs = search(meld3, med, encAgeGe17);
    assertEquals(Set.of("sbjYoung_enc2", "sbjOld_enc9", "sbjOld_enc10"), rs.getSubjectIds());
    assertEquals(
        new BigDecimal("25.83929138811024"), rs.getNumberValue("sbjYoung_enc2", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjOld_enc9", "meld", null));
    assertEquals(
        new BigDecimal("19.69683703591735"), rs.getNumberValue("sbjOld_enc10", "meld", null));
  }

  private ResultSet search(Phenotype inc, Phenotype exc, Phenotype ageInc)
      throws InstantiationException {
    Que q =
        new Que(
                CONFIG,
                birthDate,
                enc,
                inp,
                amb,
                encAge,
                encAgeGe17,
                age,
                crea,
                creaHigh,
                creaLow,
                bili,
                inr,
                creaAdj,
                biliAdj,
                inrAdj,
                diaInt,
                diaCon,
                diaAdj,
                med,
                meld,
                meld0,
                meld1,
                meld2,
                meld3)
            .pro(age)
            .pro(encAge)
            .pro(encAgeGe17)
            .pro(enc)
            .inc(inc);
    if (ageInc != null) q.inc(ageInc);
    if (exc != null) q.exc(exc);

    ResultSet rs = q.execute();

    //    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.out.println(new CSV().toStringSubjects(rs, q.getEntities(), q.getQuery()));
    //    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    return rs;
  }
}
