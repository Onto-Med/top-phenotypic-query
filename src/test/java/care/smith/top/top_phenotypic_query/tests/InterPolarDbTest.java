package care.smith.top.top_phenotypic_query.tests;

import org.junit.jupiter.api.Test;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class InterPolarDbTest {

  private static final String CONFIG = "config/INTERPOLAR_DB_Adapter.yml";

  private static Phenotype enc = new Phe("enc").itemType(ItemType.ENCOUNTER).string().get();
  private static Phenotype imp = new Phe("imp").restriction(enc, Res.of("IMP")).get();
  private static Phenotype age = new Phe("age").itemType(ItemType.SUBJECT_AGE).string().get();
  private static Phenotype sex = new Phe("sex").itemType(ItemType.SUBJECT_SEX).string().get();

  private static Phenotype crea =
      new Phe("crea", "http://loinc.org", "2160-0", "38483-4")
          .itemType(ItemType.OBSERVATION)
          .titleEn("Creatinine [Mass/volume] in Serum or Plasma")
          .number("mmol/L")
          .get();

  @Test
  void test() throws InstantiationException {
    ResultSet rs =
        new Que(CONFIG, age, sex, enc, imp, crea)
            .inc(crea)
            .inc(imp)
            .pro(enc)
            .pro(crea)
            .pro(age)
            .pro(sex)
            .execute();
    System.out.println(rs);
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
