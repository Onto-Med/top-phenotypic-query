package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Switch;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Max;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.And;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Not;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Eq;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Empty;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Exists;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.In;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.DefaultSqlWriter;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class SESTest {

  private static final DataAdapterConfig CONFIG =
      DataAdapterConfig.getInstanceFromResource("config/Default_SQL_Adapter.yml");

  private static final DefaultSqlWriter WRITER = new DefaultSqlWriter(CONFIG);

  // Bildung

  private static Phenotype schule =
      new Phe("schule", "ses", "schule")
          .itemType(ItemType.OBSERVATION)
          .titleDe("schule")
          .number()
          .get();

  private static Phenotype schuleSES =
      new Phe("schuleSES")
          .titleDe("schuleSES")
          .expression(
              If.of(Or.of(Empty.of(schule), In.of(schule, 95, 98, 99)), Exp.of(0), Exp.of(schule)))
          .get();

  private static Phenotype ausbildung1 =
      new Phe("ausbildung1", "ses", "ausbildung1")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung1")
          .number()
          .get();

  private static Phenotype ausbildung2 =
      new Phe("ausbildung2", "ses", "ausbildung2")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung2")
          .number()
          .get();

  private static Phenotype ausbildung3 =
      new Phe("ausbildung3", "ses", "ausbildung3")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung3")
          .number()
          .get();

  private static Phenotype ausbildung4 =
      new Phe("ausbildung4", "ses", "ausbildung4")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung4")
          .number()
          .get();

  private static Phenotype ausbildung5 =
      new Phe("ausbildung5", "ses", "ausbildung5")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung5")
          .number()
          .get();

  private static Phenotype ausbildung6 =
      new Phe("ausbildung6", "ses", "ausbildung6")
          .itemType(ItemType.OBSERVATION)
          .titleDe("ausbildung6")
          .number()
          .get();

  private static Phenotype ausbildung7 =
      new Phe("ausbildung7", "ses", "ausbildung7")
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
                  Exp.of(0)))
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
                  Exp.of(0)))
          .get();

  // Beruf eigener

  private static Phenotype erwerbstaetigkeit =
      new Phe("erwerbstaetigkeit", "ses", "erwerbstaetigkeit")
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
      new Phe("fruehereErwerbstaetigkeit", "ses", "fruehereErwerbstaetigkeit")
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
      new Phe("berufEigener", "ses", "berufEigener")
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
                  Exp.of(0),
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
                  Exp.of(0)))
          .get();

  // Beruf Partner

  private static Phenotype familienstand =
      new Phe("familienstand", "ses", "familienstand")
          .itemType(ItemType.OBSERVATION)
          .titleDe("familienstand")
          .number()
          .get();

  private static Phenotype verheiratet =
      new Phe("verheiratet").titleDe("verheiratet").restriction(familienstand, Res.of(1)).get();

  private static Phenotype partnerschaft =
      new Phe("partnerschaft", "ses", "partnerschaft")
          .itemType(ItemType.OBSERVATION)
          .titleDe("partnerschaft")
          .number()
          .get();

  private static Phenotype lebenZusammen =
      new Phe("lebenZusammen").titleDe("lebenZusammen").restriction(partnerschaft, Res.of(1)).get();

  private static Phenotype berufPartner =
      new Phe("berufPartner", "ses", "berufPartner")
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
                  Exp.of(0),
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
                  Exp.of(0)))
          .get();

  private static Phenotype berufSES =
      new Phe("berufSES")
          .titleDe("berufSES")
          .expression(Max.of(berufEigenerSES, berufPartnerSES))
          .get();

  // Einkommen Haushalt

  private static Phenotype einkommenHaushalt =
      new Phe("einkommenHaushalt", "ses", "einkommenHaushalt")
          .itemType(ItemType.OBSERVATION)
          .titleDe("einkommenHaushalt")
          .number()
          .get();

  private static Phenotype einkommensgruppeHaushalt =
      new Phe("einkommensgruppeHaushalt", "ses", "einkommensgruppeHaushalt")
          .itemType(ItemType.OBSERVATION)
          .titleDe("einkommensgruppeHaushalt")
          .string()
          .get();

  private static Phenotype einkommenHaushaltSES =
      new Phe("einkommenHaushaltSES")
          .titleDe("einkommenHaushalt")
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
                  Exp.of(0)))
          .get();
}
