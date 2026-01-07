package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.ForEach;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Switch;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Max;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Median;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Min;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Subtract;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.And;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Gt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.RefValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Obs;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import java.util.List;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Disabled
public class AKIPolarTestIntern {

  private static final Logger LOGGER = LoggerFactory.getLogger(AKIPolarTestIntern.class);

  private static Client client = new Client();

  private static Phenotype crea =
      new Phe("crea", "http://loinc.org", "2160-0")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
          .number("mmol/L")
          .get();

  private static Phenotype refCrea0_7 =
      new Phe("refCrea0_7").expression(Min.of(RefValues.of(Exp.of(crea), Exp.of(7)))).get();

  private static Phenotype refCrea8_365 =
      new Phe("refCrea8_365")
          .expression(Median.of(RefValues.of(Exp.of(crea), Exp.of(8), Exp.of(365))))
          .get();

  private static Phenotype rvRatio =
      new Phe("rvRatio")
          .expression(
              Divide.of(
                  Exp.of(crea),
                  If.of(Exists.of(refCrea0_7), Exp.of(refCrea0_7), Exp.of(refCrea8_365))))
          .get();

  private static Phenotype rvRatioGe1_5 =
      new Phe("rvRatioGe1_5").restriction(rvRatio, Res.ge(1.5)).get();
  private static Phenotype rvRatioGe1_5Lt2 =
      new Phe("rvRatioGe1_5Lt2").restriction(rvRatio, Res.geLt(1.5, 2)).get();
  private static Phenotype rvRatioGe2Lt3 =
      new Phe("rvRatioGe2Lt3").restriction(rvRatio, Res.geLt(2, 3)).get();
  private static Phenotype rvRatioGe3 = new Phe("rvRatioGe3").restriction(rvRatio, Res.ge(3)).get();

  private static Phenotype akiSingle =
      new Phe("AKISingle")
          .expression(
              Switch.of(
                  And.of(Exp.of(rvRatioGe1_5), Gt.of(Exp.of(crea), Exp.of(354))),
                  Exp.of(3),
                  Exp.of(rvRatioGe3),
                  Exp.of(3),
                  Exp.of(rvRatioGe2Lt3),
                  Exp.of(2),
                  Exp.of(rvRatioGe1_5Lt2),
                  Exp.of(1),
                  Gt.of(
                      Subtract.of(Exp.of(crea), Exp.of(26)),
                      Min.of(RefValues.of(Exp.of(crea), Exp.of(2)))),
                  Exp.of(1),
                  Exp.of(0)))
          .get();

  private static Phenotype akiAll = new Phe("AKIAll").expression(ForEach.of(crea, akiSingle)).get();

  private static Phenotype aki = new Phe("AKI").expression(Max.of(akiAll)).get();

  private static Phenotype aki1 = new Phe("aki1").restriction(aki, Res.of(1)).get();
  private static Phenotype aki2 = new Phe("aki2").restriction(aki, Res.of(2)).get();
  private static Phenotype aki3 = new Phe("aki3").restriction(aki, Res.of(3)).get();

