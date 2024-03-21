package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.Category;
import care.smith.top.model.DataType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Switch;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Avg;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Max;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Add;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Multiply;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Round;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Subtract;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Sum;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.And;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Eq;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Le;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Empty;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.In;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.DefaultSqlWriter;
import care.smith.top.top_phenotypic_query.util.builder.Cat;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SES3Test {

  private static final DataAdapterConfig CONFIG =
      DataAdapterConfig.getInstanceFromResource("config/Default_SQL_Adapter.yml");

  private static final DefaultSqlWriter WRITER = new DefaultSqlWriter(CONFIG);

  /////////////////////////// BILDUNG ///////////////////////////

  private static Category bildungKategorie =
      new Cat("bildungKategorie").titleDe("Bildung").titleEn("Education").get();

  private static Category bildungParameterKategorie =
      new Cat("bildungParameterKategorie")
          .superCategory(bildungKategorie)
          .titleDe("Parameter")
          .titleEn("Parameters")
          .get();

  private static Phenotype schule =
      new Phe("schule", "SOZIO", "F0041")
          .category(bildungParameterKategorie)
          .titleDe("Schulabschluss")
          .titleEn("School-leaving qualification")
          .descriptionDe("Welchen hoechsten allgemein bildenden Schulabschluss haben Sie?")
          .descriptionEn("What is your highest general school-leaving qualification?")
          .number()
          .get();
  private static Phenotype schule1 =
      new Phe("schule1")
          .titleEn("1")
          .descriptionDe("Noch Schueler/in ohne Abschluss")
          .descriptionEn("Still a pupil without a qualification")
          .restriction(schule, Res.of(1))
          .get();
  private static Phenotype schule2 =
      new Phe("schule2")
          .titleEn("2")
          .descriptionDe(
              "Von der Schule abgegangen ohne Hauptschulabschluss (ohne Volksschulabschluss)")
          .descriptionEn("Left school without a secondary school-leaving qualification")
          .restriction(schule, Res.of(2))
          .get();
  private static Phenotype schule3 =
      new Phe("schule3")
          .titleEn("3")
          .descriptionDe("Hauptschulabschluss (Volksschulabschluss)")
          .descriptionEn("Secondary school-leaving qualification")
          .restriction(schule, Res.of(3))
          .get();
  private static Phenotype schule4 =
      new Phe("schule4")
          .titleEn("4")
          .descriptionDe("Realschulabschluss (Mittlere Reife)")
          .descriptionEn("Intermediate school-leaving qualification")
          .restriction(schule, Res.of(4))
          .get();
  private static Phenotype schule5 =
      new Phe("schule5")
          .titleEn("5")
          .descriptionDe(
              "Abschluss der Polytechnischen Oberschule 10. Klasse (vor 1965: 8. Klasse)")
          .descriptionEn(
              "Qualification from polytechnic secondary school 10th class (before 1965: 8th class)")
          .restriction(schule, Res.of(5))
          .get();
  private static Phenotype schule6 =
      new Phe("schule6")
          .titleEn("6")
          .descriptionDe("Fachhochschulreife, Abschluss Fachoberschule")
          .descriptionEn(
              "Entrance qualification for universities of applied sciences, qualification from technical college")
          .restriction(schule, Res.of(6))
          .get();
  private static Phenotype schule7 =
      new Phe("schule7")
          .titleEn("7")
          .descriptionDe(
              "Allgemeine/fachgebundene Hochschulreife/Abitur (Gymnasium/EOS, auch EOS mit Lehre)")
          .descriptionEn("General or specialized entrance qualification for universities")
          .restriction(schule, Res.of(7))
          .get();
  private static Phenotype schule12 =
      new Phe("schule12").titleEn("1|2").restriction(schule, Res.of(1, 2)).get();
  private static Phenotype schule123 =
      new Phe("schule123").titleEn("1|2|3").restriction(schule, Res.of(1, 2, 3)).get();
  private static Phenotype schule45 =
      new Phe("schule45").titleEn("4|5").restriction(schule, Res.of(4, 5)).get();
  private static Phenotype schule67 =
      new Phe("schule67").titleEn("6|7").restriction(schule, Res.of(6, 7)).get();

  private static Phenotype ausbildung1 =
      new Phe("ausbildung1", "SOZIO", "F0045")
          .category(bildungParameterKategorie)
          .titleDe("Ausbildungsabschluss 1")
          .titleEn("Vocational qualification 1")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Noch in beruflicher Ausbildung (Auszubildende/r, Student/in)")
          .descriptionEn(
              "What vocational qualification do you have? - Still in vocational training (trainee, student)")
          .number()
          .get();

  private static Phenotype ausbildung2 =
      new Phe("ausbildung2", "SOZIO", "F0046")
          .category(bildungParameterKategorie)
          .titleDe("Ausbildungsabschluss 2")
          .titleEn("Vocational qualification 2")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Keinen beruflichen Abschluss und bin nicht in beruflicher Ausbildung")
          .descriptionEn(
              "What vocational qualification do you have? - No vocational qualification and I am not in vocational training")
          .number()
          .get();

  private static Phenotype ausbildung3 =
      new Phe("ausbildung3", "SOZIO", "F0047")
          .category(bildungParameterKategorie)
          .titleDe("Ausbildungsabschluss 3")
          .titleEn("Vocational qualification 3")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Beruflich-betriebliche Berufsausbildung (Lehre) abgeschlossen")
          .descriptionEn(
              "What vocational qualification do you have? - Completed in-company vocational training (apprenticeship)")
          .number()
          .get();

  private static Phenotype ausbildung4 =
      new Phe("ausbildung4", "SOZIO", "F0048")
          .category(bildungParameterKategorie)
          .titleDe("Ausbildungsabschluss 4")
          .titleEn("Vocational qualification 4")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Beruflich-schulische Ausbildung (Berufsfachschule, Handelsschule) abgeschlossen")
          .descriptionEn(
              "What vocational qualification do you have? - Completed school-based vocational training (vocational school, commercial school)")
          .number()
          .get();

  private static Phenotype ausbildung5 =
      new Phe("ausbildung5", "SOZIO", "F0049")
          .category(bildungParameterKategorie)
          .titleDe("Ausbildungsabschluss 5")
          .titleEn("Vocational qualification 5")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Ausbildung an einer Fachschule, Meister-, Technikerschule, Berufs- oder Fachakademie abgeschlossen")
          .descriptionEn(
              "What vocational qualification do you have? - Completed training at a college, master school, technical school, vocational or specialised academy")
          .number()
          .get();

  private static Phenotype ausbildung6 =
      new Phe("ausbildung6", "SOZIO", "F0050")
          .category(bildungParameterKategorie)
          .titleDe("Ausbildungsabschluss 6")
          .titleEn("Vocational qualification 6")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Fachhochschulabschluss")
          .descriptionEn(
              "What vocational qualification do you have? - University of Applied Sciences qualification")
          .number()
          .get();

  private static Phenotype ausbildung7 =
      new Phe("ausbildung7", "SOZIO", "F0051")
          .category(bildungParameterKategorie)
          .titleDe("Ausbildungsabschluss 7")
          .titleEn("Vocational qualification 7")
          .descriptionDe("Welchen beruflichen Ausbildungsabschluss haben Sie? - Hochschulabschluss")
          .descriptionEn("What vocational qualification do you have? - University qualification")
          .number()
          .get();

  private static Phenotype ausbildung =
      new Phe("ausbildung")
          .category(bildungKategorie)
          .titleDe("Ausbildungsabschluss")
          .titleEn("Vocational qualification")
          .descriptionDe("Hoechster Ausbildungsabschluss")
          .descriptionEn("Highest vocational qualification")
          .expression(
              Switch.of(
                  Eq.of(ausbildung7, 1),
                  Exp.of(7),
                  Eq.of(ausbildung6, 1),
                  Exp.of(6),
                  Eq.of(ausbildung5, 1),
                  Exp.of(5),
                  Eq.of(ausbildung4, 1),
                  Exp.of(4),
                  Eq.of(ausbildung3, 1),
                  Exp.of(3),
                  Eq.of(ausbildung2, 1),
                  Exp.of(2),
                  Eq.of(ausbildung1, 1),
                  Exp.of(1)),
              DataType.NUMBER)
          .get();
  private static Phenotype ausbildungS1 =
      new Phe("ausbildungS1")
          .titleEn("1")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Noch in beruflicher Ausbildung (Auszubildende/r, Student/in)")
          .descriptionEn(
              "What vocational qualification do you have? - Still in vocational training (trainee, student)")
          .restriction(ausbildung, Res.of(1))
          .get();
  private static Phenotype ausbildungS2 =
      new Phe("ausbildungS2")
          .titleEn("2")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Keinen beruflichen Abschluss und bin nicht in beruflicher Ausbildung")
          .descriptionEn(
              "What vocational qualification do you have? - No vocational qualification and I am not in vocational training")
          .restriction(ausbildung, Res.of(2))
          .get();
  private static Phenotype ausbildungS3 =
      new Phe("ausbildungS3")
          .titleEn("3")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Beruflich-betriebliche Berufsausbildung (Lehre) abgeschlossen")
          .descriptionEn(
              "What vocational qualification do you have? - Completed in-company vocational training (apprenticeship)")
          .restriction(ausbildung, Res.of(3))
          .get();
  private static Phenotype ausbildungS4 =
      new Phe("ausbildungS4")
          .titleEn("4")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Beruflich-schulische Ausbildung (Berufsfachschule, Handelsschule) abgeschlossen")
          .descriptionEn(
              "What vocational qualification do you have? - Completed school-based vocational training (vocational school, commercial school)")
          .restriction(ausbildung, Res.of(4))
          .get();
  private static Phenotype ausbildungS5 =
      new Phe("ausbildungS5")
          .titleEn("5")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Ausbildung an einer Fachschule, Meister-, Technikerschule, Berufs- oder Fachakademie abgeschlossen")
          .descriptionEn(
              "What vocational qualification do you have? - Completed training at a college, master school, technical school, vocational or specialised academy")
          .restriction(ausbildung, Res.of(5))
          .get();
  private static Phenotype ausbildungS6 =
      new Phe("ausbildungS6")
          .titleEn("6")
          .descriptionDe(
              "Welchen beruflichen Ausbildungsabschluss haben Sie? - Fachhochschulabschluss")
          .descriptionEn(
              "What vocational qualification do you have? - University of Applied Sciences qualification")
          .restriction(ausbildung, Res.of(6))
          .get();
  private static Phenotype ausbildungS7 =
      new Phe("ausbildungS7")
          .titleEn("7")
          .descriptionDe("Welchen beruflichen Ausbildungsabschluss haben Sie? - Hochschulabschluss")
          .descriptionEn("What vocational qualification do you have? - University qualification")
          .restriction(ausbildung, Res.of(7))
          .get();
  private static Phenotype ausbildung12 =
      new Phe("ausbildung12").titleEn("1|2").restriction(ausbildung, Res.of(1, 2)).get();
  private static Phenotype ausbildung345 =
      new Phe("ausbildung345").titleEn("3|4|5").restriction(ausbildung, Res.of(3, 4, 5)).get();

  private static Phenotype bildungSES =
      new Phe("bildungSES")
          .category(bildungKategorie)
          .titleDe("Bildung (SES)")
          .titleEn("Education (SES)")
          .descriptionDe("Schulische und berufliche Qualifikation (Individualmerkmal)")
          .descriptionEn("School and professional qualification (individual characteristic)")
          .expression(
              Switch.of(
                  And.of(schule12, ausbildung12),
                  Exp.of(1.0),
                  And.of(schule3, ausbildung12),
                  Exp.of(1.7),
                  And.of(schule45, ausbildung12),
                  Exp.of(2.8),
                  And.of(schule123, ausbildung345),
                  Exp.of(3.0),
                  And.of(schule45, ausbildung345),
                  Exp.of(3.6),
                  And.of(schule67, ausbildung12),
                  Exp.of(3.7),
                  And.of(schule67, ausbildung345),
                  Exp.of(4.8),
                  And.of(schule67, ausbildungS6),
                  Exp.of(6.1),
                  And.of(schule67, ausbildungS7),
                  Exp.of(7.0),
                  And.of(schule12, ausbildungS6),
                  Exp.of(3.55),
                  And.of(schule12, ausbildungS7),
                  Exp.of(4.0),
                  And.of(schule3, ausbildungS6),
                  Exp.of(4.55),
                  And.of(schule3, ausbildungS7),
                  Exp.of(5.0),
                  And.of(schule45, ausbildungS6),
                  Exp.of(4.85),
                  And.of(schule45, ausbildungS7),
                  Exp.of(5.3),
                  Exp.of(ausbildung12),
                  Exp.of(1.0),
                  Exp.of(ausbildung345),
                  Exp.of(3.0),
                  Exp.of(ausbildungS6),
                  Exp.of(6.1),
                  Exp.of(ausbildungS7),
                  Exp.of(7.0),
                  Exp.of(schule12),
                  Exp.of(1.0),
                  Exp.of(schule3),
                  Exp.of(1.7),
                  Exp.of(schule45),
                  Exp.of(2.8),
                  Exp.of(schule67),
                  Exp.of(3.7)),
              DataType.NUMBER)
          .get();

  private static Phenotype bildungSES_1_0 =
      new Phe("bildungSES_1_0")
          .titleEn("1.0")
          .descriptionDe("Kein schulischer Abschluss und kein beruflicher Abschluss")
          .descriptionEn("No school-leaving qualification and no vocational qualification")
          .restriction(bildungSES, Res.of(1.0))
          .get();
  private static Phenotype bildungSES_1_7 =
      new Phe("bildungSES_1_7")
          .titleEn("1.7")
          .descriptionDe("Hauptschulabschluss und kein beruflicher Abschluss")
          .descriptionEn("Secondary school-leaving qualification and no vocational qualification")
          .restriction(bildungSES, Res.of(1.7))
          .get();
  private static Phenotype bildungSES_2_8 =
      new Phe("bildungSES_2_8")
          .titleEn("2.8")
          .descriptionDe("Realschul- oder POS-Abschluss und kein beruflicher Abschluss")
          .descriptionEn(
              "Intermediate school-leaving qualification and no vocational qualification")
          .restriction(bildungSES, Res.of(2.8))
          .get();
  private static Phenotype bildungSES_3_0 =
      new Phe("bildungSES_3_0")
          .titleEn("3.0")
          .descriptionDe(
              "Kein schulischer Abschluss oder Hauptschulabschluss und Ausbildung/Lehre/Fachschule")
          .descriptionEn(
              "No school-leaving qualification or lower secondary school-leaving qualification and training/apprenticeship/technical college")
          .restriction(bildungSES, Res.of(3.0))
          .get();
  private static Phenotype bildungSES_3_6 =
      new Phe("bildungSES_3_6")
          .titleEn("3.6")
          .descriptionDe("Realschul- oder POS-Abschluss und Ausbildung/Lehre/Fachschule")
          .descriptionEn(
              "Intermediate school-leaving qualification and training/apprenticeship/technical college")
          .restriction(bildungSES, Res.of(3.6))
          .get();
  private static Phenotype bildungSES_3_7 =
      new Phe("bildungSES_3_7")
          .titleEn("3.7")
          .descriptionDe("FH-Reife oder Abitur und kein beruflicher Abschluss")
          .descriptionEn(
              "Entrance qualification for universities of applied sciences or universities and no vocational qualification")
          .restriction(bildungSES, Res.of(3.7))
          .get();
  private static Phenotype bildungSES_4_55 =
      new Phe("bildungSES_4_55")
          .titleEn("4.55")
          .descriptionDe("Hauptschulabschluss und Fachhochschulabschluss")
          .descriptionEn(
              "Secondary school-leaving qualification and university of applied sciences qualification")
          .restriction(bildungSES, Res.of(4.55))
          .get();
  private static Phenotype bildungSES_4_8 =
      new Phe("bildungSES_4_8")
          .titleEn("4.8")
          .descriptionDe("FH-Reife oder Abitur und Ausbildung/Lehre/Fachschule")
          .descriptionEn(
              "Entrance qualification for universities of applied sciences or universities and training/apprenticeship/technical college")
          .restriction(bildungSES, Res.of(4.8))
          .get();
  private static Phenotype bildungSES_4_85 =
      new Phe("bildungSES_4_85")
          .titleEn("4.85")
          .descriptionDe("Realschul- oder POS-Abschluss und Fachhochschulabschluss")
          .descriptionEn(
              "Intermediate school-leaving qualification and university of applied sciences qualification")
          .restriction(bildungSES, Res.of(4.85))
          .get();
  private static Phenotype bildungSES_5_0 =
      new Phe("bildungSES_5_0")
          .titleEn("5.0")
          .descriptionDe("Hauptschulabschluss und Hochschulabschluss")
          .descriptionEn("Secondary school-leaving qualification and university qualification")
          .restriction(bildungSES, Res.of(5.0))
          .get();
  private static Phenotype bildungSES_5_3 =
      new Phe("bildungSES_5_3")
          .titleEn("5.3")
          .descriptionDe("Realschul- oder POS-Abschluss und Hochschulabschluss")
          .descriptionEn("Intermediate school-leaving qualification and university qualification")
          .restriction(bildungSES, Res.of(5.3))
          .get();
  private static Phenotype bildungSES_6_1 =
      new Phe("bildungSES_6_1")
          .titleEn("6.1")
          .descriptionDe("FH-Reife oder Abitur und Bachelor oder Diplom Fachhochschule")
          .descriptionEn(
              "Entrance qualification for universities of applied sciences or universities and bachelor or university of applied sciences qualification")
          .restriction(bildungSES, Res.of(6.1))
          .get();
  private static Phenotype bildungSES_7_0 =
      new Phe("bildungSES_7_0")
          .titleEn("7.0")
          .descriptionDe("FH-Reife oder Abitur und Master/Magister/Diplom, Promotion")
          .descriptionEn(
              "Entrance qualification for universities of applied sciences or universities and university qualification, doctorate")
          .restriction(bildungSES, Res.of(7.0))
          .get();

  /////////////////////////// EIGENER BERUF ///////////////////////////

  private static Category berufKategorie =
      new Cat("berufKategorie").titleDe("Beruf").titleEn("Profession").get();

  private static Category berufParameterKategorie =
      new Cat("berufParameterKategorie")
          .superCategory(berufKategorie)
          .titleDe("Parameter")
          .titleEn("Parameters")
          .get();

  private static Phenotype erwerbstaetigkeit =
      new Phe("erwerbstaetigkeit", "SOZIO", "F0055")
          .category(berufParameterKategorie)
          .titleDe("Erwerbstaetigkeit")
          .titleEn("Employment")
          .descriptionDe(
              "Sind Sie zurzeit erwerbstaetig? Unter Erwerbstaetigkeit wird jede bezahlte bzw. mit einem Einkommen verbundene Taetigkeit verstanden, egal welchen zeitlichen Umfang sie hat.")
          .descriptionEn(
              "Are you currently employed? Employment is defined as any paid or income-generating activity, regardless of the amount of time it takes.")
          .number()
          .get();
  private static Phenotype erwerbstaetig =
      new Phe("erwerbstaetig")
          .titleDe("Ja")
          .titleEn("Yes")
          .descriptionDe(
              "1 - Ja, ich bin vollzeiterwerbstaetig mit 35 Stunden und mehr/Woche oder 2 - Ja, ich bin teilzeiterwerbstaetig mit 15 bis 34 Stunden/Woche")
          .descriptionEn(
              "1 - Yes, I am in full-time employment with 35 hours or more/week or 2 - Yes, I am in part-time employment with 15 to 34 hours/week")
          .restriction(erwerbstaetigkeit, Res.of(1, 2))
          .get();

  private static Phenotype fruehereErwerbstaetigkeit =
      new Phe("fruehereErwerbstaetigkeit", "SOZIO", "F0070")
          .category(berufParameterKategorie)
          .titleDe("Fruehere Erwerbstaetigkeit")
          .titleEn("Former employment")
          .descriptionDe(
              "Waren Sie frueher einmal voll- oder teilzeiterwerbstaetig mit einer Wochenarbeitszeit von mindestens 15 Wochenstunden?")
          .descriptionEn(
              "Were you previously employed full-time or part-time with a working week of at least 15 hours?")
          .number()
          .get();
  private static Phenotype frueherErwerbstaetig =
      new Phe("frueherErwerbstaetig")
          .titleDe("Ja")
          .titleEn("Yes")
          .restriction(fruehereErwerbstaetigkeit, Res.of(1))
          .get();

  private static Phenotype berufEigener =
      new Phe("berufEigener", "SOZIO", "F0072")
          .category(berufParameterKategorie)
          .titleDe("Eigener Beruf")
          .titleEn("Own profession")
          .descriptionDe("Zu welcher Gruppe gehoert Ihr Beruf?")
          .descriptionEn("To which group does your profession belong?")
          .string()
          .get();
  private static Phenotype berufEigener_1_0 =
      new Phe("berufEigener_1_0")
          .titleEn("1.0")
          .descriptionDe(
              "Landwirt o. n. A., Landwirt unter 10 ha, Genossenschaftsbauer (ehem. LPG)")
          .descriptionEn(
              "Farmer n/a, farmer under 10 ha, cooperative farmer (former agricultural production cooperative)")
          .restriction(berufEigener, Res.of("A", "A1", "A3"))
          .get();
  private static Phenotype berufEigener_1_1 =
      new Phe("berufEigener_1_1")
          .titleEn("1.1")
          .descriptionDe("Landwirt 10 ha und mehr")
          .descriptionEn("Farmer 10 ha and more")
          .restriction(berufEigener, Res.of("A2"))
          .get();
  private static Phenotype berufEigener_1_3 =
      new Phe("berufEigener_1_3")
          .titleEn("1.3")
          .descriptionDe("Ungelernter Arbeiter")
          .descriptionEn("Unskilled worker")
          .restriction(berufEigener, Res.of("F1"))
          .get();
  private static Phenotype berufEigener_1_8 =
      new Phe("berufEigener_1_8")
          .titleEn("1.8")
          .descriptionDe("Angelernter Arbeiter")
          .descriptionEn("Semi-skilled worker")
          .restriction(berufEigener, Res.of("F2"))
          .get();
  private static Phenotype berufEigener_1_9 =
      new Phe("berufEigener_1_9")
          .titleEn("1.9")
          .descriptionDe("Arbeiter o. n. A.")
          .descriptionEn("Worker n/a")
          .restriction(berufEigener, Res.of("F"))
          .get();
  private static Phenotype berufEigener_2_0 =
      new Phe("berufEigener_2_0")
          .titleEn("2.0")
          .descriptionDe("Vorarbeiter, Kolonnenfuehrer")
          .descriptionEn("Foreman, column leader")
          .restriction(berufEigener, Res.of("F4"))
          .get();
  private static Phenotype berufEigener_2_1 =
      new Phe("berufEigener_2_1")
          .titleEn("2.1")
          .descriptionDe("Facharbeiter")
          .descriptionEn("Skilled worker")
          .restriction(berufEigener, Res.of("F3"))
          .get();
  private static Phenotype berufEigener_2_4 =
      new Phe("berufEigener_2_4")
          .titleEn("2.4")
          .descriptionDe("Meister, Polier, Brigadier, Angestellter mit ausfuehrender Taetigkeit")
          .descriptionEn("Master, foreman, brigadier, employee with executive activity")
          .restriction(berufEigener, Res.of("E1", "F5"))
          .get();
  private static Phenotype berufEigener_2_9 =
      new Phe("berufEigener_2_9")
          .titleEn("2.9")
          .descriptionDe(
              "Beamter im einfachen Dienst, Auszubildender, mithelfender Familienangehoeriger")
          .descriptionEn("Civil servant in the civil service, trainee, assisting family member")
          .restriction(berufEigener, Res.of("D1", "G", "G1", "G2", "G3", "H"))
          .get();
  private static Phenotype berufEigener_3_5 =
      new Phe("berufEigener_3_5")
          .titleEn("3.5")
          .descriptionDe("Selbstaendiger, keine Mitarbeiter")
          .descriptionEn("Self-employed, no employees")
          .restriction(berufEigener, Res.of("C1"))
          .get();
  private static Phenotype berufEigener_3_6 =
      new Phe("berufEigener_3_6")
          .titleEn("3.6")
          .descriptionDe(
              "Angestellter mit qualifizierter Taetigkeit, Selbstaendiger mit 1 bis 4 Mitarbeitern")
          .descriptionEn(
              "Employee with qualified activity, self-employed person with 1 to 4 employees")
          .restriction(berufEigener, Res.of("C2", "E2"))
          .get();
  private static Phenotype berufEigener_3_7 =
      new Phe("berufEigener_3_7")
          .titleEn("3.7")
          .descriptionDe("Angestellter o. n. A.")
          .descriptionEn("Employee n/a")
          .restriction(berufEigener, Res.of("E"))
          .get();
  private static Phenotype berufEigener_3_9 =
      new Phe("berufEigener_3_9")
          .titleEn("3.9")
          .descriptionDe("Selbstaendiger in Handel, Gewerbe etc.")
          .descriptionEn("Self-employed in trade, commerce, etc.")
          .restriction(berufEigener, Res.of("C"))
          .get();
  private static Phenotype berufEigener_4_1 =
      new Phe("berufEigener_4_1")
          .titleEn("4.1")
          .descriptionDe("Beamter im mittleren Dienst")
          .descriptionEn("Intermediate civil servant")
          .restriction(berufEigener, Res.of("D2"))
          .get();
  private static Phenotype berufEigener_4_2 =
      new Phe("berufEigener_4_2")
          .titleEn("4.2")
          .descriptionDe(
              "Angestellter mit verantwortlicher Taetigkeit, Selbstaendiger mit 5 oder mehr Mitarbeitern/PGH-Mitglied")
          .descriptionEn(
              "Employee with a responsible job, self-employed person with 5 or more employees/member of production cooperative of the skilled crafts sector")
          .restriction(berufEigener, Res.of("C3", "C4", "E3"))
          .get();
  private static Phenotype berufEigener_4_7 =
      new Phe("berufEigener_4_7")
          .titleEn("4.7")
          .descriptionDe("Angestellter mit umfassender Fuehrungstaetigkeit")
          .descriptionEn("Employee with extensive management responsibilities")
          .restriction(berufEigener, Res.of("E4"))
          .get();
  private static Phenotype berufEigener_5_0 =
      new Phe("berufEigener_5_0")
          .titleEn("5.0")
          .descriptionDe("Beamter o. n. A.")
          .descriptionEn("Civil servant n/a")
          .restriction(berufEigener, Res.of("D"))
          .get();
  private static Phenotype berufEigener_5_2 =
      new Phe("berufEigener_5_2")
          .titleEn("5.2")
          .descriptionDe("Beamter im gehobenen Dienst")
          .descriptionEn("Senior civil servant")
          .restriction(berufEigener, Res.of("D3"))
          .get();
  private static Phenotype berufEigener_5_8 =
      new Phe("berufEigener_5_8")
          .titleEn("5.8")
          .descriptionDe("Freiberufler (Akademiker) ohne Mitarbeiter")
          .descriptionEn("Freelancer (academic) without employees")
          .restriction(berufEigener, Res.of("B1"))
          .get();
  private static Phenotype berufEigener_6_2 =
      new Phe("berufEigener_6_2")
          .titleEn("6.2")
          .descriptionDe("Akademiker im freien Beruf")
          .descriptionEn("Academic in a freelance profession")
          .restriction(berufEigener, Res.of("B"))
          .get();
  private static Phenotype berufEigener_6_4 =
      new Phe("berufEigener_6_4")
          .titleEn("6.4")
          .descriptionDe("Beamter im hoeheren Dienst")
          .descriptionEn("Civil servant in higher service")
          .restriction(berufEigener, Res.of("D4"))
          .get();
  private static Phenotype berufEigener_6_8 =
      new Phe("berufEigener_6_8")
          .titleEn("6.8")
          .descriptionDe("Freiberufler (Akademiker) mit 1 bis 4 Mitarbeitern")
          .descriptionEn("Freelancer (academic) with 1 to 4 employees")
          .restriction(berufEigener, Res.of("B2"))
          .get();
  private static Phenotype berufEigener_7_0 =
      new Phe("berufEigener_7_0")
          .titleEn("7.0")
          .descriptionDe("Freiberufler (Akademiker) mit 5 oder mehr Mitarbeitern")
          .descriptionEn("Freelancer (academic) with 5 or more employees")
          .restriction(berufEigener, Res.of("B3"))
          .get();

  private static Phenotype berufEigenerSES =
      new Phe("berufEigenerSES")
          .category(berufKategorie)
          .titleDe("Eigener Beruf (SES)")
          .titleEn("Own profession (SES)")
          .expression(
              If.of(
                  Or.of(erwerbstaetig, frueherErwerbstaetig),
                  Switch.of(
                      Exp.of(berufEigener_1_0),
                      Exp.of(1.0),
                      Exp.of(berufEigener_1_1),
                      Exp.of(1.1),
                      Exp.of(berufEigener_1_3),
                      Exp.of(1.3),
                      Exp.of(berufEigener_1_8),
                      Exp.of(1.8),
                      Exp.of(berufEigener_1_9),
                      Exp.of(1.9),
                      Exp.of(berufEigener_2_0),
                      Exp.of(2.0),
                      Exp.of(berufEigener_2_1),
                      Exp.of(2.1),
                      Exp.of(berufEigener_2_4),
                      Exp.of(2.4),
                      Exp.of(berufEigener_2_9),
                      Exp.of(2.9),
                      Exp.of(berufEigener_3_5),
                      Exp.of(3.5),
                      Exp.of(berufEigener_3_6),
                      Exp.of(3.6),
                      Exp.of(berufEigener_3_7),
                      Exp.of(3.7),
                      Exp.of(berufEigener_3_9),
                      Exp.of(3.9),
                      Exp.of(berufEigener_4_1),
                      Exp.of(4.1),
                      Exp.of(berufEigener_4_2),
                      Exp.of(4.2),
                      Exp.of(berufEigener_4_7),
                      Exp.of(4.7),
                      Exp.of(berufEigener_5_0),
                      Exp.of(5.0),
                      Exp.of(berufEigener_5_2),
                      Exp.of(5.2),
                      Exp.of(berufEigener_5_8),
                      Exp.of(5.8),
                      Exp.of(berufEigener_6_2),
                      Exp.of(6.2),
                      Exp.of(berufEigener_6_4),
                      Exp.of(6.4),
                      Exp.of(berufEigener_6_8),
                      Exp.of(6.8),
                      Exp.of(berufEigener_7_0),
                      Exp.of(7.0))))
          .get();

  /////////////////////////// BERUF PARTNER ///////////////////////////

  private static Phenotype familienstand =
      new Phe("familienstand", "SOZIO", "F0026")
          .category(berufParameterKategorie)
          .titleDe("Familienstand")
          .titleEn("Marital status")
          .descriptionDe("Welchen Familienstand haben Sie?")
          .descriptionEn("What is your marital status?")
          .number()
          .get();
  private static Phenotype verheiratet =
      new Phe("verheiratet")
          .titleDe("Verheiratet")
          .titleEn("Married")
          .restriction(familienstand, Res.of(1))
          .get();

  private static Phenotype partnerschaft =
      new Phe("partnerschaft", "SOZIO", "F0035")
          .category(berufParameterKategorie)
          .titleDe("Partnerschaft")
          .titleEn("Partnership")
          .descriptionDe("Leben Sie mit einem Partner zusammen?")
          .descriptionEn("Do you live together with a partner?")
          .number()
          .get();
  private static Phenotype lebenZusammen =
      new Phe("lebenZusammen")
          .titleDe("Ja")
          .titleEn("Yes")
          .restriction(partnerschaft, Res.of(1))
          .get();

  private static Phenotype berufPartner =
      new Phe("berufPartner", "SOZIO", "F0078")
          .category(berufParameterKategorie)
          .titleDe("Beruf des Partners")
          .titleEn("Partner's profession")
          .descriptionDe("Zu welcher Gruppe gehoert der Beruf Ihres Partners?")
          .descriptionEn("To which group does your partner's profession belong?")
          .string()
          .get();
  private static Phenotype berufPartner_1_0 =
      new Phe("berufPartner_1_0")
          .titleEn("1.0")
          .descriptionDe(
              "Landwirt o. n. A., Landwirt unter 10 ha, Genossenschaftsbauer (ehem. LPG)")
          .descriptionEn(
              "Farmer n/a, farmer under 10 ha, cooperative farmer (former agricultural production cooperative)")
          .restriction(berufPartner, Res.of("A", "A1", "A3"))
          .get();
  private static Phenotype berufPartner_1_1 =
      new Phe("berufPartner_1_1")
          .titleEn("1.1")
          .descriptionDe("Landwirt 10 ha und mehr")
          .descriptionEn("Farmer 10 ha and more")
          .restriction(berufPartner, Res.of("A2"))
          .get();
  private static Phenotype berufPartner_1_3 =
      new Phe("berufPartner_1_3")
          .titleEn("1.3")
          .descriptionDe("Ungelernter Arbeiter")
          .descriptionEn("Unskilled worker")
          .restriction(berufPartner, Res.of("F1"))
          .get();
  private static Phenotype berufPartner_1_8 =
      new Phe("berufPartner_1_8")
          .titleEn("1.8")
          .descriptionDe("Angelernter Arbeiter")
          .descriptionEn("Semi-skilled worker")
          .restriction(berufPartner, Res.of("F2"))
          .get();
  private static Phenotype berufPartner_1_9 =
      new Phe("berufPartner_1_9")
          .titleEn("1.9")
          .descriptionDe("Arbeiter o. n. A.")
          .descriptionEn("Worker n/a")
          .restriction(berufPartner, Res.of("F"))
          .get();
  private static Phenotype berufPartner_2_0 =
      new Phe("berufPartner_2_0")
          .titleEn("2.0")
          .descriptionDe("Vorarbeiter, Kolonnenfuehrer")
          .descriptionEn("Foreman, column leader")
          .restriction(berufPartner, Res.of("F4"))
          .get();
  private static Phenotype berufPartner_2_1 =
      new Phe("berufPartner_2_1")
          .titleEn("2.1")
          .descriptionDe("Facharbeiter")
          .descriptionEn("Skilled worker")
          .restriction(berufPartner, Res.of("F3"))
          .get();
  private static Phenotype berufPartner_2_4 =
      new Phe("berufPartner_2_4")
          .titleEn("2.4")
          .descriptionDe("Meister, Polier, Brigadier, Angestellter mit ausfuehrender Taetigkeit")
          .descriptionEn("Master, foreman, brigadier, employee with executive activity")
          .restriction(berufPartner, Res.of("E1", "F5"))
          .get();
  private static Phenotype berufPartner_2_9 =
      new Phe("berufPartner_2_9")
          .titleEn("2.9")
          .descriptionDe(
              "Beamter im einfachen Dienst, Auszubildender, mithelfender Familienangehoeriger")
          .descriptionEn("Civil servant in the civil service, trainee, assisting family member")
          .restriction(berufPartner, Res.of("D1", "G", "G1", "G2", "G3", "H"))
          .get();
  private static Phenotype berufPartner_3_5 =
      new Phe("berufPartner_3_5")
          .titleEn("3.5")
          .descriptionDe("Selbstaendiger, keine Mitarbeiter")
          .descriptionEn("Self-employed, no employees")
          .restriction(berufPartner, Res.of("C1"))
          .get();
  private static Phenotype berufPartner_3_6 =
      new Phe("berufPartner_3_6")
          .titleEn("3.6")
          .descriptionDe(
              "Angestellter mit qualifizierter Taetigkeit, Selbstaendiger mit 1 bis 4 Mitarbeitern")
          .descriptionEn(
              "Employee with qualified activity, self-employed person with 1 to 4 employees")
          .restriction(berufPartner, Res.of("C2", "E2"))
          .get();
  private static Phenotype berufPartner_3_7 =
      new Phe("berufPartner_3_7")
          .titleEn("3.7")
          .descriptionDe("Angestellter o. n. A.")
          .descriptionEn("Employee n/a")
          .restriction(berufPartner, Res.of("E"))
          .get();
  private static Phenotype berufPartner_3_9 =
      new Phe("berufPartner_3_9")
          .titleEn("3.9")
          .descriptionDe("Selbstaendiger in Handel, Gewerbe etc.")
          .descriptionEn("Self-employed in trade, commerce, etc.")
          .restriction(berufPartner, Res.of("C"))
          .get();
  private static Phenotype berufPartner_4_1 =
      new Phe("berufPartner_4_1")
          .titleEn("4.1")
          .descriptionDe("Beamter im mittleren Dienst")
          .descriptionEn("Intermediate civil servant")
          .restriction(berufPartner, Res.of("D2"))
          .get();
  private static Phenotype berufPartner_4_2 =
      new Phe("berufPartner_4_2")
          .titleEn("4.2")
          .descriptionDe(
              "Angestellter mit verantwortlicher Taetigkeit, Selbstaendiger mit 5 oder mehr Mitarbeitern/PGH-Mitglied")
          .descriptionEn(
              "Employee with a responsible job, self-employed person with 5 or more employees/member of production cooperative of the skilled crafts sector")
          .restriction(berufPartner, Res.of("C3", "C4", "E3"))
          .get();
  private static Phenotype berufPartner_4_7 =
      new Phe("berufPartner_4_7")
          .titleEn("4.7")
          .descriptionDe("Angestellter mit umfassender Fuehrungstaetigkeit")
          .descriptionEn("Employee with extensive management responsibilities")
          .restriction(berufPartner, Res.of("E4"))
          .get();
  private static Phenotype berufPartner_5_0 =
      new Phe("berufPartner_5_0")
          .titleEn("5.0")
          .descriptionDe("Beamter o. n. A.")
          .descriptionEn("Civil servant n/a")
          .restriction(berufPartner, Res.of("D"))
          .get();
  private static Phenotype berufPartner_5_2 =
      new Phe("berufPartner_5_2")
          .titleEn("5.2")
          .descriptionDe("Beamter im gehobenen Dienst")
          .descriptionEn("Senior civil servant")
          .restriction(berufPartner, Res.of("D3"))
          .get();
  private static Phenotype berufPartner_5_8 =
      new Phe("berufPartner_5_8")
          .titleEn("5.8")
          .descriptionDe("Freiberufler (Akademiker) ohne Mitarbeiter")
          .descriptionEn("Freelancer (academic) without employees")
          .restriction(berufPartner, Res.of("B1"))
          .get();
  private static Phenotype berufPartner_6_2 =
      new Phe("berufPartner_6_2")
          .titleEn("6.2")
          .descriptionDe("Akademiker im freien Beruf")
          .descriptionEn("Academic in a freelance profession")
          .restriction(berufPartner, Res.of("B"))
          .get();
  private static Phenotype berufPartner_6_4 =
      new Phe("berufPartner_6_4")
          .titleEn("6.4")
          .descriptionDe("Beamter im hoeheren Dienst")
          .descriptionEn("Civil servant in higher service")
          .restriction(berufPartner, Res.of("D4"))
          .get();
  private static Phenotype berufPartner_6_8 =
      new Phe("berufPartner_6_8")
          .titleEn("6.8")
          .descriptionDe("Freiberufler (Akademiker) mit 1 bis 4 Mitarbeitern")
          .descriptionEn("Freelancer (academic) with 1 to 4 employees")
          .restriction(berufPartner, Res.of("B2"))
          .get();
  private static Phenotype berufPartner_7_0 =
      new Phe("berufPartner_7_0")
          .titleEn("7.0")
          .descriptionDe("Freiberufler (Akademiker) mit 5 oder mehr Mitarbeitern")
          .descriptionEn("Freelancer (academic) with 5 or more employees")
          .restriction(berufPartner, Res.of("B3"))
          .get();

  private static Phenotype berufPartnerSES =
      new Phe("berufPartnerSES")
          .category(berufKategorie)
          .titleDe("Beruf des Partners (SES)")
          .titleEn("Partner's profession (SES)")
          .expression(
              If.of(
                  Or.of(verheiratet, lebenZusammen),
                  Switch.of(
                      Exp.of(berufPartner_1_0),
                      Exp.of(1.0),
                      Exp.of(berufPartner_1_1),
                      Exp.of(1.1),
                      Exp.of(berufPartner_1_3),
                      Exp.of(1.3),
                      Exp.of(berufPartner_1_8),
                      Exp.of(1.8),
                      Exp.of(berufPartner_1_9),
                      Exp.of(1.9),
                      Exp.of(berufPartner_2_0),
                      Exp.of(2.0),
                      Exp.of(berufPartner_2_1),
                      Exp.of(2.1),
                      Exp.of(berufPartner_2_4),
                      Exp.of(2.4),
                      Exp.of(berufPartner_2_9),
                      Exp.of(2.9),
                      Exp.of(berufPartner_3_5),
                      Exp.of(3.5),
                      Exp.of(berufPartner_3_6),
                      Exp.of(3.6),
                      Exp.of(berufPartner_3_7),
                      Exp.of(3.7),
                      Exp.of(berufPartner_3_9),
                      Exp.of(3.9),
                      Exp.of(berufPartner_4_1),
                      Exp.of(4.1),
                      Exp.of(berufPartner_4_2),
                      Exp.of(4.2),
                      Exp.of(berufPartner_4_7),
                      Exp.of(4.7),
                      Exp.of(berufPartner_5_0),
                      Exp.of(5.0),
                      Exp.of(berufPartner_5_2),
                      Exp.of(5.2),
                      Exp.of(berufPartner_5_8),
                      Exp.of(5.8),
                      Exp.of(berufPartner_6_2),
                      Exp.of(6.2),
                      Exp.of(berufPartner_6_4),
                      Exp.of(6.4),
                      Exp.of(berufPartner_6_8),
                      Exp.of(6.8),
                      Exp.of(berufPartner_7_0),
                      Exp.of(7.0))))
          .get();

  private static Phenotype berufSES =
      new Phe("berufSES")
          .category(berufKategorie)
          .titleDe("Beruf (SES)")
          .titleEn("Profession (SES)")
          .descriptionDe("Berufsstatus (Haushaltsmerkmal)")
          .descriptionEn("Occupational status (household characteristic)")
          .expression(Max.of(berufEigenerSES, berufPartnerSES))
          .get();

  /////////////////////////// EINKOMMEN HAUSHALT ///////////////////////////

  private static Category einkommenKategorie =
      new Cat("einkommenKategorie").titleDe("Einkommen").titleEn("Income").get();

  private static Category einkommenParameterKategorie =
      new Cat("einkommenParameterKategorie")
          .superCategory(einkommenKategorie)
          .titleDe("Parameter")
          .titleEn("Parameters")
          .get();

  private static Phenotype einkommenHaushalt =
      new Phe("einkommenHaushalt", "SOZIO", "F0083")
          .category(einkommenParameterKategorie)
          .titleDe("Haushaltseinkommen")
          .titleEn("Household income")
          .descriptionDe(
              "Wie hoch ist das monatliche Nettoeinkommen Ihres Haushalts insgesamt? Ich meine dabei die Summe, die sich aus Lohn, Gehalt, Einkommen aus selbststaendiger Taetigkeit, Rente oder Pension ergibt. Rechnen Sie bitte auch die Einkuenfte aus oeffentlichen Beihilfen, Einkommen aus Vermietung, Verpachtung, Wohngeld, Kindergeld und sonstige Einkuenfte hinzu und ziehen Sie dann Steuern und Sozialversicherungsbetraege ab. Falls jemand in Ihrem Haushalt in selbststaendiger Taetigkeit arbeitet, bitte beruecksichtigen Sie fuer diese Person die durchschnittlichen Nettobezuege abzueglich der Betriebsausgaben.")
          .descriptionEn(
              "What is the total monthly net income of your household? I am referring to the sum resulting from wages, salary, income from self-employment, pension or annuity. Please also include income from public benefits, income from renting, leasing, housing benefit, child benefit and other income and then deduct taxes and social security contributions. If someone in your household is self-employed, please take into account the average net income for this person minus operating expenses.")
          .number()
          .get();

  private static Phenotype einkommensgruppeHaushalt =
      new Phe("einkommensgruppeHaushalt", "SOZIO", "F0084")
          .category(einkommenParameterKategorie)
          .titleDe("Einkommensgruppe des Haushalts")
          .titleEn("Income group of the household")
          .descriptionDe(
              "Bei dieser Frage geht es darum, Gruppen in der Bevoelkerung mit z. B. hohem, mittlerem oder niedrigerem Einkommen auswerten zu koennen. Es wuerde uns deshalb sehr helfen wenn Sie die Einkommensgruppe nennen wuerden zu der Ihr Haushalt gehoert.")
          .descriptionEn(
              "This question is about being able to analyse groups in the population with e.g. high, medium or low income. It would therefore be very helpful if you could state the income group to which your household belongs.")
          .string()
          .get();
  private static Phenotype einkommensgruppeHaushalt_150 =
      new Phe("einkommensgruppeHaushalt_150")
          .titleEn("<150")
          .restriction(einkommensgruppeHaushalt, Res.of("B"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_150_400 =
      new Phe("einkommensgruppeHaushalt_150_400")
          .titleEn("150-400")
          .restriction(einkommensgruppeHaushalt, Res.of("P"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_400_500 =
      new Phe("einkommensgruppeHaushalt_400_500")
          .titleEn("400-500")
          .restriction(einkommensgruppeHaushalt, Res.of("T"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_500_750 =
      new Phe("einkommensgruppeHaushalt_500_750")
          .titleEn("500-750")
          .restriction(einkommensgruppeHaushalt, Res.of("F"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_750_1000 =
      new Phe("einkommensgruppeHaushalt_750_1000")
          .titleEn("750-1000")
          .restriction(einkommensgruppeHaushalt, Res.of("E"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_1000_1250 =
      new Phe("einkommensgruppeHaushalt_1000_1250")
          .titleEn("1000-1250")
          .restriction(einkommensgruppeHaushalt, Res.of("H"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_1250_1500 =
      new Phe("einkommensgruppeHaushalt_1250_1500")
          .titleEn("1250-1500")
          .restriction(einkommensgruppeHaushalt, Res.of("L"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_1500_1750 =
      new Phe("einkommensgruppeHaushalt_1500_1750")
          .titleEn("1500-1750")
          .restriction(einkommensgruppeHaushalt, Res.of("N"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_1750_2000 =
      new Phe("einkommensgruppeHaushalt_1750_2000")
          .titleEn("1750-2000")
          .restriction(einkommensgruppeHaushalt, Res.of("R"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_2000_2250 =
      new Phe("einkommensgruppeHaushalt_2000_2250")
          .titleEn("2000-2250")
          .restriction(einkommensgruppeHaushalt, Res.of("M"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_2250_2500 =
      new Phe("einkommensgruppeHaushalt_2250_2500")
          .titleEn("2250-2500")
          .restriction(einkommensgruppeHaushalt, Res.of("S"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_2500_2750 =
      new Phe("einkommensgruppeHaushalt_2500_2750")
          .titleEn("2500-2750")
          .restriction(einkommensgruppeHaushalt, Res.of("K"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_2750_3000 =
      new Phe("einkommensgruppeHaushalt_2750_3000")
          .titleEn("2750-3000")
          .restriction(einkommensgruppeHaushalt, Res.of("O"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_3000_3250 =
      new Phe("einkommensgruppeHaushalt_3000_3250")
          .titleEn("3000-3250")
          .restriction(einkommensgruppeHaushalt, Res.of("C"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_3250_3500 =
      new Phe("einkommensgruppeHaushalt_3250_3500")
          .titleEn("3250-3500")
          .restriction(einkommensgruppeHaushalt, Res.of("G"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_3500_3750 =
      new Phe("einkommensgruppeHaushalt_3500_3750")
          .titleEn("3500-3750")
          .restriction(einkommensgruppeHaushalt, Res.of("U"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_3750_4000 =
      new Phe("einkommensgruppeHaushalt_3750_4000")
          .titleEn("3750-4000")
          .restriction(einkommensgruppeHaushalt, Res.of("J"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_4000_4500 =
      new Phe("einkommensgruppeHaushalt_4000_4500")
          .titleEn("4000-4500")
          .restriction(einkommensgruppeHaushalt, Res.of("V"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_4500_5000 =
      new Phe("einkommensgruppeHaushalt_4500_5000")
          .titleEn("4500-5000")
          .restriction(einkommensgruppeHaushalt, Res.of("A"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_5000_5500 =
      new Phe("einkommensgruppeHaushalt_5000_5500")
          .titleEn("5000-5500")
          .restriction(einkommensgruppeHaushalt, Res.of("Z"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_5500_6000 =
      new Phe("einkommensgruppeHaushalt_5500_6000")
          .titleEn("5500-6000")
          .restriction(einkommensgruppeHaushalt, Res.of("X"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_6000_7500 =
      new Phe("einkommensgruppeHaushalt_6000_7500")
          .titleEn("6000-7500")
          .restriction(einkommensgruppeHaushalt, Res.of("Q"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_7500_10000 =
      new Phe("einkommensgruppeHaushalt_7500_10000")
          .titleEn("7500-10000")
          .restriction(einkommensgruppeHaushalt, Res.of("W"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_10000_20000 =
      new Phe("einkommensgruppeHaushalt_10000_20000")
          .titleEn("10000-20000")
          .restriction(einkommensgruppeHaushalt, Res.of("D"))
          .get();
  private static Phenotype einkommensgruppeHaushalt_20000 =
      new Phe("einkommensgruppeHaushalt_20000")
          .titleEn("≥20000")
          .restriction(einkommensgruppeHaushalt, Res.of("Y"))
          .get();

  private static Phenotype einkommenHaushaltSES =
      new Phe("einkommenHaushaltSES")
          .category(einkommenKategorie)
          .titleDe("Haushaltseinkommen (SES)")
          .titleEn("Household income (SES)")
          .expression(
              Switch.of(
                  Exists.of(einkommenHaushalt),
                  Exp.of(einkommenHaushalt),
                  Exp.of(einkommensgruppeHaushalt_150),
                  Exp.of(75),
                  Exp.of(einkommensgruppeHaushalt_150_400),
                  Exp.of(275),
                  Exp.of(einkommensgruppeHaushalt_400_500),
                  Exp.of(450),
                  Exp.of(einkommensgruppeHaushalt_500_750),
                  Exp.of(625),
                  Exp.of(einkommensgruppeHaushalt_750_1000),
                  Exp.of(875),
                  Exp.of(einkommensgruppeHaushalt_1000_1250),
                  Exp.of(1125),
                  Exp.of(einkommensgruppeHaushalt_1250_1500),
                  Exp.of(1375),
                  Exp.of(einkommensgruppeHaushalt_1500_1750),
                  Exp.of(1625),
                  Exp.of(einkommensgruppeHaushalt_1750_2000),
                  Exp.of(1875),
                  Exp.of(einkommensgruppeHaushalt_2000_2250),
                  Exp.of(2125),
                  Exp.of(einkommensgruppeHaushalt_2250_2500),
                  Exp.of(2375),
                  Exp.of(einkommensgruppeHaushalt_2500_2750),
                  Exp.of(2625),
                  Exp.of(einkommensgruppeHaushalt_2750_3000),
                  Exp.of(2875),
                  Exp.of(einkommensgruppeHaushalt_3000_3250),
                  Exp.of(3125),
                  Exp.of(einkommensgruppeHaushalt_3250_3500),
                  Exp.of(3375),
                  Exp.of(einkommensgruppeHaushalt_3500_3750),
                  Exp.of(3625),
                  Exp.of(einkommensgruppeHaushalt_3750_4000),
                  Exp.of(3875),
                  Exp.of(einkommensgruppeHaushalt_4000_4500),
                  Exp.of(4250),
                  Exp.of(einkommensgruppeHaushalt_4500_5000),
                  Exp.of(4750),
                  Exp.of(einkommensgruppeHaushalt_5000_5500),
                  Exp.of(5250),
                  Exp.of(einkommensgruppeHaushalt_5500_6000),
                  Exp.of(5750),
                  Exp.of(einkommensgruppeHaushalt_6000_7500),
                  Exp.of(6750),
                  Exp.of(einkommensgruppeHaushalt_7500_10000),
                  Exp.of(8750),
                  Exp.of(einkommensgruppeHaushalt_10000_20000),
                  Exp.of(15000),
                  Exp.of(einkommensgruppeHaushalt_20000),
                  Exp.of(20000)))
          .get();

  /////////////////////////// EIGENES EINKOMMEN ///////////////////////////

  private static Phenotype einkommenEigenes =
      new Phe("einkommenEigenes", "SOZIO", "F0086")
          .category(einkommenParameterKategorie)
          .titleDe("Eigenes Einkommen")
          .titleEn("Own income")
          .descriptionDe(
              "Wie hoch ist Ihr eigenes monatliches Nettoeinkommen? Ich meine dabei die Summe, die sich aus Lohn, Gehalt, Einkommen aus selbststaendiger Taetigkeit, Rente oder Pension ergibt. Rechnen Sie bitte auch die Einkuenfte aus oeffentlichen Beihilfen, Einkommen aus Vermietung, Verpachtung, Wohngeld, Kindergeld und sonstige Einkuenfte hinzu und ziehen Sie dann Steuern und Sozialversicherungsbetraege ab. Falls jemand in Ihrem Haushalt in selbststaendiger Taetigkeit arbeitet, bitte beruecksichtigen Sie fuer diese Person die durchschnittlichen Nettobezuege abzueglich der Betriebsausgaben.")
          .descriptionEn(
              "What is your own monthly net income? I am referring to the sum resulting from wages, salary, income from self-employment, pension or annuity. Please also include income from public benefits, income from renting, leasing, housing benefit, child benefit and other income and then deduct taxes and social security contributions. If someone in your household is self-employed, please take into account the average net income for this person minus operating expenses.")
          .number()
          .get();

  private static Phenotype einkommensgruppeEigenes =
      new Phe("einkommensgruppeEigenes", "SOZIO", "F0087")
          .category(einkommenParameterKategorie)
          .titleDe("Eigene Einkommensgruppe")
          .titleEn("Own income group")
          .descriptionDe(
              "Bei dieser Frage geht es darum, Gruppen in der Bevoelkerung mit z. B. hohem, mittlerem oder niedrigerem Einkommen auswerten zu koennen. Es wuerde uns deshalb sehr helfen wenn Sie die Einkommensgruppe nennen wuerden zu der Sie gehoeren.")
          .descriptionEn(
              "This question is about being able to analyse groups in the population with e.g. high, medium or low income. It would therefore be very helpful if you could state the income group to which you belong.")
          .string()
          .get();
  private static Phenotype einkommensgruppeEigenes_150 =
      new Phe("einkommensgruppeEigenes_150")
          .titleEn("<150")
          .restriction(einkommensgruppeEigenes, Res.of("B"))
          .get();
  private static Phenotype einkommensgruppeEigenes_150_400 =
      new Phe("einkommensgruppeEigenes_150_400")
          .titleEn("150-400")
          .restriction(einkommensgruppeEigenes, Res.of("P"))
          .get();
  private static Phenotype einkommensgruppeEigenes_400_500 =
      new Phe("einkommensgruppeEigenes_400_500")
          .titleEn("400-500")
          .restriction(einkommensgruppeEigenes, Res.of("T"))
          .get();
  private static Phenotype einkommensgruppeEigenes_500_750 =
      new Phe("einkommensgruppeEigenes_500_750")
          .titleEn("500-750")
          .restriction(einkommensgruppeEigenes, Res.of("F"))
          .get();
  private static Phenotype einkommensgruppeEigenes_750_1000 =
      new Phe("einkommensgruppeEigenes_750_1000")
          .titleEn("750-1000")
          .restriction(einkommensgruppeEigenes, Res.of("E"))
          .get();
  private static Phenotype einkommensgruppeEigenes_1000_1250 =
      new Phe("einkommensgruppeEigenes_1000_1250")
          .titleEn("1000-1250")
          .restriction(einkommensgruppeEigenes, Res.of("H"))
          .get();
  private static Phenotype einkommensgruppeEigenes_1250_1500 =
      new Phe("einkommensgruppeEigenes_1250_1500")
          .titleEn("1250-1500")
          .restriction(einkommensgruppeEigenes, Res.of("L"))
          .get();
  private static Phenotype einkommensgruppeEigenes_1500_1750 =
      new Phe("einkommensgruppeEigenes_1500_1750")
          .titleEn("1500-1750")
          .restriction(einkommensgruppeEigenes, Res.of("N"))
          .get();
  private static Phenotype einkommensgruppeEigenes_1750_2000 =
      new Phe("einkommensgruppeEigenes_1750_2000")
          .titleEn("1750-2000")
          .restriction(einkommensgruppeEigenes, Res.of("R"))
          .get();
  private static Phenotype einkommensgruppeEigenes_2000_2250 =
      new Phe("einkommensgruppeEigenes_2000_2250")
          .titleEn("2000-2250")
          .restriction(einkommensgruppeEigenes, Res.of("M"))
          .get();
  private static Phenotype einkommensgruppeEigenes_2250_2500 =
      new Phe("einkommensgruppeEigenes_2250_2500")
          .titleEn("2250-2500")
          .restriction(einkommensgruppeEigenes, Res.of("S"))
          .get();
  private static Phenotype einkommensgruppeEigenes_2500_2750 =
      new Phe("einkommensgruppeEigenes_2500_2750")
          .titleEn("2500-2750")
          .restriction(einkommensgruppeEigenes, Res.of("K"))
          .get();
  private static Phenotype einkommensgruppeEigenes_2750_3000 =
      new Phe("einkommensgruppeEigenes_2750_3000")
          .titleEn("2750-3000")
          .restriction(einkommensgruppeEigenes, Res.of("O"))
          .get();
  private static Phenotype einkommensgruppeEigenes_3000_3250 =
      new Phe("einkommensgruppeEigenes_3000_3250")
          .titleEn("3000-3250")
          .restriction(einkommensgruppeEigenes, Res.of("C"))
          .get();
  private static Phenotype einkommensgruppeEigenes_3250_3500 =
      new Phe("einkommensgruppeEigenes_3250_3500")
          .titleEn("3250-3500")
          .restriction(einkommensgruppeEigenes, Res.of("G"))
          .get();
  private static Phenotype einkommensgruppeEigenes_3500_3750 =
      new Phe("einkommensgruppeEigenes_3500_3750")
          .titleEn("3500-3750")
          .restriction(einkommensgruppeEigenes, Res.of("U"))
          .get();
  private static Phenotype einkommensgruppeEigenes_3750_4000 =
      new Phe("einkommensgruppeEigenes_3750_4000")
          .titleEn("3750-4000")
          .restriction(einkommensgruppeEigenes, Res.of("J"))
          .get();
  private static Phenotype einkommensgruppeEigenes_4000_4500 =
      new Phe("einkommensgruppeEigenes_4000_4500")
          .titleEn("4000-4500")
          .restriction(einkommensgruppeEigenes, Res.of("V"))
          .get();
  private static Phenotype einkommensgruppeEigenes_4500_5000 =
      new Phe("einkommensgruppeEigenes_4500_5000")
          .titleEn("4500-5000")
          .restriction(einkommensgruppeEigenes, Res.of("A"))
          .get();
  private static Phenotype einkommensgruppeEigenes_5000_5500 =
      new Phe("einkommensgruppeEigenes_5000_5500")
          .titleEn("5000-5500")
          .restriction(einkommensgruppeEigenes, Res.of("Z"))
          .get();
  private static Phenotype einkommensgruppeEigenes_5500_6000 =
      new Phe("einkommensgruppeEigenes_5500_6000")
          .titleEn("5500-6000")
          .restriction(einkommensgruppeEigenes, Res.of("X"))
          .get();
  private static Phenotype einkommensgruppeEigenes_6000_7500 =
      new Phe("einkommensgruppeEigenes_6000_7500")
          .titleEn("6000-7500")
          .restriction(einkommensgruppeEigenes, Res.of("Q"))
          .get();
  private static Phenotype einkommensgruppeEigenes_7500_10000 =
      new Phe("einkommensgruppeEigenes_7500_10000")
          .titleEn("7500-10000")
          .restriction(einkommensgruppeEigenes, Res.of("W"))
          .get();
  private static Phenotype einkommensgruppeEigenes_10000_20000 =
      new Phe("einkommensgruppeEigenes_10000_20000")
          .titleEn("10000-20000")
          .restriction(einkommensgruppeEigenes, Res.of("D"))
          .get();
  private static Phenotype einkommensgruppeEigenes_20000 =
      new Phe("einkommensgruppeEigenes_20000")
          .titleEn("≥20000")
          .restriction(einkommensgruppeEigenes, Res.of("Y"))
          .get();

  private static Phenotype einkommenEigenesSES =
      new Phe("einkommenEigenesSES")
          .category(einkommenKategorie)
          .titleDe("Eigenes Einkommen (SES)")
          .titleEn("Own income (SES)")
          .expression(
              Switch.of(
                  Exists.of(einkommenEigenes),
                  Exp.of(einkommenEigenes),
                  Exp.of(einkommensgruppeEigenes_150),
                  Exp.of(75),
                  Exp.of(einkommensgruppeEigenes_150_400),
                  Exp.of(275),
                  Exp.of(einkommensgruppeEigenes_400_500),
                  Exp.of(450),
                  Exp.of(einkommensgruppeEigenes_500_750),
                  Exp.of(625),
                  Exp.of(einkommensgruppeEigenes_750_1000),
                  Exp.of(875),
                  Exp.of(einkommensgruppeEigenes_1000_1250),
                  Exp.of(1125),
                  Exp.of(einkommensgruppeEigenes_1250_1500),
                  Exp.of(1375),
                  Exp.of(einkommensgruppeEigenes_1500_1750),
                  Exp.of(1625),
                  Exp.of(einkommensgruppeEigenes_1750_2000),
                  Exp.of(1875),
                  Exp.of(einkommensgruppeEigenes_2000_2250),
                  Exp.of(2125),
                  Exp.of(einkommensgruppeEigenes_2250_2500),
                  Exp.of(2375),
                  Exp.of(einkommensgruppeEigenes_2500_2750),
                  Exp.of(2625),
                  Exp.of(einkommensgruppeEigenes_2750_3000),
                  Exp.of(2875),
                  Exp.of(einkommensgruppeEigenes_3000_3250),
                  Exp.of(3125),
                  Exp.of(einkommensgruppeEigenes_3250_3500),
                  Exp.of(3375),
                  Exp.of(einkommensgruppeEigenes_3500_3750),
                  Exp.of(3625),
                  Exp.of(einkommensgruppeEigenes_3750_4000),
                  Exp.of(3875),
                  Exp.of(einkommensgruppeEigenes_4000_4500),
                  Exp.of(4250),
                  Exp.of(einkommensgruppeEigenes_4500_5000),
                  Exp.of(4750),
                  Exp.of(einkommensgruppeEigenes_5000_5500),
                  Exp.of(5250),
                  Exp.of(einkommensgruppeEigenes_5500_6000),
                  Exp.of(5750),
                  Exp.of(einkommensgruppeEigenes_6000_7500),
                  Exp.of(6750),
                  Exp.of(einkommensgruppeEigenes_7500_10000),
                  Exp.of(8750),
                  Exp.of(einkommensgruppeEigenes_10000_20000),
                  Exp.of(15000),
                  Exp.of(einkommensgruppeEigenes_20000),
                  Exp.of(20000)))
          .get();

  private static Phenotype haushaltsgroesse =
      new Phe("haushaltsgroesse", "SOZIO", "F0079")
          .category(einkommenParameterKategorie)
          .titleDe("Haushaltsgroesse")
          .titleEn("Household size")
          .descriptionDe(
              "Wie viele Personen leben staendig in Ihrem Haushalt, Sie selbst eingeschlossen? Denken Sie dabei bitte auch an alle im Haushalt lebenden Kinder.")
          .descriptionEn(
              "How many people permanently live in your household, including yourself? Please also think about any children living in the household.")
          .number()
          .get();

  private static Phenotype haushaltseinkommenSES =
      new Phe("haushaltseinkommenSES")
          .category(einkommenKategorie)
          .titleDe("Einkommen")
          .titleEn("Income")
          .descriptionDe(
              "Lebt genau eine Person im Haushalt, wird fuer die weiteren Berechnungen deren eigenes Einkommen herangezogen, andernfalls das Haushaltseinkommen.")
          .descriptionEn(
              "If exactly one person lives in the household, their own income is used for further calculations, otherwise the household income is used.")
          .expression(
              If.of(
                  Eq.of(haushaltsgroesse, 1),
                  Exp.of(einkommenEigenesSES),
                  Exp.of(einkommenHaushaltSES)))
          .get();

  private static Phenotype juenger15 =
      new Phe("juenger15", "SOZIO", "F0080")
          .category(einkommenParameterKategorie)
          .titleDe("Personen unter 15")
          .titleEn("Persons under 15")
          .descriptionDe("Wie viele der Personen in Ihrem Haushalt sind juenger als 15 Jahre?")
          .descriptionEn("How many of the people in your household are younger than 15?")
          .number()
          .get();

  private static Phenotype h0000072 =
      new Phe("h0000072")
          .category(einkommenKategorie)
          .titleDe("Personen unter 15 (angepasst)")
          .titleEn("Persons under 15 (adjusted)")
          .expression(
              If.of(
                  Or.of(Empty.of(juenger15), In.of(juenger15, 98, 99)),
                  Exp.of(0),
                  Exp.of(juenger15)))
          .get();

  private static Phenotype h0000071 =
      new Phe("h0000071")
          .category(einkommenKategorie)
          .titleDe("Haushaltsgroesse (angepasst)")
          .titleEn("Household size (adjusted)")
          .expression(
              If.of(
                  And.of(Ge.of(haushaltsgroesse, h0000072), Le.of(haushaltsgroesse, 12)),
                  If.of(
                      Eq.of(haushaltsgroesse, h0000072),
                      Add.of(haushaltsgroesse, 1),
                      Exp.of(haushaltsgroesse))))
          .get();

  private static Phenotype bedgew =
      new Phe("bedgew")
          .category(einkommenKategorie)
          .titleDe("Koeffizient fuer Aequivalenzeinkommen")
          .titleEn("Coefficient for equivalised income")
          .expression(
              Sum.of(
                  Exp.of(1.0),
                  Multiply.of(h0000072, 0.3),
                  Multiply.of(
                      Subtract.of(Subtract.of(h0000071, h0000072), Exp.of(1.0)), Exp.of(0.5))))
          .get();

  private static Phenotype aequivalenzeinkommenSES =
      new Phe("aequivalenzeinkommenSES")
          .category(einkommenKategorie)
          .titleDe("Aequivalenzeinkommen")
          .titleEn("Equivalised income")
          .descriptionDe("Nettoaequivalenzeinkommen (Haushaltsmerkmal), Euro")
          .descriptionEn("Net equivalent income (household characteristic), euro")
          .expression(Round.of(Divide.of(haushaltseinkommenSES, bedgew), 2))
          .get();
  private static Phenotype aequivalenzeinkommenSES_1_0 =
      new Phe("aequivalenzeinkommenSES_1_0")
          .titleEn("<800")
          .restriction(aequivalenzeinkommenSES, Res.lt(800))
          .get();
  private static Phenotype aequivalenzeinkommenSES_1_5 =
      new Phe("aequivalenzeinkommenSES_1_5")
          .titleEn("800-980")
          .restriction(aequivalenzeinkommenSES, Res.geLt(800, 980))
          .get();
  private static Phenotype aequivalenzeinkommenSES_2_0 =
      new Phe("aequivalenzeinkommenSES_2_0")
          .titleEn("980-1100")
          .restriction(aequivalenzeinkommenSES, Res.geLt(980, 1100))
          .get();
  private static Phenotype aequivalenzeinkommenSES_2_5 =
      new Phe("aequivalenzeinkommenSES_2_5")
          .titleEn("1100-1200")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1100, 1200))
          .get();
  private static Phenotype aequivalenzeinkommenSES_3_0 =
      new Phe("aequivalenzeinkommenSES_3_0")
          .titleEn("1200-1333")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1200, 1333.33))
          .get();
  private static Phenotype aequivalenzeinkommenSES_3_5 =
      new Phe("aequivalenzeinkommenSES_3_5")
          .titleEn("1333-1400")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1333.33, 1400))
          .get();
  private static Phenotype aequivalenzeinkommenSES_4_0 =
      new Phe("aequivalenzeinkommenSES_4_0")
          .titleEn("1400-1533")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1400, 1533.33))
          .get();
  private static Phenotype aequivalenzeinkommenSES_4_5 =
      new Phe("aequivalenzeinkommenSES_4_5")
          .titleEn("1533-1667")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1533.33, 1666.67))
          .get();
  private static Phenotype aequivalenzeinkommenSES_5_0 =
      new Phe("aequivalenzeinkommenSES_5_0")
          .titleEn("1667-1867")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1666.67, 1866.67))
          .get();
  private static Phenotype aequivalenzeinkommenSES_5_5 =
      new Phe("aequivalenzeinkommenSES_5_5")
          .titleEn("1867-2000")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1866.67, 2000))
          .get();
  private static Phenotype aequivalenzeinkommenSES_6_0 =
      new Phe("aequivalenzeinkommenSES_6_0")
          .titleEn("2000-2333")
          .restriction(aequivalenzeinkommenSES, Res.geLt(2000, 2333.33))
          .get();
  private static Phenotype aequivalenzeinkommenSES_6_5 =
      new Phe("aequivalenzeinkommenSES_6_5")
          .titleEn("2333-3000")
          .restriction(aequivalenzeinkommenSES, Res.geLt(2333.33, 3000))
          .get();
  private static Phenotype aequivalenzeinkommenSES_7_0 =
      new Phe("aequivalenzeinkommenSES_7_0")
          .titleEn("≥3000")
          .restriction(aequivalenzeinkommenSES, Res.ge(3000))
          .get();

  private static Phenotype einkommenSES =
      new Phe("einkommenSES")
          .category(einkommenKategorie)
          .titleDe("Einkommen (SES)")
          .titleEn("Income (SES)")
          .descriptionDe("Nettoaequivalenzeinkommen (Haushaltsmerkmal), Punktwert")
          .descriptionEn("Net equivalent income (household characteristic), point value")
          .expression(
              Switch.of(
                  Exp.of(aequivalenzeinkommenSES_1_0),
                  Exp.of(1.0),
                  Exp.of(aequivalenzeinkommenSES_1_5),
                  Exp.of(1.5),
                  Exp.of(aequivalenzeinkommenSES_2_0),
                  Exp.of(2.0),
                  Exp.of(aequivalenzeinkommenSES_2_5),
                  Exp.of(2.5),
                  Exp.of(aequivalenzeinkommenSES_3_0),
                  Exp.of(3.0),
                  Exp.of(aequivalenzeinkommenSES_3_5),
                  Exp.of(3.5),
                  Exp.of(aequivalenzeinkommenSES_4_0),
                  Exp.of(4.0),
                  Exp.of(aequivalenzeinkommenSES_4_5),
                  Exp.of(4.5),
                  Exp.of(aequivalenzeinkommenSES_5_0),
                  Exp.of(5.0),
                  Exp.of(aequivalenzeinkommenSES_5_5),
                  Exp.of(5.5),
                  Exp.of(aequivalenzeinkommenSES_6_0),
                  Exp.of(6.0),
                  Exp.of(aequivalenzeinkommenSES_6_5),
                  Exp.of(6.5),
                  Exp.of(aequivalenzeinkommenSES_7_0),
                  Exp.of(7.0)))
          .get();

  /////////////////////////// SES ///////////////////////////

  private static Phenotype SES =
      new Phe("SES")
          .titleEn("SES")
          .descriptionDe("Soziooekonomischer Status-Index, Punktsummenscore")
          .descriptionEn("Socioeconomic status index, point sum score")
          .expression(
              Switch.of(
                  And.of(Exists.of(bildungSES), Exists.of(berufSES), Exists.of(einkommenSES)),
                  Sum.of(bildungSES, berufSES, einkommenSES),
                  And.of(Exists.of(berufSES), Exists.of(einkommenSES)),
                  Sum.of(
                      Exp.of(berufSES),
                      Exp.of(einkommenSES),
                      Avg.of(Exp.of(berufSES), Exp.of(einkommenSES), Exp.of(2))),
                  And.of(Exists.of(bildungSES), Exists.of(einkommenSES)),
                  Sum.of(
                      Exp.of(bildungSES),
                      Exp.of(einkommenSES),
                      Avg.of(Exp.of(bildungSES), Exp.of(einkommenSES), Exp.of(2))),
                  And.of(Exists.of(bildungSES), Exists.of(berufSES)),
                  Sum.of(
                      Exp.of(bildungSES),
                      Exp.of(berufSES),
                      Avg.of(Exp.of(bildungSES), Exp.of(berufSES), Exp.of(2)))))
          .get();
  private static Phenotype SES_Q1 =
      new Phe("SES_Q1")
          .titleDe("1. Quintil")
          .titleEn("1st quintile")
          .restriction(SES, Res.le(9.2))
          .get();
  private static Phenotype SES_Q2 =
      new Phe("SES_Q2")
          .titleDe("2. Quintil")
          .titleEn("2nd quintile")
          .restriction(SES, Res.gtLe(9.2, 11.3))
          .get();
  private static Phenotype SES_Q3 =
      new Phe("SES_Q3")
          .titleDe("3. Quintil")
          .titleEn("3rd quintile")
          .restriction(SES, Res.gtLe(11.3, 13.2))
          .get();
  private static Phenotype SES_Q4 =
      new Phe("SES_Q4")
          .titleDe("4. Quintil")
          .titleEn("4th quintile")
          .restriction(SES, Res.gtLe(13.2, 15.3))
          .get();
  private static Phenotype SES_Q5 =
      new Phe("SES_Q5")
          .titleDe("5. Quintil")
          .titleEn("5th quintile")
          .restriction(SES, Res.gt(15.3))
          .get();
  private static Phenotype SES_Niedrig =
      new Phe("SES_Niedrig").titleDe("Niedrig").titleEn("Low").restriction(SES, Res.le(9.2)).get();
  private static Phenotype SES_Mittel =
      new Phe("SES_Mittel")
          .titleDe("Mittel")
          .titleEn("Medium")
          .restriction(SES, Res.gtLe(9.2, 15.3))
          .get();
  private static Phenotype SES_Hoch =
      new Phe("SES_Hoch").titleDe("Hoch").titleEn("High").restriction(SES, Res.gt(15.3)).get();

  @BeforeAll
  static void beforeAll() {
    WRITER
        .insertSbj(1, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 3)
        .insertPhe(juenger15, 1);

    WRITER
        .insertSbj(2, "1995-05-05", "female")
        .insertPhe(schule, 1)
        .insertPhe(ausbildung2, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "G1")
        .insertPhe(familienstand, 2)
        .insertPhe(einkommenHaushalt, 2000)
        .insertPhe(einkommenEigenes, 1500)
        .insertPhe(haushaltsgroesse, 1)
        .insertPhe(juenger15, 0);

    WRITER
        .insertSbj(3, "2001-01-01", "female")
        .insertPhe(schule, 6)
        .insertPhe(ausbildung6, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "E")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "F5")
        .insertPhe(einkommensgruppeHaushalt, "T")
        .insertPhe(einkommensgruppeEigenes, "P")
        .insertPhe(haushaltsgroesse, 3)
        .insertPhe(juenger15, 1);

    WRITER
        .insertSbj(4, "2001-01-01", "male")
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 3)
        .insertPhe(juenger15, 1);

    WRITER
        .insertSbj(5, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 3)
        .insertPhe(juenger15, 1);

    WRITER
        .insertSbj(6, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1");

    WRITER
        .insertSbj(7, "1995-05-05", "female")
        .insertPhe(ausbildung4, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "G1")
        .insertPhe(familienstand, 2)
        .insertPhe(einkommenHaushalt, 2000)
        .insertPhe(einkommenEigenes, 1500)
        .insertPhe(haushaltsgroesse, 1)
        .insertPhe(juenger15, 0);

    WRITER
        .insertSbj(8, "1995-05-05", "female")
        .insertPhe(schule, 4)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "G1")
        .insertPhe(familienstand, 2)
        .insertPhe(einkommenHaushalt, 2000)
        .insertPhe(einkommenEigenes, 1500)
        .insertPhe(haushaltsgroesse, 1)
        .insertPhe(juenger15, 0);

    WRITER
        .insertSbj(9, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 98)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 3)
        .insertPhe(juenger15, 1);

    WRITER
        .insertSbj(10, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 3)
        .insertPhe(juenger15, 1);

    WRITER
        .insertSbj(11, "1995-05-05", "female")
        .insertPhe(schule, 1)
        .insertPhe(ausbildung2, 1)
        .insertPhe(berufEigener, "G1")
        .insertPhe(einkommenHaushalt, 2000)
        .insertPhe(einkommenEigenes, 1500)
        .insertPhe(haushaltsgroesse, 1)
        .insertPhe(juenger15, 0);

    WRITER
        .insertSbj(12, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000);

    WRITER
        .insertSbj(13, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 99)
        .insertPhe(juenger15, 98);

    WRITER
        .insertSbj(14, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 2)
        .insertPhe(juenger15, 3);

    WRITER
        .insertSbj(15, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 13)
        .insertPhe(juenger15, 3);

    WRITER
        .insertSbj(16, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 3);

    WRITER
        .insertSbj(17, "2001-01-01", "male")
        .insertPhe(schule, 2)
        .insertPhe(ausbildung3, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "B1")
        .insertPhe(familienstand, 1)
        .insertPhe(berufPartner, "C1")
        .insertPhe(einkommenHaushalt, 3000)
        .insertPhe(einkommenEigenes, 2000)
        .insertPhe(haushaltsgroesse, 2)
        .insertPhe(juenger15, 2);

    WRITER
        .insertSbj(18, "2001-01-01", "male")
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(familienstand, 1)
        .insertPhe(haushaltsgroesse, 2)
        .insertPhe(juenger15, 2);

    WRITER
        .insertSbj(19, "1995-05-05", "female")
        .insertPhe(schule, 98)
        .insertPhe(ausbildung2, 1)
        .insertPhe(erwerbstaetigkeit, 2)
        .insertPhe(berufEigener, "G1")
        .insertPhe(familienstand, 2)
        .insertPhe(einkommenHaushalt, 2000)
        .insertPhe(einkommenEigenes, 1500)
        .insertPhe(haushaltsgroesse, 1)
        .insertPhe(juenger15, 0);

    WRITER.printSbj();
    WRITER.printPhe();
  }

  @AfterAll
  static void afterAll() {
    WRITER.close();
  }

  //  @Test
  //  void test() {
  //    Entities.writeJSON(
  //        "test_files/ses.json",
  //        bildungParameterKategorie,
  //        bildungKategorie,
  //        schule,
  //        schule1,
  //        schule2,
  //        schule3,
  //        schule4,
  //        schule5,
  //        schule6,
  //        schule7,
  //        schule12,
  //        schule123,
  //        schule45,
  //        schule67,
  //        ausbildung1,
  //        ausbildung2,
  //        ausbildung3,
  //        ausbildung4,
  //        ausbildung5,
  //        ausbildung6,
  //        ausbildung7,
  //        ausbildung,
  //        ausbildungS1,
  //        ausbildungS2,
  //        ausbildungS3,
  //        ausbildungS4,
  //        ausbildungS5,
  //        ausbildungS6,
  //        ausbildungS7,
  //        ausbildung12,
  //        ausbildung345,
  //        bildungSES,
  //        bildungSES_1_0,
  //        bildungSES_1_7,
  //        bildungSES_2_8,
  //        bildungSES_3_0,
  //        bildungSES_3_6,
  //        bildungSES_3_7,
  //        bildungSES_4_55,
  //        bildungSES_4_8,
  //        bildungSES_4_85,
  //        bildungSES_5_0,
  //        bildungSES_5_3,
  //        bildungSES_6_1,
  //        bildungSES_7_0,
  //        berufParameterKategorie,
  //        berufKategorie,
  //        erwerbstaetigkeit,
  //        erwerbstaetig,
  //        fruehereErwerbstaetigkeit,
  //        frueherErwerbstaetig,
  //        familienstand,
  //        verheiratet,
  //        partnerschaft,
  //        lebenZusammen,
  //        berufEigener,
  //        berufEigener_1_0,
  //        berufEigener_1_1,
  //        berufEigener_1_3,
  //        berufEigener_1_8,
  //        berufEigener_1_9,
  //        berufEigener_2_0,
  //        berufEigener_2_1,
  //        berufEigener_2_4,
  //        berufEigener_2_9,
  //        berufEigener_3_5,
  //        berufEigener_3_6,
  //        berufEigener_3_7,
  //        berufEigener_3_9,
  //        berufEigener_4_1,
  //        berufEigener_4_2,
  //        berufEigener_4_7,
  //        berufEigener_5_0,
  //        berufEigener_5_2,
  //        berufEigener_5_8,
  //        berufEigener_6_2,
  //        berufEigener_6_4,
  //        berufEigener_6_8,
  //        berufEigener_7_0,
  //        berufEigenerSES,
  //        berufPartner,
  //        berufPartner_1_0,
  //        berufPartner_1_1,
  //        berufPartner_1_3,
  //        berufPartner_1_8,
  //        berufPartner_1_9,
  //        berufPartner_2_0,
  //        berufPartner_2_1,
  //        berufPartner_2_4,
  //        berufPartner_2_9,
  //        berufPartner_3_5,
  //        berufPartner_3_6,
  //        berufPartner_3_7,
  //        berufPartner_3_9,
  //        berufPartner_4_1,
  //        berufPartner_4_2,
  //        berufPartner_4_7,
  //        berufPartner_5_0,
  //        berufPartner_5_2,
  //        berufPartner_5_8,
  //        berufPartner_6_2,
  //        berufPartner_6_4,
  //        berufPartner_6_8,
  //        berufPartner_7_0,
  //        berufPartnerSES,
  //        berufSES,
  //        einkommenKategorie,
  //        einkommenParameterKategorie,
  //        einkommenHaushalt,
  //        einkommensgruppeHaushalt,
  //        einkommensgruppeHaushalt_150,
  //        einkommensgruppeHaushalt_150_400,
  //        einkommensgruppeHaushalt_400_500,
  //        einkommensgruppeHaushalt_500_750,
  //        einkommensgruppeHaushalt_750_1000,
  //        einkommensgruppeHaushalt_1000_1250,
  //        einkommensgruppeHaushalt_1250_1500,
  //        einkommensgruppeHaushalt_1500_1750,
  //        einkommensgruppeHaushalt_1750_2000,
  //        einkommensgruppeHaushalt_2000_2250,
  //        einkommensgruppeHaushalt_2250_2500,
  //        einkommensgruppeHaushalt_2500_2750,
  //        einkommensgruppeHaushalt_2750_3000,
  //        einkommensgruppeHaushalt_3000_3250,
  //        einkommensgruppeHaushalt_3250_3500,
  //        einkommensgruppeHaushalt_3500_3750,
  //        einkommensgruppeHaushalt_3750_4000,
  //        einkommensgruppeHaushalt_4000_4500,
  //        einkommensgruppeHaushalt_4500_5000,
  //        einkommensgruppeHaushalt_5000_5500,
  //        einkommensgruppeHaushalt_5500_6000,
  //        einkommensgruppeHaushalt_6000_7500,
  //        einkommensgruppeHaushalt_7500_10000,
  //        einkommensgruppeHaushalt_10000_20000,
  //        einkommensgruppeHaushalt_20000,
  //        einkommenHaushaltSES,
  //        einkommenEigenes,
  //        einkommensgruppeEigenes,
  //        einkommensgruppeEigenes_150,
  //        einkommensgruppeEigenes_150_400,
  //        einkommensgruppeEigenes_400_500,
  //        einkommensgruppeEigenes_500_750,
  //        einkommensgruppeEigenes_750_1000,
  //        einkommensgruppeEigenes_1000_1250,
  //        einkommensgruppeEigenes_1250_1500,
  //        einkommensgruppeEigenes_1500_1750,
  //        einkommensgruppeEigenes_1750_2000,
  //        einkommensgruppeEigenes_2000_2250,
  //        einkommensgruppeEigenes_2250_2500,
  //        einkommensgruppeEigenes_2500_2750,
  //        einkommensgruppeEigenes_2750_3000,
  //        einkommensgruppeEigenes_3000_3250,
  //        einkommensgruppeEigenes_3250_3500,
  //        einkommensgruppeEigenes_3500_3750,
  //        einkommensgruppeEigenes_3750_4000,
  //        einkommensgruppeEigenes_4000_4500,
  //        einkommensgruppeEigenes_4500_5000,
  //        einkommensgruppeEigenes_5000_5500,
  //        einkommensgruppeEigenes_5500_6000,
  //        einkommensgruppeEigenes_6000_7500,
  //        einkommensgruppeEigenes_7500_10000,
  //        einkommensgruppeEigenes_10000_20000,
  //        einkommensgruppeEigenes_20000,
  //        einkommenEigenesSES,
  //        aequivalenzeinkommenSES,
  //        aequivalenzeinkommenSES_1_0,
  //        aequivalenzeinkommenSES_1_5,
  //        aequivalenzeinkommenSES_2_0,
  //        aequivalenzeinkommenSES_2_5,
  //        aequivalenzeinkommenSES_3_0,
  //        aequivalenzeinkommenSES_3_5,
  //        aequivalenzeinkommenSES_4_0,
  //        aequivalenzeinkommenSES_4_5,
  //        aequivalenzeinkommenSES_5_0,
  //        aequivalenzeinkommenSES_5_5,
  //        aequivalenzeinkommenSES_6_0,
  //        aequivalenzeinkommenSES_6_5,
  //        aequivalenzeinkommenSES_7_0,
  //        bedgew,
  //        einkommenSES,
  //        h0000071,
  //        h0000072,
  //        haushaltseinkommenSES,
  //        haushaltsgroesse,
  //        juenger15,
  //        SES,
  //        SES_Q1,
  //        SES_Q2,
  //        SES_Q3,
  //        SES_Q4,
  //        SES_Q5,
  //        SES_Niedrig,
  //        SES_Mittel,
  //        SES_Hoch);
  //  }

  @Test
  void test1() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("1", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("1", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("1", "einkommenHaushaltSES", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("1", "h0000071", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("1", "h0000072", null));
    assertEquals(new BigDecimal("1.8000"), rs.getNumberValue("1", "bedgew", null));
    assertEquals(
        new BigDecimal("1666.67"), rs.getNumberValue("1", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("5.0"), rs.getNumberValue("1", "einkommenSES", null));
    assertEquals(new BigDecimal("13.8"), rs.getNumberValue("1", "SES", null));
  }

  @Test
  void test2() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("1.0"), rs.getNumberValue("2", "bildungSES", null));
    assertEquals(new BigDecimal("2.9"), rs.getNumberValue("2", "berufSES", null));
    assertEquals(new BigDecimal("1500.000"), rs.getNumberValue("2", "einkommenEigenesSES", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("2", "h0000071", null));
    assertEquals(new BigDecimal("0.000"), rs.getNumberValue("2", "h0000072", null));
    assertEquals(new BigDecimal("1.0000"), rs.getNumberValue("2", "bedgew", null));
    assertEquals(
        new BigDecimal("1500.00"), rs.getNumberValue("2", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("4.0"), rs.getNumberValue("2", "einkommenSES", null));
    assertEquals(new BigDecimal("7.9"), rs.getNumberValue("2", "SES", null));
  }

  @Test
  void test3() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("6.1"), rs.getNumberValue("3", "bildungSES", null));
    assertEquals(new BigDecimal("3.7"), rs.getNumberValue("3", "berufSES", null));
    assertEquals(new BigDecimal("450"), rs.getNumberValue("3", "einkommenHaushaltSES", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("3", "h0000071", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("3", "h0000072", null));
    assertEquals(new BigDecimal("1.8000"), rs.getNumberValue("3", "bedgew", null));
    assertEquals(new BigDecimal("250.00"), rs.getNumberValue("3", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("1.0"), rs.getNumberValue("3", "einkommenSES", null));
    assertEquals(new BigDecimal("10.8"), rs.getNumberValue("3", "SES", null));
  }

  @Test
  void test4() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(null, rs.getNumberValue("4", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("4", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("4", "einkommenHaushaltSES", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("4", "h0000071", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("4", "h0000072", null));
    assertEquals(new BigDecimal("1.8000"), rs.getNumberValue("4", "bedgew", null));
    assertEquals(
        new BigDecimal("1666.67"), rs.getNumberValue("4", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("5.0"), rs.getNumberValue("4", "einkommenSES", null));
    assertEquals(new BigDecimal("15.06666666666667"), rs.getNumberValue("4", "SES", null));
  }

  @Test
  void test5() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("5", "bildungSES", null));
    assertEquals(null, rs.getNumberValue("5", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("5", "einkommenHaushaltSES", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("5", "h0000071", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("5", "h0000072", null));
    assertEquals(new BigDecimal("1.8000"), rs.getNumberValue("5", "bedgew", null));
    assertEquals(
        new BigDecimal("1666.67"), rs.getNumberValue("5", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("5.0"), rs.getNumberValue("5", "einkommenSES", null));
    assertEquals(new BigDecimal("11.33333333333333"), rs.getNumberValue("5", "SES", null));
  }

  @Test
  void test6() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("6", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("6", "berufSES", null));
    assertEquals(null, rs.getNumberValue("6", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("6", "h0000071", null));
    assertEquals(null, rs.getNumberValue("6", "h0000072", null));
    assertEquals(null, rs.getNumberValue("6", "bedgew", null));
    assertEquals(null, rs.getNumberValue("6", "aequivalenzeinkommenSES", null));
    assertEquals(null, rs.getNumberValue("6", "einkommenSES", null));
    assertEquals(new BigDecimal("12.4"), rs.getNumberValue("6", "SES", null));
  }

  @Test
  void test7() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("7", "bildungSES", null));
    assertEquals(new BigDecimal("2.9"), rs.getNumberValue("7", "berufSES", null));
    assertEquals(new BigDecimal("1500.000"), rs.getNumberValue("7", "einkommenEigenesSES", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("7", "h0000071", null));
    assertEquals(new BigDecimal("0.000"), rs.getNumberValue("7", "h0000072", null));
    assertEquals(new BigDecimal("1.0000"), rs.getNumberValue("7", "bedgew", null));
    assertEquals(
        new BigDecimal("1500.00"), rs.getNumberValue("7", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("4.0"), rs.getNumberValue("7", "einkommenSES", null));
    assertEquals(new BigDecimal("9.9"), rs.getNumberValue("7", "SES", null));
  }

  @Test
  void test8() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("2.8"), rs.getNumberValue("8", "bildungSES", null));
    assertEquals(new BigDecimal("2.9"), rs.getNumberValue("8", "berufSES", null));
    assertEquals(new BigDecimal("1500.000"), rs.getNumberValue("8", "einkommenEigenesSES", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("8", "h0000071", null));
    assertEquals(new BigDecimal("0.000"), rs.getNumberValue("8", "h0000072", null));
    assertEquals(new BigDecimal("1.0000"), rs.getNumberValue("8", "bedgew", null));
    assertEquals(
        new BigDecimal("1500.00"), rs.getNumberValue("8", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("4.0"), rs.getNumberValue("8", "einkommenSES", null));
    assertEquals(new BigDecimal("9.7"), rs.getNumberValue("8", "SES", null));
  }

  @Test
  void test9() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("9", "bildungSES", null));
    assertEquals(null, rs.getNumberValue("9", "berufEigenerSES", null));
    assertEquals(new BigDecimal("3.5"), rs.getNumberValue("9", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("9", "einkommenHaushaltSES", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("9", "h0000071", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("9", "h0000072", null));
    assertEquals(new BigDecimal("1.8000"), rs.getNumberValue("9", "bedgew", null));
    assertEquals(
        new BigDecimal("1666.67"), rs.getNumberValue("9", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("5.0"), rs.getNumberValue("9", "einkommenSES", null));
    assertEquals(new BigDecimal("11.5"), rs.getNumberValue("9", "SES", null));
  }

  @Test
  void test10() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("10", "bildungSES", null));
    assertEquals(null, rs.getNumberValue("10", "berufEigenerSES", null));
    assertEquals(new BigDecimal("3.5"), rs.getNumberValue("10", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("10", "einkommenHaushaltSES", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("10", "h0000071", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("10", "h0000072", null));
    assertEquals(new BigDecimal("1.8000"), rs.getNumberValue("10", "bedgew", null));
    assertEquals(
        new BigDecimal("1666.67"), rs.getNumberValue("10", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("5.0"), rs.getNumberValue("10", "einkommenSES", null));
    assertEquals(new BigDecimal("11.5"), rs.getNumberValue("10", "SES", null));
  }

  @Test
  void test11() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("1.0"), rs.getNumberValue("11", "bildungSES", null));
    assertEquals(null, rs.getNumberValue("11", "berufSES", null));
    assertEquals(new BigDecimal("1500.000"), rs.getNumberValue("11", "einkommenEigenesSES", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("11", "h0000071", null));
    assertEquals(new BigDecimal("0.000"), rs.getNumberValue("11", "h0000072", null));
    assertEquals(new BigDecimal("1.0000"), rs.getNumberValue("11", "bedgew", null));
    assertEquals(
        new BigDecimal("1500.00"), rs.getNumberValue("11", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("4.0"), rs.getNumberValue("11", "einkommenSES", null));
    assertEquals(new BigDecimal("7.333333333333333"), rs.getNumberValue("11", "SES", null));
  }

  @Test
  void test12() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("12", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("12", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("12", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("12", "h0000071", null));
    assertEquals(new BigDecimal("0"), rs.getNumberValue("12", "h0000072", null));
    assertEquals(null, rs.getNumberValue("12", "bedgew", null));
    assertEquals(null, rs.getNumberValue("12", "aequivalenzeinkommenSES", null));
    assertEquals(null, rs.getNumberValue("12", "einkommenSES", null));
    assertEquals(new BigDecimal("12.4"), rs.getNumberValue("12", "SES", null));
  }

  @Test
  void test13() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("13", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("13", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("13", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("13", "h0000071", null));
    assertEquals(new BigDecimal("0"), rs.getNumberValue("13", "h0000072", null));
    assertEquals(null, rs.getNumberValue("13", "bedgew", null));
    assertEquals(null, rs.getNumberValue("13", "aequivalenzeinkommenSES", null));
    assertEquals(null, rs.getNumberValue("13", "einkommenSES", null));
    assertEquals(new BigDecimal("12.4"), rs.getNumberValue("13", "SES", null));
  }

  @Test
  void test14() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("14", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("14", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("14", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("14", "h0000071", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("14", "h0000072", null));
    assertEquals(null, rs.getNumberValue("14", "bedgew", null));
    assertEquals(null, rs.getNumberValue("14", "aequivalenzeinkommenSES", null));
    assertEquals(null, rs.getNumberValue("14", "einkommenSES", null));
    assertEquals(new BigDecimal("12.4"), rs.getNumberValue("14", "SES", null));
  }

  @Test
  void test15() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("15", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("15", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("15", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("15", "h0000071", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("15", "h0000072", null));
    assertEquals(null, rs.getNumberValue("15", "bedgew", null));
    assertEquals(null, rs.getNumberValue("15", "aequivalenzeinkommenSES", null));
    assertEquals(null, rs.getNumberValue("15", "einkommenSES", null));
    assertEquals(new BigDecimal("12.4"), rs.getNumberValue("15", "SES", null));
  }

  @Test
  void test16() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("16", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("16", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("16", "einkommenHaushaltSES", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("16", "h0000071", null));
    assertEquals(new BigDecimal("0"), rs.getNumberValue("16", "h0000072", null));
    assertEquals(new BigDecimal("2.0000"), rs.getNumberValue("16", "bedgew", null));
    assertEquals(
        new BigDecimal("1500.00"), rs.getNumberValue("16", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("4.0"), rs.getNumberValue("16", "einkommenSES", null));
    assertEquals(new BigDecimal("12.8"), rs.getNumberValue("16", "SES", null));
  }

  @Test
  void test17() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("3.0"), rs.getNumberValue("17", "bildungSES", null));
    assertEquals(new BigDecimal("5.8"), rs.getNumberValue("17", "berufSES", null));
    assertEquals(new BigDecimal("3000.000"), rs.getNumberValue("17", "einkommenHaushaltSES", null));
    assertEquals(new BigDecimal("3.000"), rs.getNumberValue("17", "h0000071", null));
    assertEquals(new BigDecimal("2.000"), rs.getNumberValue("17", "h0000072", null));
    assertEquals(new BigDecimal("1.6000"), rs.getNumberValue("17", "bedgew", null));
    assertEquals(
        new BigDecimal("1875.00"), rs.getNumberValue("17", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("5.5"), rs.getNumberValue("17", "einkommenSES", null));
    assertEquals(new BigDecimal("14.3"), rs.getNumberValue("17", "SES", null));
  }

  @Test
  void test18() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(null, rs.getNumberValue("18", "bildungSES", null));
    assertEquals(null, rs.getNumberValue("18", "berufSES", null));
    assertEquals(null, rs.getNumberValue("18", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("18", "h0000071", null));
    assertEquals(null, rs.getNumberValue("18", "h0000072", null));
    assertEquals(null, rs.getNumberValue("18", "bedgew", null));
    assertEquals(null, rs.getNumberValue("18", "aequivalenzeinkommenSES", null));
    assertEquals(null, rs.getNumberValue("18", "einkommenSES", null));
    assertEquals(null, rs.getNumberValue("18", "SES", null));
  }

  @Test
  void test19() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(new BigDecimal("1.0"), rs.getNumberValue("19", "bildungSES", null));
    assertEquals(new BigDecimal("2.9"), rs.getNumberValue("19", "berufSES", null));
    assertEquals(new BigDecimal("1500.000"), rs.getNumberValue("19", "einkommenEigenesSES", null));
    assertEquals(new BigDecimal("1.000"), rs.getNumberValue("19", "h0000071", null));
    assertEquals(new BigDecimal("0.000"), rs.getNumberValue("19", "h0000072", null));
    assertEquals(new BigDecimal("1.0000"), rs.getNumberValue("19", "bedgew", null));
    assertEquals(
        new BigDecimal("1500.00"), rs.getNumberValue("19", "aequivalenzeinkommenSES", null));
    assertEquals(new BigDecimal("4.0"), rs.getNumberValue("19", "einkommenSES", null));
    assertEquals(new BigDecimal("7.9"), rs.getNumberValue("19", "SES", null));
  }

  private ResultSet search() throws InstantiationException {
    Que q =
        new Que(
                CONFIG,
                bildungParameterKategorie,
                bildungKategorie,
                schule,
                schule1,
                schule2,
                schule3,
                schule4,
                schule5,
                schule6,
                schule7,
                schule12,
                schule123,
                schule45,
                schule67,
                ausbildung1,
                ausbildung2,
                ausbildung3,
                ausbildung4,
                ausbildung5,
                ausbildung6,
                ausbildung7,
                ausbildung,
                ausbildungS1,
                ausbildungS2,
                ausbildungS3,
                ausbildungS4,
                ausbildungS5,
                ausbildungS6,
                ausbildungS7,
                ausbildung12,
                ausbildung345,
                bildungSES,
                bildungSES_1_0,
                bildungSES_1_7,
                bildungSES_2_8,
                bildungSES_3_0,
                bildungSES_3_6,
                bildungSES_3_7,
                bildungSES_4_55,
                bildungSES_4_8,
                bildungSES_4_85,
                bildungSES_5_0,
                bildungSES_5_3,
                bildungSES_6_1,
                bildungSES_7_0,
                berufParameterKategorie,
                berufKategorie,
                erwerbstaetigkeit,
                erwerbstaetig,
                fruehereErwerbstaetigkeit,
                frueherErwerbstaetig,
                familienstand,
                verheiratet,
                partnerschaft,
                lebenZusammen,
                berufEigener,
                berufEigener_1_0,
                berufEigener_1_1,
                berufEigener_1_3,
                berufEigener_1_8,
                berufEigener_1_9,
                berufEigener_2_0,
                berufEigener_2_1,
                berufEigener_2_4,
                berufEigener_2_9,
                berufEigener_3_5,
                berufEigener_3_6,
                berufEigener_3_7,
                berufEigener_3_9,
                berufEigener_4_1,
                berufEigener_4_2,
                berufEigener_4_7,
                berufEigener_5_0,
                berufEigener_5_2,
                berufEigener_5_8,
                berufEigener_6_2,
                berufEigener_6_4,
                berufEigener_6_8,
                berufEigener_7_0,
                berufEigenerSES,
                berufPartner,
                berufPartner_1_0,
                berufPartner_1_1,
                berufPartner_1_3,
                berufPartner_1_8,
                berufPartner_1_9,
                berufPartner_2_0,
                berufPartner_2_1,
                berufPartner_2_4,
                berufPartner_2_9,
                berufPartner_3_5,
                berufPartner_3_6,
                berufPartner_3_7,
                berufPartner_3_9,
                berufPartner_4_1,
                berufPartner_4_2,
                berufPartner_4_7,
                berufPartner_5_0,
                berufPartner_5_2,
                berufPartner_5_8,
                berufPartner_6_2,
                berufPartner_6_4,
                berufPartner_6_8,
                berufPartner_7_0,
                berufPartnerSES,
                berufSES,
                einkommenKategorie,
                einkommenParameterKategorie,
                einkommenHaushalt,
                einkommensgruppeHaushalt,
                einkommensgruppeHaushalt_150,
                einkommensgruppeHaushalt_150_400,
                einkommensgruppeHaushalt_400_500,
                einkommensgruppeHaushalt_500_750,
                einkommensgruppeHaushalt_750_1000,
                einkommensgruppeHaushalt_1000_1250,
                einkommensgruppeHaushalt_1250_1500,
                einkommensgruppeHaushalt_1500_1750,
                einkommensgruppeHaushalt_1750_2000,
                einkommensgruppeHaushalt_2000_2250,
                einkommensgruppeHaushalt_2250_2500,
                einkommensgruppeHaushalt_2500_2750,
                einkommensgruppeHaushalt_2750_3000,
                einkommensgruppeHaushalt_3000_3250,
                einkommensgruppeHaushalt_3250_3500,
                einkommensgruppeHaushalt_3500_3750,
                einkommensgruppeHaushalt_3750_4000,
                einkommensgruppeHaushalt_4000_4500,
                einkommensgruppeHaushalt_4500_5000,
                einkommensgruppeHaushalt_5000_5500,
                einkommensgruppeHaushalt_5500_6000,
                einkommensgruppeHaushalt_6000_7500,
                einkommensgruppeHaushalt_7500_10000,
                einkommensgruppeHaushalt_10000_20000,
                einkommensgruppeHaushalt_20000,
                einkommenHaushaltSES,
                einkommenEigenes,
                einkommensgruppeEigenes,
                einkommensgruppeEigenes_150,
                einkommensgruppeEigenes_150_400,
                einkommensgruppeEigenes_400_500,
                einkommensgruppeEigenes_500_750,
                einkommensgruppeEigenes_750_1000,
                einkommensgruppeEigenes_1000_1250,
                einkommensgruppeEigenes_1250_1500,
                einkommensgruppeEigenes_1500_1750,
                einkommensgruppeEigenes_1750_2000,
                einkommensgruppeEigenes_2000_2250,
                einkommensgruppeEigenes_2250_2500,
                einkommensgruppeEigenes_2500_2750,
                einkommensgruppeEigenes_2750_3000,
                einkommensgruppeEigenes_3000_3250,
                einkommensgruppeEigenes_3250_3500,
                einkommensgruppeEigenes_3500_3750,
                einkommensgruppeEigenes_3750_4000,
                einkommensgruppeEigenes_4000_4500,
                einkommensgruppeEigenes_4500_5000,
                einkommensgruppeEigenes_5000_5500,
                einkommensgruppeEigenes_5500_6000,
                einkommensgruppeEigenes_6000_7500,
                einkommensgruppeEigenes_7500_10000,
                einkommensgruppeEigenes_10000_20000,
                einkommensgruppeEigenes_20000,
                einkommenEigenesSES,
                aequivalenzeinkommenSES,
                aequivalenzeinkommenSES_1_0,
                aequivalenzeinkommenSES_1_5,
                aequivalenzeinkommenSES_2_0,
                aequivalenzeinkommenSES_2_5,
                aequivalenzeinkommenSES_3_0,
                aequivalenzeinkommenSES_3_5,
                aequivalenzeinkommenSES_4_0,
                aequivalenzeinkommenSES_4_5,
                aequivalenzeinkommenSES_5_0,
                aequivalenzeinkommenSES_5_5,
                aequivalenzeinkommenSES_6_0,
                aequivalenzeinkommenSES_6_5,
                aequivalenzeinkommenSES_7_0,
                bedgew,
                einkommenSES,
                h0000071,
                h0000072,
                haushaltseinkommenSES,
                haushaltsgroesse,
                juenger15,
                SES,
                SES_Q1,
                SES_Q2,
                SES_Q3,
                SES_Q4,
                SES_Q5,
                SES_Niedrig,
                SES_Mittel,
                SES_Hoch)
            .pro(SES);

    ResultSet rs = q.execute();
    System.out.println(rs);

    //    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.out.println(new CSV().toStringWideTable(rs, q.getEntities(), q.getQuery()));
    //    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    return rs;
  }
}
