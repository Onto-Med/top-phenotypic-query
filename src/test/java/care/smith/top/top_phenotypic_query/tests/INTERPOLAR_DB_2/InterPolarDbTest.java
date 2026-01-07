package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterPolarDbTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(InterPolarDbTest.class);

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

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
            .number("mg/dL")
            .get();

    ResultSet rs =
        new Que(CONFIG, age, sex, enc, imp, crea)
            .inc(crea)
            .inc(imp)
            .pro(enc)
            .pro(crea)
            .pro(age)
            .pro(sex)
            .executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/test1.sql")
            .execute();
    LOGGER.trace(rs.toString());

    assertEquals(Set.of("HOSP-0001-E-11", "HOSP-0001-E-33"), rs.getSubjectIds());
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
