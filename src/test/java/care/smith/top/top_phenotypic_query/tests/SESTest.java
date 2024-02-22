package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.model.Expression;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.If;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Switch;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.And;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Eq;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.set.Empty;
import care.smith.top.top_phenotypic_query.tests.default_sql_writer.DefaultSqlWriter;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Arrays;

public class SESTest {

  private static final DataAdapterConfig CONFIG =
      DataAdapterConfig.getInstanceFromResource("config/Default_SQL_Adapter.yml");

  private static final DefaultSqlWriter WRITER = new DefaultSqlWriter(CONFIG);

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
              If.of(
                  Or.of(
                      Empty.of(schule),
                      Eq.of(Exp.of(schule), Exp.of(95)),
                      Eq.of(Exp.of(schule), Exp.of(98)),
                      Eq.of(Exp.of(schule), Exp.of(99))),
                  Exp.of(-1),
                  Exp.of(schule)))
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
                  Eq.of(Exp.of(ausbildung7), Exp.of(1)),
                  Exp.of(7),
                  Eq.of(Exp.of(ausbildung6), Exp.of(1)),
                  Exp.of(6),
                  Eq.of(Exp.of(ausbildung5), Exp.of(1)),
                  Exp.of(5),
                  Eq.of(Exp.of(ausbildung4), Exp.of(1)),
                  Exp.of(4),
                  Eq.of(Exp.of(ausbildung3), Exp.of(1)),
                  Exp.of(3),
                  Eq.of(Exp.of(ausbildung2), Exp.of(1)),
                  Exp.of(2),
                  Eq.of(Exp.of(ausbildung1), Exp.of(1)),
                  Exp.of(1),
                  Exp.of(-1)))
          .get();

  private static Phenotype bildungSES =
      new Phe("bildungSES")
          .titleDe("bildungSES")
          .expression(
              Switch.of(
                  And.of(sch(1, 2), aus(1, 2)),
                  Exp.of(1.0),
                  And.of(sch(3), aus(1, 2)),
                  Exp.of(1.7),
                  And.of(sch(4, 5), aus(1, 2)),
                  Exp.of(2.8),
                  And.of(sch(1, 2, 3), aus(3, 4, 5)),
                  Exp.of(3.0),
                  And.of(sch(4, 5), aus(3, 4, 5)),
                  Exp.of(3.6),
                  And.of(sch(6, 7), aus(1, 2)),
                  Exp.of(3.7),
                  And.of(sch(6, 7), aus(3, 4, 5)),
                  Exp.of(4.8),
                  And.of(sch(6, 7), aus(6)),
                  Exp.of(6.1),
                  And.of(sch(6, 7), aus(7)),
                  Exp.of(7.0),
                  And.of(sch(1, 2), aus(6)),
                  Exp.of(3.55),
                  And.of(sch(1, 2), aus(7)),
                  Exp.of(4.0),
                  And.of(sch(3), aus(6)),
                  Exp.of(4.55),
                  And.of(sch(3), aus(7)),
                  Exp.of(5.00),
                  And.of(sch(4, 5), aus(6)),
                  Exp.of(4.85),
                  And.of(sch(4, 5), aus(7)),
                  Exp.of(5.3),
                  And.of(sch(-1), aus(1, 2)),
                  Exp.of(1.0),
                  And.of(sch(-1), aus(3, 4, 5)),
                  Exp.of(3.0),
                  And.of(sch(-1), aus(6)),
                  Exp.of(6.1),
                  And.of(sch(-1), aus(7)),
                  Exp.of(7.0),
                  And.of(aus(-1), sch(1, 2)),
                  Exp.of(1.0),
                  And.of(aus(-1), sch(3)),
                  Exp.of(1.7),
                  And.of(aus(-1), sch(4, 5)),
                  Exp.of(2.8),
                  And.of(aus(-1), sch(6, 7)),
                  Exp.of(3.7),
                  Exp.of(-1)))
          .get();

  private static Expression sch(Integer... vals) {
    return exp(schuleSES, vals);
  }

  private static Expression aus(Integer... vals) {
    return exp(ausbildungSES, vals);
  }

  private static Expression exp(Phenotype p, Integer... vals) {
    if (vals.length == 1) return Eq.of(Exp.of(p), Exp.of(vals[0]));
    else
      return Or.of(
          Arrays.stream(vals).map(v -> Eq.of(Exp.of(p), Exp.of(v))).toArray(Expression[]::new));
  }

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
}
