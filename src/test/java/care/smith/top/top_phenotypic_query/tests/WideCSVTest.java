package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.Entity;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Power;
import care.smith.top.top_phenotypic_query.converter.csv.CSV;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import org.junit.jupiter.api.Test;

public class WideCSVTest {

  private static Phenotype age =
      new Phe("age", "http://loinc.org", "30525-0").titleEn("Age").number("a").get();
  private static Phenotype young =
      new Phe("young").titleEn("Young").restriction(age, Res.lt(18)).get();
  private static Phenotype old = new Phe("old").titleEn("Old").restriction(age, Res.ge(18)).get();

  private static Phenotype sex =
      new Phe("sex", "http://loinc.org", "46098-0").titleEn("Sex").string().get();
  private static Phenotype female =
      new Phe("female").titleEn("Female").restriction(sex, Res.of("female")).get();
  private static Phenotype male =
      new Phe("male").titleEn("Male").restriction(sex, Res.of("male")).get();

  private static Phenotype weight =
      new Phe("weight", "http://loinc.org", "3141-9")
          .code("http://snomed.info/sct", "27113001")
          .number("kg")
          .titleDe("Gewicht")
          .titleEn("Weight")
          .get();

  private static Phenotype height =
      new Phe("height", "http://loinc.org", "3137-7")
          .code("http://snomed.info/sct", "1153637007")
          .titleEn("Height")
          .number("m")
          .get();

  private static Phenotype bmi =
      new Phe("bmi", "http://loinc.org", "39156-5")
          .titleEn("BMI")
          .number()
          .expression(
              Divide.of(Exp.ofEntity("weight"), Power.of(Exp.ofEntity("height"), Exp.of(2))))
          .get();
  private static Phenotype overweight =
      new Phe("overweight").titleEn("Overweight").restriction(bmi, Res.gt(25)).get();

  private static Phenotype dabi =
      new Phe("Dabigatran", "http://fhir.de/CodeSystem/bfarm/atc", "B01AE07")
          .titleEn("Dabigatran")
          .itemType(ItemType.MEDICATION)
          .bool()
          .get();

  private static Entity[] entities = {
    age, young, old, sex, female, male, weight, height, bmi, overweight, dabi
  };

  @Test
  public void testData() throws InstantiationException {
    ResultSet rs = new ResultSet();

    rs.addValueWithRestriction("1", young, Val.of(15));
    rs.addValueWithRestriction("1", male, Val.of("male"));
    rs.addValue(
        "1",
        weight,
        Res.geLe(DateUtil.parse("2008-01-01"), DateUtil.parse("2008-12-31")),
        Val.of(58, DateUtil.parse("2008-01-01")));
    rs.addValue(
        "1",
        weight,
        Res.geLe(DateUtil.parse("2009-01-01"), DateUtil.parse("2009-12-31")),
        Val.of(59, DateUtil.parse("2009-01-01")));
    rs.addValue(
        "1",
        weight,
        Res.geLe(DateUtil.parse("2010-01-01"), DateUtil.parse("2010-12-31")),
        Val.of(60, DateUtil.parse("2010-01-01")));
    rs.addValue("1", height, null, Val.of(1.8));
    rs.addValue("1", bmi, null, Val.of(18.52));

    rs.addValueWithRestriction("2", old, Val.of(35));
    rs.addValueWithRestriction("2", female, Val.of("female"));
    rs.addValue("2", weight, null, Val.of(80));
    rs.addValue("2", height, null, Val.of(1.65));
    rs.addValueWithRestriction("2", overweight, Val.of(29.38));
    rs.addValue(
        "2", dabi, null, Val.of(true, DateUtil.parse("2010-01-01"), DateUtil.parse("2011-01-31")));

    String dataRequired =
        "Id;Dabigatran;Young;Age;Male;Sex;Old;Overweight;Female;BMI;Weight;Height"
            + System.lineSeparator()
            + "1;false;true;15;true;male;false;false;false;18.52;58|2008-01-01T00:00:00,59|2009-01-01T00:00:00,60|2010-01-01T00:00:00;1.8"
            + System.lineSeparator()
            + "2;true|2010-01-01T00:00:00|2011-01-31T00:00:00;false;35;false;female;true;true;true;29.38;80;1.65"
            + System.lineSeparator();

    Que q =
        new Que("config/Default_SQL_Adapter.yml", entities)
            .pro(dabi)
            .pro(young)
            .pro(male)
            .inc(old)
            .inc(overweight)
            .exc(female);

    String dataActual = new CSV().toStringWideTable(rs, q.getEntities(), q.getQuery());

    System.out.println(dataActual);

    assertEquals(dataRequired, dataActual);
  }
}