  @Test
  void test() {
    client.clean();

    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));
    Reference pat3 = client.add(new Pat("p3").birthDate("1951-01-01").gender("male"));
    Reference pat4 = client.add(new Pat("p4").birthDate("1951-01-01").gender("female"));
    Reference pat5 = client.add(new Pat("p5").birthDate("1951-01-01").gender("female"));
    Reference pat6 = client.add(new Pat("p6").birthDate("1951-01-01").gender("male"));
    Reference pat7 = client.add(new Pat("p7").birthDate("1951-01-01").gender("male"));
    Reference pat8 = client.add(new Pat("p8").birthDate("1951-01-01").gender("male"));
    Reference pat9 = client.add(new Pat("p9").birthDate("1951-01-01").gender("male"));

    client.add(
        new Obs("p1a", pat1)
            .code("http://loinc.org", "2160-0")
            .value(31, "mmol/L")
            .date("2020-01-05"));
    client.add(
        new Obs("p1b", pat1)
            .code("http://loinc.org", "2160-0")
            .value(10, "mmol/L")
            .date("2020-01-01"));
    client.add(
        new Obs("p1c", pat1)
            .code("http://loinc.org", "2160-0")
            .value(20, "mmol/L")
            .date("2020-01-02"));
    client.add(
        new Obs("p1d", pat1)
            .code("http://loinc.org", "2160-0")
            .value(30, "mmol/L")
            .date("2020-01-03"));

    client.add(
        new Obs("p2a", pat2)
            .code("http://loinc.org", "2160-0")
            .value(31, "mmol/L")
            .date("2020-01-01"));
    client.add(
        new Obs("p2b", pat2)
            .code("http://loinc.org", "2160-0")
            .value(10, "mmol/L")
            .date("2019-01-01"));
    client.add(
        new Obs("p2c", pat2)
            .code("http://loinc.org", "2160-0")
            .value(20, "mmol/L")
            .date("2019-01-02"));
    client.add(
        new Obs("p2d", pat2)
            .code("http://loinc.org", "2160-0")
            .value(30, "mmol/L")
            .date("2019-01-03"));

    client.add(
        new Obs("p3a", pat3)
            .code("http://loinc.org", "2160-0")
            .value(31, "mmol/L")
            .date("2020-01-01"));
    client.add(
        new Obs("p3b", pat3)
            .code("http://loinc.org", "2160-0")
            .value(10, "mmol/L")
            .date("2019-10-01"));
    client.add(
        new Obs("p3c", pat3)
            .code("http://loinc.org", "2160-0")
            .value(20, "mmol/L")
            .date("2019-11-01"));
    client.add(
        new Obs("p3d", pat3)
            .code("http://loinc.org", "2160-0")
            .value(30, "mmol/L")
            .date("2019-12-01"));

    client.add(
        new Obs("p4a", pat4)
            .code("http://loinc.org", "2160-0")
            .value(31, "mmol/L")
            .date("2020-01-01"));
    client.add(
        new Obs("p4b", pat4)
            .code("http://loinc.org", "2160-0")
            .value(150, "mmol/L")
            .date("2019-10-01"));
    client.add(
        new Obs("p4c", pat4)
            .code("http://loinc.org", "2160-0")
            .value(160, "mmol/L")
            .date("2019-11-01"));
    client.add(
        new Obs("p4d", pat4)
            .code("http://loinc.org", "2160-0")
            .value(355, "mmol/L")
            .date("2019-12-01"));

    client.add(
        new Obs("p5a", pat5)
            .code("http://loinc.org", "2160-0")
            .value(31, "mmol/L")
            .date("2020-01-05"));
    client.add(
        new Obs("p5b", pat5)
            .code("http://loinc.org", "2160-0")
            .value(150, "mmol/L")
            .date("2020-01-01"));
    client.add(
        new Obs("p5c", pat5)
            .code("http://loinc.org", "2160-0")
            .value(160, "mmol/L")
            .date("2020-01-02"));
    client.add(
        new Obs("p5d", pat5)
            .code("http://loinc.org", "2160-0")
            .value(355, "mmol/L")
            .date("2020-01-03"));

    client.add(
        new Obs("p6a", pat6)
            .code("http://loinc.org", "2160-0")
            .value(270, "mmol/L")
            .date("2020-01-04"));
    client.add(
        new Obs("p6b", pat6)
            .code("http://loinc.org", "2160-0")
            .value(200, "mmol/L")
            .date("2020-01-01"));
    client.add(
        new Obs("p6c", pat6)
            .code("http://loinc.org", "2160-0")
            .value(230, "mmol/L")
            .date("2020-01-02"));
    client.add(
        new Obs("p6d", pat6)
            .code("http://loinc.org", "2160-0")
            .value(260, "mmol/L")
            .date("2020-01-03"));

    client.add(
        new Obs("p7a", pat7)
            .code("http://loinc.org", "2160-0")
            .value(100, "mmol/L")
            .date("2020-01-04"));
    client.add(
        new Obs("p7b", pat7)
            .code("http://loinc.org", "2160-0")
            .value(400, "mmol/L")
            .date("2020-01-01"));
    client.add(
        new Obs("p7c", pat7)
            .code("http://loinc.org", "2160-0")
            .value(300, "mmol/L")
            .date("2020-01-02"));
    client.add(
        new Obs("p7d", pat7)
            .code("http://loinc.org", "2160-0")
            .value(200, "mmol/L")
            .date("2020-01-03"));

    client.add(
        new Obs("p8a", pat8)
            .code("http://loinc.org", "2160-0")
            .value(100, "mmol/L")
            .date("2020-01-04"));

    client.add(
        new Obs("p9a", pat9)
            .code("http://loinc.org", "2160-0")
            .value(31, "mmol/L")
            .date("2020-01-05"));
    client.add(
        new Obs("p9b", pat9)
            .code("http://loinc.org", "2160-0")
            .value(10, "mmol/L")
            .date("2017-01-02"));
    client.add(
        new Obs("p9c", pat9)
            .code("http://loinc.org", "2160-0")
            .value(20, "mmol/L")
            .date("2018-01-03"));
    client.add(
        new Obs("p9d", pat9)
            .code("http://loinc.org", "2160-0")
            .value(30, "mmol/L")
            .date("2019-01-04"));

    try {
      ResultSet rs =
          new Que(
                  "config/Default_FHIR_Adapter_Test.yml",
                  crea,
                  refCrea0_7,
                  refCrea8_365,
                  rvRatio,
                  rvRatioGe1_5,
                  rvRatioGe1_5Lt2,
                  rvRatioGe2Lt3,
                  rvRatioGe3,
                  akiSingle,
                  akiAll,
                  aki,
                  aki1,
                  aki2,
                  aki3)
              .pro(akiAll)
              .execute();
      LOGGER.trace(rs.toString());

      assertEquals(
          List.of(Val.of(3), Val.of(0), Val.of(2), Val.of(3)),
          rs.get(pat1.getReference()).getValues("AKIAll", null));

      assertEquals(
          List.of(Val.of(1), Val.of(0), Val.of(2), Val.of(3)),
          rs.get(pat2.getReference()).getValues("AKIAll", null));

      assertEquals(
          List.of(Val.of(1), Val.of(0), Val.of(2), Val.of(2)),
          rs.get(pat3.getReference()).getValues("AKIAll", null));

      assertEquals(
          List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(3)),
          rs.get(pat4.getReference()).getValues("AKIAll", null));

      assertEquals(
          List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(3)),
          rs.get(pat5.getReference()).getValues("AKIAll", null));

      assertEquals(
          List.of(Val.of(1), Val.of(0), Val.of(1), Val.of(1)),
          rs.get(pat6.getReference()).getValues("AKIAll", null));

      assertEquals(
          List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(0)),
          rs.get(pat7.getReference()).getValues("AKIAll", null));

      assertEquals(List.of(Val.of(0)), rs.get(pat8.getReference()).getValues("AKIAll", null));

      assertEquals(
          List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(0)),
          rs.get(pat9.getReference()).getValues("AKIAll", null));

    } catch (InstantiationException e) {
      e.printStackTrace();
    }
  }
}
