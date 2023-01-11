package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Entity;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.And;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.MinTrue;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class DelirTest {

  Phenotype expIcd =
      new Phe("expIcd", "http://fhir.de/CodeSystem/bfarm/icd-10-gm", "F05.-", "F05.0", "F05.1")
          .itemType(ItemType.CONDITION)
          .titleDe("Expliziter ICD-10-Code")
          .titleEn("Explicit ICD-10 code")
          .bool()
          .get();
  Phenotype impIcd =
      new Phe("impIcd", "http://fhir.de/CodeSystem/bfarm/icd-10-gm", "A81.2", "A51.2", "G92.-")
          .itemType(ItemType.CONDITION)
          .titleDe("Impliziter ICD-10-Code nach Kim et al.")
          .titleEn("Implicit ICD-10 code by to Kim et al.")
          .bool()
          .get();
  Phenotype infect =
      new Phe("infect", "http://fhir.de/CodeSystem/bfarm/icd-10-gm", "G00.0", "G00.1", "G00.2")
          .itemType(ItemType.CONDITION)
          .titleDe("Infektion")
          .titleEn("Infection")
          .bool()
          .get();
  Phenotype cognit =
      new Phe("cognit", "http://fhir.de/CodeSystem/bfarm/icd-10-gm", "F00.-", "F01.-", "F02.-")
          .itemType(ItemType.CONDITION)
          .titleDe("Kognitive Beeinträchtigung")
          .titleEn("Cognitive impairment")
          .bool()
          .get();
  Phenotype age =
      new Phe("age", "http://loinc.org", "30525-0")
          .itemType(ItemType.OBSERVATION)
          .titleDe("Alter")
          .titleEn("Age")
          .number("a")
          .get();
  Phenotype above80 =
      new Phe("above80")
          .titleDe("Über 80 Jahre")
          .titleEn("Above 80 years")
          .restriction(age, Res.ge(80))
          .get();
  Phenotype antiPsy =
      new Phe("antiPsy", "http://fhir.de/CodeSystem/bfarm/atc", "N05A")
          .itemType(ItemType.MEDICATION_ADMINISTRATION)
          .titleDe("Antipsychotika Medikation")
          .titleEn("Anti-psychotic medication")
          .bool()
          .get();
  Phenotype sodium =
      new Phe("sodium", "http://loinc.org", "2951-2", "2947-0", "32717-1")
          .itemType(ItemType.OBSERVATION)
          .titleDe("Natriumspiegel")
          .titleEn("Sodium level")
          .number("mmol/L")
          .get();
  Phenotype lt135 =
      new Phe("lt135")
          .titleDe("< 135 mg/dL")
          .titleEn("< 135 mg/dL")
          .restriction(sodium, Res.lt(58.73))
          .get();
  Phenotype gt145 =
      new Phe("gt145")
          .titleDe("> 145 mg/dL")
          .titleEn("> 145 mg/dL")
          .restriction(sodium, Res.gt(63.08))
          .get();
  Phenotype op =
      new Phe("op", "http://fhir.de/CodeSystem/bfarm/ops", "5-")
          .itemType(ItemType.PROCEDURE)
          .titleDe("Operation")
          .titleEn("Operation")
          .bool()
          .get();
  Phenotype fact2of3 =
      new Phe("fact2of3")
          .titleDe("2/3 Faktoren")
          .titleEn("2/3 factors")
          .descriptionDe(
              "Dies ist ein Hilfsphänotyp zur Überprüfung, ob mindestens 2 von 3 Kriterien erfüllt sind.")
          .descriptionEn(
              "This is a helper phenotype to check whether at least 2 of 3 criteria are fulfilled.")
          .expression(
              MinTrue.of(
                  Exp.of(2), Exp.of(above80), Exp.of(infect), Or.of(Exp.of(lt135), Exp.of(gt145))))
          .get();
  Phenotype algA =
      new Phe("algA")
          .titleDe("Basisalgorithmus A")
          .titleEn("Basic algorithm A")
          .synonymDe("Delir Basisalgorithmus A")
          .synonymEn("Delirium basic algorithm A")
          .descriptionDe(
              "Algorithmus zur Identifikation von Delir. Der Algorithmus basiert ausschließlich auf ICD-10-Codes. Es ist zu beachten, dass die Sensitivität gemäß Literatur nur zwischen 18% bis 30% liegt - bei guter Spezifität.")
          .descriptionEn(
              "Algorithm for the identification of delirium. The algorithm is based exclusively on ICD-10 codes. It should be noted that the sensitivity according to literature is only between 18% to 30% - with good specificity.")
          .expression(Exp.of(expIcd))
          .get();
  Phenotype algB =
      new Phe("algB")
          .titleDe("Basisalgorithmus B")
          .titleEn("Basic algorithm B")
          .synonymDe("Delir Basisalgorithmus B")
          .synonymEn("Delirium basic algorithm B")
          .descriptionDe(
              "Delir Algorithmus ausschließlich basierend auf ICD-10-Codes, wobei explizite und implizite ICD-10-Codes nach Kim et al. zum Einsatz kommen. Zu beachten ist, dass die Sensitivität gemäß Literatur nur zwischen 18% bis 30% liegt - bei guter Spezifität.")
          .descriptionEn(
              "Delirium algorithm based solely on ICD-10 codes, using explicit and implicit ICD-10 codes according to Kim et al. It should be noted that the sensitivity according to the literature is only between 18% and 30% - with good specificity.")
          .expression(Or.of(Exp.of(expIcd), Exp.of(impIcd)))
          .get();
  Phenotype extAlg =
      new Phe("extAlg")
          .titleDe("Erweiterter Algorithmus")
          .titleEn("Extended algorithm")
          .synonymDe("Delir erweiterter Algorithmus")
          .synonymEn("Delirium extended algorithm")
          .descriptionDe(
              "Komplexer Delir Algorithmus, der verschiedenste Datenkategorien beinhaltet. Durch Hinzunahme weiterer ICD-10-Codes für kognitive Beeinträchtigung, sowie Einbezug weiterer NICE-Risiko-Score Datenpunkte, soll die Sensitivität erhöht werden.")
          .descriptionEn(
              "Complex delirium algorithm that includes a wide range of data categories. The addition of further ICD-10 codes for cognitive impairment, as well as the inclusion of further NICE risk score data points, should increase the sensitivity.")
          .expression(
              Or.of(
                  Exp.of(expIcd),
                  And.of(Exp.of(antiPsy), Exp.of(op)),
                  And.of(Exp.of(impIcd), Or.of(Exp.of(op), Exp.of(antiPsy))),
                  And.of(Exp.of(cognit), Exp.of(fact2of3))))
          .get();

  Phenotype antiPsyAndOp =
      new Phe("antiPsyAndOp").expression(And.of(Exp.of(antiPsy), Exp.of(op))).get();
  Phenotype impIcdAndAntiPsyOrOp =
      new Phe("impIcdAndAntiPsyOrOp")
          .expression(And.of(Exp.of(impIcd), Or.of(Exp.of(op), Exp.of(antiPsy))))
          .get();
  Phenotype cognitAnd2of3 =
      new Phe("cognitAnd2of3").expression(And.of(Exp.of(cognit), Exp.of(fact2of3))).get();

  Entity[] entities = {
    expIcd,
    impIcd,
    infect,
    cognit,
    age,
    above80,
    antiPsy,
    sodium,
    lt135,
    gt145,
    op,
    fact2of3,
    algA,
    algB,
    extAlg,
    antiPsyAndOp,
    impIcdAndAntiPsyOrOp,
    cognitAnd2of3
  };

  private ResultSet execute(Phenotype p) throws InstantiationException {
    QueryCriterion cri =
        new QueryCriterion()
            .inclusion(true)
            .defaultAggregationFunctionId("Last")
            .subjectId(p.getId());

    Query query = new Query().addCriteriaItem(cri);

    URL configFile =
        Thread.currentThread()
            .getContextClassLoader()
            .getResource("config/Delir_SQL_Adapter_Test.yml");

    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, entities, adapter);
    ResultSet rs = pf.execute();
    adapter.close();

    return rs;
  }

  @Test
  public void testAlgA() throws InstantiationException {
    ResultSet rs = execute(algA);
    assertEquals(Set.of("1", "5", "8"), rs.getSubjectIds());
  }

  @Test
  public void testExpIcd() throws InstantiationException {
    ResultSet rs = execute(expIcd);
    assertEquals(Set.of("1", "5", "8"), rs.getSubjectIds());
  }

  @Test
  public void testAlgB() throws InstantiationException {
    ResultSet rs = execute(algB);
    assertEquals(Set.of("1", "2", "5", "6", "8", "10", "11"), rs.getSubjectIds());
  }

  @Test
  public void test2of3() throws InstantiationException {
    ResultSet rs = execute(fact2of3);
    assertEquals(Set.of("2", "3", "4", "10"), rs.getSubjectIds());
  }

  @Test
  public void testExtAlg() throws InstantiationException {
    ResultSet rs = execute(extAlg);
    System.out.println(rs);
    assertEquals(Set.of("1", "2", "3", "4", "5", "8", "11"), rs.getSubjectIds());
  }

  @Test
  public void testAntiPsyAndOp() throws InstantiationException {
    ResultSet rs = execute(antiPsyAndOp);
    assertEquals(Set.of("1", "3", "5"), rs.getSubjectIds());
  }

  @Test
  public void testImpIcdAndAntiPsyOrOp() throws InstantiationException {
    ResultSet rs = execute(impIcdAndAntiPsyOrOp);
    assertEquals(Set.of("2", "11"), rs.getSubjectIds());
  }

  @Test
  public void testCognitAnd2of3() throws InstantiationException {
    ResultSet rs = execute(cognitAnd2of3);
    assertEquals(Set.of("4"), rs.getSubjectIds());
  }
}
