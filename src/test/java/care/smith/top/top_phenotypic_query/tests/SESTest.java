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
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Subtract;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Sum;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.And;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Not;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Eq;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Gt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Lt;
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
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SESTest {

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
          .expression(
              If.of(Or.of(Empty.of(schule), In.of(schule, 95, 98, 99)), Exp.of(-1), Exp.of(schule)))
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
                  Exp.of(1),
                  Exp.of(-1)))
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
                  And.of(Eq.of(schuleSES, 0), In.of(ausbildungSES, 1, 2)),
                  Exp.of(1.0),
                  And.of(Eq.of(schuleSES, 0), In.of(ausbildungSES, 3, 4, 5)),
                  Exp.of(3.0),
                  And.of(Eq.of(schuleSES, 0), Eq.of(ausbildungSES, 6)),
                  Exp.of(6.1),
                  And.of(Eq.of(schuleSES, 0), Eq.of(ausbildungSES, 7)),
                  Exp.of(7.0),
                  And.of(Eq.of(ausbildungSES, 0), In.of(schuleSES, 1, 2)),
                  Exp.of(1.0),
                  And.of(Eq.of(ausbildungSES, 0), Eq.of(schuleSES, 3)),
                  Exp.of(1.7),
                  And.of(Eq.of(ausbildungSES, 0), In.of(schuleSES, 4, 5)),
                  Exp.of(2.8),
                  And.of(Eq.of(ausbildungSES, 0), In.of(schuleSES, 6, 7)),
                  Exp.of(3.7),
                  Exp.of(-1)))
          .get();

  private static Phenotype bildungSESvorhanden =
      new Phe("bildungSESvorhanden")
          .titleDe("bildungSESvorhanden")
          .restriction(bildungSES, Res.gt(-1))
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
              Switch.of(
                  Or.of(
                      And.of(Not.of(erwerbstaetig), Not.of(frueherErwerbstaetig)),
                      Empty.of(berufEigener),
                      In.of(berufEigener, "97", "98", "99")),
                  Exp.of(-1),
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
                  Exp.of(2.0),
                  Exp.of(-1)))
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
              Switch.of(
                  Or.of(
                      And.of(Not.of(verheiratet), Not.of(lebenZusammen)),
                      Empty.of(berufPartner),
                      In.of(berufPartner, "97", "98", "99")),
                  Exp.of(-1),
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
                  Exp.of(2.0),
                  Exp.of(-1)))
          .get();

  private static Phenotype berufSES =
      new Phe("berufSES")
          .titleDe("berufSES")
          .expression(Max.of(berufEigenerSES, berufPartnerSES))
          .get();

  private static Phenotype berufSESvorhanden =
      new Phe("berufSESvorhanden")
          .titleDe("berufSESvorhanden")
          .restriction(berufSES, Res.gt(-1))
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
                  Exp.of(20000),
                  Exp.of(-1)))
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
                  Exp.of(20000),
                  Exp.of(-1)))
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
              Switch.of(
                  Or.of(Lt.of(haushaltsgroesse, juenger15), Gt.of(haushaltsgroesse, 12)),
                  Exp.of(-1),
                  Or.of(Empty.of(juenger15), In.of(juenger15, 98, 99)),
                  Exp.of(0),
                  Exp.of(juenger15)))
          .get();

  private static Phenotype h0000071 =
      new Phe("h0000071")
          .titleDe("h0000071")
          .expression(
              Switch.of(
                  Or.of(
                      Empty.of(haushaltsgroesse),
                      Lt.of(haushaltsgroesse, juenger15),
                      Gt.of(haushaltsgroesse, 12)),
                  Exp.of(-1),
                  Eq.of(haushaltsgroesse, h0000072),
                  Add.of(haushaltsgroesse, 1),
                  Exp.of(haushaltsgroesse)))
          .get();

  private static Phenotype bedgew =
      new Phe("bedgew")
          .titleDe("bedgew")
          .expression(
              If.of(
                  And.of(Ge.of(h0000071, 1), Ge.of(h0000072, 0)),
                  Sum.of(
                      Exp.of(1.0),
                      Multiply.of(h0000072, 0.3),
                      Multiply.of(
                          Subtract.of(Subtract.of(h0000071, h0000072), Exp.of(1.0)), Exp.of(0.5))),
                  Exp.of(-1)))
          .get();

  private static Phenotype aequivalenzeinkommenSES =
      new Phe("aequivalenzeinkommenSES")
          .titleDe("aequivalenzeinkommenSES")
          .expression(
              If.of(
                  And.of(Ge.of(bedgew, 1), Gt.of(haushaltseinkommenSES, -1)),
                  Divide.of(haushaltseinkommenSES, bedgew),
                  Exp.of(-1)))
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
                  Exp.of(7.0),
                  Exp.of(-1)))
          .get();

  private static Phenotype einkommenSESvorhanden =
      new Phe("einkommenSESvorhanden")
          .titleDe("einkommenSESvorhanden")
          .restriction(einkommenSES, Res.gt(-1))
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
        .insertSbj(0, "2001-01-01", "male")
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

    WRITER.printSbj();
    WRITER.printPhe();
  }

  @AfterAll
  static void afterAll() {
    WRITER.close();
  }

  @Test
  void test() throws InstantiationException {
    ResultSet rs = search();
    assertEquals(Set.of("0"), rs.getSubjectIds());
    assertEquals(new BigDecimal("13.3"), rs.getNumberValue("0", "SES", null));
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
