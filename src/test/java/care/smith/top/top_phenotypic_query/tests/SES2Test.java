package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
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
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Not;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Eq;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Le;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Empty;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.In;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.DefaultSqlWriter;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SES2Test {

  private static final DataAdapterConfig CONFIG =
      DataAdapterConfig.getInstanceFromResource("config/Default_SQL_Adapter.yml");

  private static final DefaultSqlWriter WRITER = new DefaultSqlWriter(CONFIG);

  // Bildung

  private static Phenotype schule =
      new Phe("schule", "SOZIO", "F0041")
          .itemType(ItemType.OBSERVATION)
          .titleDe("schule")
          .number()
          .get();

  private static Phenotype schuleSES =
      new Phe("schuleSES")
          .titleDe("schuleSES")
          .expression(If.of(Not.of(In.of(schule, 95, 98, 99)), Exp.of(schule)))
          .get();

  private static Phenotype ausbildung1 =
      new Phe("ausbildung1", "SOZIO", "F0045")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung1")
          .number()
          .get();

  private static Phenotype ausbildung2 =
      new Phe("ausbildung2", "SOZIO", "F0046")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung2")
          .number()
          .get();

  private static Phenotype ausbildung3 =
      new Phe("ausbildung3", "SOZIO", "F0047")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung3")
          .number()
          .get();

  private static Phenotype ausbildung4 =
      new Phe("ausbildung4", "SOZIO", "F0048")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung4")
          .number()
          .get();

  private static Phenotype ausbildung5 =
      new Phe("ausbildung5", "SOZIO", "F0049")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung5")
          .number()
          .get();

  private static Phenotype ausbildung6 =
      new Phe("ausbildung6", "SOZIO", "F0050")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung6")
          .number()
          .get();

  private static Phenotype ausbildung7 =
      new Phe("ausbildung7", "SOZIO", "F0051")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung7")
          .number()
          .get();

  private static Phenotype ausbildungSES =
      new Phe("ausbildungSES")
          .titleDe("ausbildungSES")
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
                  Exp.of(1)))
          .get();

  private static Phenotype bildungSES =
      new Phe("bildungSES")
          .titleDe("bildungSES")
          .expression(
              Switch.of(
                  And.of(In.of(schuleSES, 1, 2), In.of(ausbildungSES, 1, 2)),
                  Exp.of(1.0),
                  And.of(Eq.of(schuleSES, 3), In.of(ausbildungSES, 1, 2)),
                  Exp.of(1.7),
                  And.of(In.of(schuleSES, 4, 5), In.of(ausbildungSES, 1, 2)),
                  Exp.of(2.8),
                  And.of(In.of(schuleSES, 1, 2, 3), In.of(ausbildungSES, 3, 4, 5)),
                  Exp.of(3.0),
                  And.of(In.of(schuleSES, 4, 5), In.of(ausbildungSES, 3, 4, 5)),
                  Exp.of(3.6),
                  And.of(In.of(schuleSES, 6, 7), In.of(ausbildungSES, 1, 2)),
                  Exp.of(3.7),
                  And.of(In.of(schuleSES, 6, 7), In.of(ausbildungSES, 3, 4, 5)),
                  Exp.of(4.8),
                  And.of(In.of(schuleSES, 6, 7), Eq.of(ausbildungSES, 6)),
                  Exp.of(6.1),
                  And.of(In.of(schuleSES, 6, 7), Eq.of(ausbildungSES, 7)),
                  Exp.of(7.0),
                  And.of(In.of(schuleSES, 1, 2), Eq.of(ausbildungSES, 6)),
                  Exp.of(3.55),
                  And.of(In.of(schuleSES, 1, 2), Eq.of(ausbildungSES, 7)),
                  Exp.of(4.0),
                  And.of(Eq.of(schuleSES, 3), Eq.of(ausbildungSES, 6)),
                  Exp.of(4.55),
                  And.of(Eq.of(schuleSES, 3), Eq.of(ausbildungSES, 7)),
                  Exp.of(5.00),
                  And.of(In.of(schuleSES, 4, 5), Eq.of(ausbildungSES, 6)),
                  Exp.of(4.85),
                  And.of(In.of(schuleSES, 4, 5), Eq.of(ausbildungSES, 7)),
                  Exp.of(5.3),
                  And.of(Empty.of(schuleSES), In.of(ausbildungSES, 1, 2)),
                  Exp.of(1.0),
                  And.of(Empty.of(schuleSES), In.of(ausbildungSES, 3, 4, 5)),
                  Exp.of(3.0),
                  And.of(Empty.of(schuleSES), Eq.of(ausbildungSES, 6)),
                  Exp.of(6.1),
                  And.of(Empty.of(schuleSES), Eq.of(ausbildungSES, 7)),
                  Exp.of(7.0),
                  And.of(Empty.of(ausbildungSES), In.of(schuleSES, 1, 2)),
                  Exp.of(1.0),
                  And.of(Empty.of(ausbildungSES), Eq.of(schuleSES, 3)),
                  Exp.of(1.7),
                  And.of(Empty.of(ausbildungSES), In.of(schuleSES, 4, 5)),
                  Exp.of(2.8),
                  And.of(Empty.of(ausbildungSES), In.of(schuleSES, 6, 7)),
                  Exp.of(3.7)))
          .get();

  private static Phenotype bildungSESvorhanden =
      new Phe("bildungSESvorhanden")
          .titleDe("bildungSESvorhanden")
          .expression(Exists.of(bildungSES))
          .get();

  // Beruf eigener

  private static Phenotype erwerbstaetigkeit =
      new Phe("erwerbstaetigkeit", "SOZIO", "F0055")
          .itemType(ItemType.OBSERVATION)
          .titleDe("erwerbstaetigkeit")
          .number()
          .get();

  private static Phenotype erwerbstaetig =
      new Phe("erwerbstaetig")
          .titleDe("erwerbstaetig")
          .restriction(erwerbstaetigkeit, Res.geLe(1, 2))
          .get();

  private static Phenotype fruehereErwerbstaetigkeit =
      new Phe("fruehereErwerbstaetigkeit", "SOZIO", "F0070")
          .itemType(ItemType.OBSERVATION)
          .titleDe("fruehereErwerbstaetigkeit")
          .number()
          .get();

  private static Phenotype frueherErwerbstaetig =
      new Phe("frueherErwerbstaetig")
          .titleDe("frueherErwerbstaetig")
          .restriction(fruehereErwerbstaetigkeit, Res.of(1))
          .get();

  private static Phenotype berufEigener =
      new Phe("berufEigener", "SOZIO", "F0072")
          .itemType(ItemType.OBSERVATION)
          .titleDe("berufEigener")
          .string()
          .get();

  private static Phenotype berufEigenerSES =
      new Phe("berufEigenerSES")
          .titleDe("berufEigenerSES")
          .expression(
              If.of(
                  Or.of(erwerbstaetig, frueherErwerbstaetig),
                  Switch.of(
                      In.of(berufEigener, "A", "A1", "A3"),
                      Exp.of(1.0),
                      Eq.of(berufEigener, "A2"),
                      Exp.of(1.1),
                      Eq.of(berufEigener, "B"),
                      Exp.of(6.2),
                      Eq.of(berufEigener, "B1"),
                      Exp.of(5.8),
                      Eq.of(berufEigener, "B2"),
                      Exp.of(6.8),
                      Eq.of(berufEigener, "B3"),
                      Exp.of(7.0),
                      Eq.of(berufEigener, "C"),
                      Exp.of(3.9),
                      Eq.of(berufEigener, "C1"),
                      Exp.of(3.5),
                      In.of(berufEigener, "C2", "E2"),
                      Exp.of(3.6),
                      In.of(berufEigener, "C3", "C4", "E3"),
                      Exp.of(4.2),
                      Eq.of(berufEigener, "D"),
                      Exp.of(5.0),
                      In.of(berufEigener, "D1", "G", "G1", "G2", "G3", "H"),
                      Exp.of(2.9),
                      Eq.of(berufEigener, "D2"),
                      Exp.of(4.1),
                      Eq.of(berufEigener, "D3"),
                      Exp.of(5.2),
                      Eq.of(berufEigener, "D4"),
                      Exp.of(6.4),
                      Eq.of(berufEigener, "E"),
                      Exp.of(3.7),
                      In.of(berufEigener, "E1", "F5"),
                      Exp.of(2.4),
                      Eq.of(berufEigener, "E4"),
                      Exp.of(4.7),
                      Eq.of(berufEigener, "F"),
                      Exp.of(1.9),
                      Eq.of(berufEigener, "F1"),
                      Exp.of(1.3),
                      Eq.of(berufEigener, "F2"),
                      Exp.of(1.8),
                      Eq.of(berufEigener, "F3"),
                      Exp.of(2.1),
                      Eq.of(berufEigener, "F4"),
                      Exp.of(2.0))))
          .get();

  // Beruf Partner

  private static Phenotype familienstand =
      new Phe("familienstand", "SOZIO", "F0026")
          .itemType(ItemType.OBSERVATION)
          .titleDe("familienstand")
          .number()
          .get();

  private static Phenotype verheiratet =
      new Phe("verheiratet").titleDe("verheiratet").restriction(familienstand, Res.of(1)).get();

  private static Phenotype partnerschaft =
      new Phe("partnerschaft", "SOZIO", "F0035")
          .itemType(ItemType.OBSERVATION)
          .titleDe("partnerschaft")
          .number()
          .get();

  private static Phenotype lebenZusammen =
      new Phe("lebenZusammen").titleDe("lebenZusammen").restriction(partnerschaft, Res.of(1)).get();

  private static Phenotype berufPartner =
      new Phe("berufPartner", "SOZIO", "F0078")
          .itemType(ItemType.OBSERVATION)
          .titleDe("berufPartner")
          .string()
          .get();

  private static Phenotype berufPartnerSES =
      new Phe("berufPartnerSES")
          .titleDe("berufPartnerSES")
          .expression(
              If.of(
                  Or.of(verheiratet, lebenZusammen),
                  Switch.of(
                      In.of(berufPartner, "A", "A1", "A3"),
                      Exp.of(1.0),
                      Eq.of(berufPartner, "A2"),
                      Exp.of(1.1),
                      Eq.of(berufPartner, "B"),
                      Exp.of(6.2),
                      Eq.of(berufPartner, "B1"),
                      Exp.of(5.8),
                      Eq.of(berufPartner, "B2"),
                      Exp.of(6.8),
                      Eq.of(berufPartner, "B3"),
                      Exp.of(7.0),
                      Eq.of(berufPartner, "C"),
                      Exp.of(3.9),
                      Eq.of(berufPartner, "C1"),
                      Exp.of(3.5),
                      In.of(berufPartner, "C2", "E2"),
                      Exp.of(3.6),
                      In.of(berufPartner, "C3", "C4", "E3"),
                      Exp.of(4.2),
                      Eq.of(berufPartner, "D"),
                      Exp.of(5.0),
                      In.of(berufPartner, "D1", "G", "G1", "G2", "G3", "H"),
                      Exp.of(2.9),
                      Eq.of(berufPartner, "D2"),
                      Exp.of(4.1),
                      Eq.of(berufPartner, "D3"),
                      Exp.of(5.2),
                      Eq.of(berufPartner, "D4"),
                      Exp.of(6.4),
                      Eq.of(berufPartner, "E"),
                      Exp.of(3.7),
                      In.of(berufPartner, "E1", "F5"),
                      Exp.of(2.4),
                      Eq.of(berufPartner, "E4"),
                      Exp.of(4.7),
                      Eq.of(berufPartner, "F"),
                      Exp.of(1.9),
                      Eq.of(berufPartner, "F1"),
                      Exp.of(1.3),
                      Eq.of(berufPartner, "F2"),
                      Exp.of(1.8),
                      Eq.of(berufPartner, "F3"),
                      Exp.of(2.1),
                      Eq.of(berufPartner, "F4"),
                      Exp.of(2.0))))
          .get();

  private static Phenotype berufSES =
      new Phe("berufSES")
          .titleDe("berufSES")
          .expression(Max.of(berufEigenerSES, berufPartnerSES))
          .get();

  private static Phenotype berufSESvorhanden =
      new Phe("berufSESvorhanden")
          .titleDe("berufSESvorhanden")
          .expression(Exists.of(berufSES))
          .get();

  // Einkommen Haushalt

  private static Phenotype einkommenHaushalt =
      new Phe("einkommenHaushalt", "SOZIO", "F0083")
          .itemType(ItemType.OBSERVATION)
          .titleDe("einkommenHaushalt")
          .number()
          .get();

  private static Phenotype einkommensgruppeHaushalt =
      new Phe("einkommensgruppeHaushalt", "SOZIO", "F0084")
          .itemType(ItemType.OBSERVATION)
          .titleDe("einkommensgruppeHaushalt")
          .string()
          .get();

  private static Phenotype einkommenHaushaltSES =
      new Phe("einkommenHaushaltSES")
          .titleDe("einkommenHaushaltSES")
          .expression(
              Switch.of(
                  Exists.of(einkommenHaushalt),
                  Exp.of(einkommenHaushalt),
                  Eq.of(einkommensgruppeHaushalt, "B"),
                  Exp.of(75),
                  Eq.of(einkommensgruppeHaushalt, "P"),
                  Exp.of(275),
                  Eq.of(einkommensgruppeHaushalt, "T"),
                  Exp.of(450),
                  Eq.of(einkommensgruppeHaushalt, "F"),
                  Exp.of(625),
                  Eq.of(einkommensgruppeHaushalt, "E"),
                  Exp.of(875),
                  Eq.of(einkommensgruppeHaushalt, "H"),
                  Exp.of(1125),
                  Eq.of(einkommensgruppeHaushalt, "L"),
                  Exp.of(1375),
                  Eq.of(einkommensgruppeHaushalt, "N"),
                  Exp.of(1625),
                  Eq.of(einkommensgruppeHaushalt, "R"),
                  Exp.of(1875),
                  Eq.of(einkommensgruppeHaushalt, "M"),
                  Exp.of(2125),
                  Eq.of(einkommensgruppeHaushalt, "S"),
                  Exp.of(2375),
                  Eq.of(einkommensgruppeHaushalt, "K"),
                  Exp.of(2625),
                  Eq.of(einkommensgruppeHaushalt, "O"),
                  Exp.of(2875),
                  Eq.of(einkommensgruppeHaushalt, "C"),
                  Exp.of(3125),
                  Eq.of(einkommensgruppeHaushalt, "G"),
                  Exp.of(3375),
                  Eq.of(einkommensgruppeHaushalt, "U"),
                  Exp.of(3625),
                  Eq.of(einkommensgruppeHaushalt, "J"),
                  Exp.of(3875),
                  Eq.of(einkommensgruppeHaushalt, "V"),
                  Exp.of(4250),
                  Eq.of(einkommensgruppeHaushalt, "A"),
                  Exp.of(4750),
                  Eq.of(einkommensgruppeHaushalt, "Z"),
                  Exp.of(5250),
                  Eq.of(einkommensgruppeHaushalt, "X"),
                  Exp.of(5750),
                  Eq.of(einkommensgruppeHaushalt, "Q"),
                  Exp.of(6750),
                  Eq.of(einkommensgruppeHaushalt, "W"),
                  Exp.of(8750),
                  Eq.of(einkommensgruppeHaushalt, "D"),
                  Exp.of(15000),
                  Eq.of(einkommensgruppeHaushalt, "Y"),
                  Exp.of(20000)))
          .get();

  // Einkommen eigenes

  private static Phenotype einkommenEigenes =
      new Phe("einkommenEigenes", "SOZIO", "F0086")
          .itemType(ItemType.OBSERVATION)
          .titleDe("einkommenEigenes")
          .number()
          .get();

  private static Phenotype einkommensgruppeEigenes =
      new Phe("einkommensgruppeEigenes", "SOZIO", "F0087")
          .itemType(ItemType.OBSERVATION)
          .titleDe("einkommensgruppeEigenes")
          .string()
          .get();

  private static Phenotype einkommenEigenesSES =
      new Phe("einkommenEigenesSES")
          .titleDe("einkommenEigenesSES")
          .expression(
              Switch.of(
                  Exists.of(einkommenEigenes),
                  Exp.of(einkommenEigenes),
                  Eq.of(einkommensgruppeEigenes, "B"),
                  Exp.of(75),
                  Eq.of(einkommensgruppeEigenes, "P"),
                  Exp.of(275),
                  Eq.of(einkommensgruppeEigenes, "T"),
                  Exp.of(450),
                  Eq.of(einkommensgruppeEigenes, "F"),
                  Exp.of(625),
                  Eq.of(einkommensgruppeEigenes, "E"),
                  Exp.of(875),
                  Eq.of(einkommensgruppeEigenes, "H"),
                  Exp.of(1125),
                  Eq.of(einkommensgruppeEigenes, "L"),
                  Exp.of(1375),
                  Eq.of(einkommensgruppeEigenes, "N"),
                  Exp.of(1625),
                  Eq.of(einkommensgruppeEigenes, "R"),
                  Exp.of(1875),
                  Eq.of(einkommensgruppeEigenes, "M"),
                  Exp.of(2125),
                  Eq.of(einkommensgruppeEigenes, "S"),
                  Exp.of(2375),
                  Eq.of(einkommensgruppeEigenes, "K"),
                  Exp.of(2625),
                  Eq.of(einkommensgruppeEigenes, "O"),
                  Exp.of(2875),
                  Eq.of(einkommensgruppeEigenes, "C"),
                  Exp.of(3125),
                  Eq.of(einkommensgruppeEigenes, "G"),
                  Exp.of(3375),
                  Eq.of(einkommensgruppeEigenes, "U"),
                  Exp.of(3625),
                  Eq.of(einkommensgruppeEigenes, "J"),
                  Exp.of(3875),
                  Eq.of(einkommensgruppeEigenes, "V"),
                  Exp.of(4250),
                  Eq.of(einkommensgruppeEigenes, "A"),
                  Exp.of(4750),
                  Eq.of(einkommensgruppeEigenes, "Z"),
                  Exp.of(5250),
                  Eq.of(einkommensgruppeEigenes, "X"),
                  Exp.of(5750),
                  Eq.of(einkommensgruppeEigenes, "Q"),
                  Exp.of(6750),
                  Eq.of(einkommensgruppeEigenes, "W"),
                  Exp.of(8750),
                  Eq.of(einkommensgruppeEigenes, "D"),
                  Exp.of(15000),
                  Eq.of(einkommensgruppeEigenes, "Y"),
                  Exp.of(20000)))
          .get();

  private static Phenotype haushaltsgroesse =
      new Phe("haushaltsgroesse", "SOZIO", "F0079")
          .itemType(ItemType.OBSERVATION)
          .titleDe("haushaltsgroesse")
          .number()
          .get();

  private static Phenotype haushaltseinkommenSES =
      new Phe("haushaltseinkommenSES")
          .titleDe("haushaltseinkommenSES")
          .expression(
              If.of(
                  Eq.of(haushaltsgroesse, 1),
                  Exp.of(einkommenEigenesSES),
                  Exp.of(einkommenHaushaltSES)))
          .get();

  private static Phenotype juenger15 =
      new Phe("juenger15", "SOZIO", "F0080")
          .itemType(ItemType.OBSERVATION)
          .titleDe("juenger15")
          .number()
          .get();

  private static Phenotype h0000072 =
      new Phe("h0000072")
          .titleDe("h0000072")
          .expression(
              If.of(
                  And.of(Ge.of(haushaltsgroesse, juenger15), Le.of(haushaltsgroesse, 12)),
                  If.of(
                      Or.of(Empty.of(juenger15), In.of(juenger15, 98, 99)),
                      Exp.of(0),
                      Exp.of(juenger15))))
          .get();

  private static Phenotype h0000071 =
      new Phe("h0000071")
          .titleDe("h0000071")
          .expression(
              If.of(
                  And.of(Ge.of(haushaltsgroesse, juenger15), Le.of(haushaltsgroesse, 12)),
                  If.of(
                      Eq.of(haushaltsgroesse, h0000072),
                      Add.of(haushaltsgroesse, 1),
                      Exp.of(haushaltsgroesse))))
          .get();

  private static Phenotype bedgew =
      new Phe("bedgew")
          .titleDe("bedgew")
          .expression(
              Sum.of(
                  Exp.of(1.0),
                  Multiply.of(h0000072, 0.3),
                  Multiply.of(
                      Subtract.of(Subtract.of(h0000071, h0000072), Exp.of(1.0)), Exp.of(0.5))))
          .get();

  private static Phenotype aequivalenzeinkommenSES =
      new Phe("aequivalenzeinkommenSES")
          .titleDe("aequivalenzeinkommenSES")
          .expression(Round.of(Divide.of(haushaltseinkommenSES, bedgew), 2))
          .get();

  private static Phenotype aequivalenzeinkommenSES_1_0 =
      new Phe("aequivalenzeinkommenSES_1_0")
          .titleDe("aequivalenzeinkommenSES_1_0")
          .restriction(aequivalenzeinkommenSES, Res.lt(800))
          .get();
  private static Phenotype aequivalenzeinkommenSES_1_5 =
      new Phe("aequivalenzeinkommenSES_1_5")
          .titleDe("aequivalenzeinkommenSES_1_5")
          .restriction(aequivalenzeinkommenSES, Res.geLt(800, 980))
          .get();
  private static Phenotype aequivalenzeinkommenSES_2_0 =
      new Phe("aequivalenzeinkommenSES_2_0")
          .titleDe("aequivalenzeinkommenSES_2_0")
          .restriction(aequivalenzeinkommenSES, Res.geLt(980, 1100))
          .get();
  private static Phenotype aequivalenzeinkommenSES_2_5 =
      new Phe("aequivalenzeinkommenSES_2_5")
          .titleDe("aequivalenzeinkommenSES_2_5")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1100, 1200))
          .get();
  private static Phenotype aequivalenzeinkommenSES_3_0 =
      new Phe("aequivalenzeinkommenSES_3_0")
          .titleDe("aequivalenzeinkommenSES_3_0")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1200, 1333.33))
          .get();
  private static Phenotype aequivalenzeinkommenSES_3_5 =
      new Phe("aequivalenzeinkommenSES_3_5")
          .titleDe("aequivalenzeinkommenSES_3_5")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1333.33, 1400))
          .get();
  private static Phenotype aequivalenzeinkommenSES_4_0 =
      new Phe("aequivalenzeinkommenSES_4_0")
          .titleDe("aequivalenzeinkommenSES_4_0")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1400, 1533.33))
          .get();
  private static Phenotype aequivalenzeinkommenSES_4_5 =
      new Phe("aequivalenzeinkommenSES_4_5")
          .titleDe("aequivalenzeinkommenSES_4_5")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1533.33, 1666.67))
          .get();
  private static Phenotype aequivalenzeinkommenSES_5_0 =
      new Phe("aequivalenzeinkommenSES_5_0")
          .titleDe("aequivalenzeinkommenSES_5_0")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1666.67, 1866.67))
          .get();
  private static Phenotype aequivalenzeinkommenSES_5_5 =
      new Phe("aequivalenzeinkommenSES_5_5")
          .titleDe("aequivalenzeinkommenSES_5_5")
          .restriction(aequivalenzeinkommenSES, Res.geLt(1866.67, 2000))
          .get();
  private static Phenotype aequivalenzeinkommenSES_6_0 =
      new Phe("aequivalenzeinkommenSES_6_0")
          .titleDe("aequivalenzeinkommenSES_6_0")
          .restriction(aequivalenzeinkommenSES, Res.geLt(2000, 2333.33))
          .get();
  private static Phenotype aequivalenzeinkommenSES_6_5 =
      new Phe("aequivalenzeinkommenSES_6_5")
          .titleDe("aequivalenzeinkommenSES_6_5")
          .restriction(aequivalenzeinkommenSES, Res.geLt(2333.33, 3000))
          .get();
  private static Phenotype aequivalenzeinkommenSES_7_0 =
      new Phe("aequivalenzeinkommenSES_7_0")
          .titleDe("aequivalenzeinkommenSES_7_0")
          .restriction(aequivalenzeinkommenSES, Res.ge(3000))
          .get();

  private static Phenotype einkommenSES =
      new Phe("einkommenSES")
          .titleDe("einkommenSES")
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

  private static Phenotype einkommenSESvorhanden =
      new Phe("einkommenSESvorhanden")
          .titleDe("einkommenSESvorhanden")
          .expression(Exists.of(einkommenSES))
          .get();

  // SES

  private static Phenotype SES =
      new Phe("SES")
          .titleDe("SES")
          .expression(
              Switch.of(
                  And.of(bildungSESvorhanden, berufSESvorhanden, einkommenSESvorhanden),
                  Sum.of(bildungSES, berufSES, einkommenSES),
                  And.of(berufSESvorhanden, einkommenSESvorhanden),
                  Sum.of(
                      Exp.of(berufSES),
                      Exp.of(einkommenSES),
                      Avg.of(Exp.of(berufSES), Exp.of(einkommenSES), Exp.of(2))),
                  And.of(bildungSESvorhanden, einkommenSESvorhanden),
                  Sum.of(
                      Exp.of(bildungSES),
                      Exp.of(einkommenSES),
                      Avg.of(Exp.of(bildungSES), Exp.of(einkommenSES), Exp.of(2))),
                  And.of(bildungSESvorhanden, berufSESvorhanden),
                  Sum.of(
                      Exp.of(bildungSES),
                      Exp.of(berufSES),
                      Avg.of(Exp.of(bildungSES), Exp.of(berufSES), Exp.of(2))),
                  Exp.of(-1)))
          .get();

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
    assertEquals(null, rs.getNumberValue("12", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("12", "h0000071", null));
    assertEquals(null, rs.getNumberValue("12", "h0000072", null));
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
    assertEquals(null, rs.getNumberValue("13", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("13", "h0000071", null));
    assertEquals(null, rs.getNumberValue("13", "h0000072", null));
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
    assertEquals(null, rs.getNumberValue("14", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("14", "h0000071", null));
    assertEquals(null, rs.getNumberValue("14", "h0000072", null));
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
    assertEquals(null, rs.getNumberValue("15", "einkommenHaushaltSES", null));
    assertEquals(null, rs.getNumberValue("15", "h0000071", null));
    assertEquals(null, rs.getNumberValue("15", "h0000072", null));
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
    assertEquals(new BigDecimal(-1), rs.getNumberValue("18", "SES", null));
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
                ausbildung1,
                ausbildung2,
                ausbildung3,
                ausbildung4,
                ausbildung5,
                ausbildung6,
                ausbildung7,
                ausbildungSES,
                bedgew,
                berufEigener,
                berufEigenerSES,
                berufPartner,
                berufPartnerSES,
                berufSES,
                berufSESvorhanden,
                bildungSES,
                bildungSESvorhanden,
                einkommenEigenes,
                einkommenEigenesSES,
                einkommenHaushalt,
                einkommenHaushaltSES,
                einkommenSES,
                einkommenSESvorhanden,
                einkommensgruppeEigenes,
                einkommensgruppeHaushalt,
                erwerbstaetig,
                erwerbstaetigkeit,
                familienstand,
                fruehereErwerbstaetigkeit,
                frueherErwerbstaetig,
                h0000071,
                h0000072,
                haushaltseinkommenSES,
                haushaltsgroesse,
                juenger15,
                lebenZusammen,
                partnerschaft,
                schule,
                schuleSES,
                SES,
                verheiratet)
            .pro(SES);

    ResultSet rs = q.execute();
    System.out.println(rs);

    //    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.out.println(new CSV().toStringWideTable(rs, q.getEntities(), q.getQuery()));
    //    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    return rs;
  }
}
