package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

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
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AKITest {

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

  @Test
  void aki() throws InstantiationException {
    Phenotype crea =
        new Phe("crea", "http://loinc.org", "2160-0")
            .itemType(ItemType.OBSERVATION)
            .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
            .number("mmol/L")
            .get();

    Phenotype refCrea0_7 =
        new Phe("refCrea0_7").expression(Min.of(RefValues.of(Exp.of(crea), Exp.of(7)))).get();

    Phenotype refCrea8_365 =
        new Phe("refCrea8_365")
            .expression(Median.of(RefValues.of(Exp.of(crea), Exp.of(8), Exp.of(365))))
            .get();

    Phenotype rvRatio =
        new Phe("rvRatio")
            .expression(
                Divide.of(
                    Exp.of(crea),
                    If.of(Exists.of(refCrea0_7), Exp.of(refCrea0_7), Exp.of(refCrea8_365))))
            .get();

    Phenotype rvRatioGe1_5 = new Phe("rvRatioGe1_5").restriction(rvRatio, Res.ge(1.5)).get();
    Phenotype rvRatioGe1_5Lt2 =
        new Phe("rvRatioGe1_5Lt2").restriction(rvRatio, Res.geLt(1.5, 2)).get();
    Phenotype rvRatioGe2Lt3 = new Phe("rvRatioGe2Lt3").restriction(rvRatio, Res.geLt(2, 3)).get();
    Phenotype rvRatioGe3 = new Phe("rvRatioGe3").restriction(rvRatio, Res.ge(3)).get();

    Phenotype akiSingle =
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

    Phenotype akiAll = new Phe("AKIAll").expression(ForEach.of(crea, akiSingle)).get();

    Phenotype aki = new Phe("AKI").expression(Max.of(akiAll)).get();

    Phenotype aki1 = new Phe("aki1").restriction(aki, Res.of(1)).get();
    Phenotype aki2 = new Phe("aki2").restriction(aki, Res.of(2)).get();
    Phenotype aki3 = new Phe("aki3").restriction(aki, Res.of(3)).get();

    ResultSet rs =
        new Que(
                CONFIG,
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
            .executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/aki.sql")
            .execute();

    assertEquals(
        List.of(Val.of(3), Val.of(0), Val.of(2), Val.of(3)),
        rs.get("HOSP-0001-E-11").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(1), Val.of(0), Val.of(2), Val.of(3)),
        rs.get("HOSP-0002-E-22").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(1), Val.of(0), Val.of(2), Val.of(2)),
        rs.get("HOSP-0003-E-33").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(3)),
        rs.get("HOSP-0004-E-44").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(3)),
        rs.get("HOSP-0005-E-55").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(1), Val.of(0), Val.of(1), Val.of(1)),
        rs.get("HOSP-0006-E-66").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(0)),
        rs.get("HOSP-0007-E-77").getValues("AKIAll", null));

    assertEquals(List.of(Val.of(0)), rs.get("HOSP-0008-E-88").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(0)),
        rs.get("HOSP-0009-E-99").getValues("AKIAll", null));
  }
}
