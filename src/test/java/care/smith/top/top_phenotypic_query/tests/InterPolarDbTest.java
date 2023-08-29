package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

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

public class InterPolarDbTest {

  private static final String CONFIG = "config/INTERPOLAR_DB_Adapter.yml";

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
            .cleanDB()
            .executeSqlFromResources("INTERPOLAR_DB/db.sql", "INTERPOLAR_DB/aki.sql")
            .execute();

    System.out.println(rs);

    assertEquals(
        List.of(Val.of(3), Val.of(0), Val.of(2), Val.of(3)),
        rs.get("11").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(1), Val.of(0), Val.of(2), Val.of(3)),
        rs.get("22").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(1), Val.of(0), Val.of(2), Val.of(2)),
        rs.get("33").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(3)),
        rs.get("44").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(3)),
        rs.get("55").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(1), Val.of(0), Val.of(1), Val.of(1)),
        rs.get("66").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(0)),
        rs.get("77").getValues("AKIAll", null));

    assertEquals(List.of(Val.of(0)), rs.get("88").getValues("AKIAll", null));

    assertEquals(
        List.of(Val.of(0), Val.of(0), Val.of(0), Val.of(0)),
        rs.get("99").getValues("AKIAll", null));
  }

  @Test
  void test1() throws InstantiationException {
    Phenotype enc = new Phe("enc").itemType(ItemType.ENCOUNTER).string().get();
    Phenotype imp = new Phe("imp").restriction(enc, Res.of("IMP")).get();
    Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).string().get();
    Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();

    Phenotype crea =
        new Phe("crea", "http://loinc.org", "2160-0", "38483-4")
            .itemType(ItemType.OBSERVATION)
            .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
            .number("mmol/L")
            .get();

    ResultSet rs =
        new Que(CONFIG, age, sex, enc, imp, crea)
            .inc(crea)
            .inc(imp)
            .pro(enc)
            .pro(crea)
            .pro(age)
            .pro(sex)
            .cleanDB()
            .executeSqlFromResources("INTERPOLAR_DB/db.sql", "INTERPOLAR_DB/test1.sql")
            .execute();
    System.out.println(rs);

    assertEquals(Set.of("11", "33"), rs.getSubjectIds());
  }

  //  @Test
  //  void test() {
  //    client.clean();
  //
  //    Reference pat1 = client.add(new Pat("p1").birthDate("2000-01-01").gender("male"));
  //    Reference pat2 = client.add(new Pat("p2").birthDate("1990-01-01").gender("female"));
  //    Reference pat3 = client.add(new Pat("p3").birthDate("1980-01-01").gender("male"));
  //    Reference pat4 = client.add(new Pat("p4").birthDate("1970-01-01").gender("female"));
  //    Reference pat5 = client.add(new Pat("p5").birthDate("1960-01-01").gender("male"));
  //    Reference pat6 = client.add(new Pat("p6").birthDate("1940-01-01").gender("female"));
  //
  //    client.add(
  //        new Enc("e1a", pat1)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .period("2000-01-01", "2000-01-03"));
  //    client.add(
  //        new Enc("e1b", pat1)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .period("2001-01-01", "2001-01-03"));
  //
  //    client.add(
  //        new Enc("e2a", pat2)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB")
  //            .period("2000-01-01", "2000-01-04"));
  //    client.add(
  //        new Enc("e2b", pat2)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .period("2000-01-01", "2000-01-03"));
  //
  //    client.add(
  //        new Enc("e3a", pat3)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .period("2000-01-01", "2000-01-04"));
  //    client.add(
  //        new Enc("e3b", pat3)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .period("2001-01-01", "2001-01-03"));
  //
  //    client.add(
  //        new Enc("e4a", pat4)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB")
  //            .period("2000-01-01", "2000-01-04"));
  //    client.add(
  //        new Enc("e4b", pat4)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB")
  //            .period("2001-01-01", "2001-01-04"));
  //
  //    client.add(
  //        new Enc("e5a", pat5)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .period("2000-01-01", "2000-01-03"));
  //    client.add(new Enc("e5b", pat5).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode",
  // "IMP"));
  //
  //    client.add(
  //        new Enc("e6a", pat6)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB")
  //            .period("2000-01-01", "2000-01-04"));
  //    client.add(
  //        new Enc("e6b", pat6)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .period("2000-01-01", "2000-01-04"));
  //    client.add(
  //        new Enc("e6c", pat6)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .start("2020-01-01"));
  //    client.add(new Enc("e6d", pat6).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode",
  // "IMP"));
  //    client.add(
  //        new Enc("e6e", pat6)
  //            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
  //            .period("2000-01-01", "2000-01-03"));
  //
  //    Multimap<String, String> patEnc = HashMultimap.create();
  //
  //    List<Resource> res =
  //        client.execute(
  //
  // "Encounter?subject.gender=female&_include=Encounter:subject&_format=json&_count=20");
  //    for (Resource r : res) {
  //      String type = r.getResourceType().toString();
  //      String id =
  //          path.getString(
  //              r, "id.substring(indexOf('" + type + "')).replaceMatches('/_history.*', '')");
  //      String sbj = path.getString(r, "subject.reference.value");
  //
  //      if ("Encounter".equals(type)) patEnc.put(sbj, id);
  //
  //      System.out.println(path.getString(r, "gender.value"));
  //      System.out.println(path.getDateTime(r, "birthDate"));
  //    }
  //
  //    System.out.println(patEnc);
  //  }
}
