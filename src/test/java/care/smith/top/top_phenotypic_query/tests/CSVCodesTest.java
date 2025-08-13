package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.Entity;
import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Power;
import care.smith.top.top_phenotypic_query.converter.csv.CSV;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import org.junit.jupiter.api.Test;

public class CSVCodesTest {

  private static Phenotype age = new Phe("age", "http://loinc.org", "30525-0").number("a").get();
  private static Phenotype young = new Phe("young").restriction(age, Res.lt(18)).get();
  private static Phenotype old = new Phe("old").titleDe("Alt").restriction(age, Res.ge(18)).get();

  private static Phenotype sex =
      new Phe("sex", "http://loinc.org", "46098-0").titleDe("Geschlecht").string().get();
  private static Phenotype female = new Phe("female").restriction(sex, Res.of("female")).get();
  private static Phenotype male = new Phe("male").restriction(sex, Res.of("male")).get();

  private static Phenotype weight =
      new Phe("weight", "http://loinc.org", "3141-9")
          .code("http://snomed.info/sct", "27113001")
          .number("kg")
          .titleDe("Gewicht")
          .synonymDe("Gewicht Synonym 1")
          .synonymDe("Gewicht Synonym 2")
          .synonymEn("Weight Synonym 1")
          .synonymEn("Weight Synonym 2")
          .descriptionDe("Gewicht Description 1")
          .descriptionDe("Gewicht Description 2")
          .descriptionEn("Weight Description 1")
          .descriptionEn("Weight Description 2")
          .get();

  private static Phenotype height =
      new Phe("height", "http://loinc.org", "3137-7")
          .code("http://snomed.info/sct", "1153637007")
          .number("m")
          .get();

  private static Phenotype bmi =
      new Phe("bmi", "http://loinc.org", "39156-5")
          .number("kg/m2")
          .expression(
              Divide.of(Exp.ofEntity("weight"), Power.of(Exp.ofEntity("height"), Exp.of(2))))
          .get();
  private static Phenotype overweight = new Phe("overweight").restriction(bmi, Res.gt(25)).get();

  private static Phenotype dabi =
      new Phe("Dabigatran", "http://fhir.de/CodeSystem/bfarm/atc", "B01AE07")
          .titleDe("Dabi")
          .itemType(ItemType.MEDICATION)
          .bool()
          .get();

  private static Entity[] entities = {
    age, young, old, sex, female, male, weight, height, bmi, overweight, dabi
  };

  //  @Test
  //  public void test() throws IOException, InterruptedException, URISyntaxException {
  //    String uri = "http://top.imise.uni-leipzig.de/api/polar/";
  //    String[] aes = {
  //      "acute_kidney_injury",
  //      "anaemie",
  //      "respiratory_depression_a",
  //      "bleeding_outside_the_gastrointestinal_tract",
  //      "delir",
  //      "exsiccosis_dehydration",
  //      "blutungen_und_perforationen_des_oberen_git",
  //      "decompensated_heart_failure",
  //      "hyperkalaemia",
  //      "hypoglykaemie",
  //      "hypokaliaemie",
  //      "hyponatraemia",
  //      "hypotension",
  //      "fall__injuries__v2",
  //      "syncope_and_collpase"
  //    };
  //
  //    for (String ae : aes) {
  //      System.out.println(ae);
  //      System.out.println("--------------------------");
  //      Entities phens = Entities.of(uri + ae);
  //      CSV csv = new CSV();
  //      System.out.println(csv.toStringCodes(phens.getEntitiesArray()));
  //      csv.writeCodes(
  //          phens.getEntitiesArray(), new FileOutputStream("test_files/ae/" + ae + ".csv"));
  //    }
  //  }

  @Test
  public void testCodes() {
    String codesRequired =
        "algorithm;parameter;type;unit;codes"
            + System.lineSeparator()
            + "bmi;height;observation;m;http://loinc.org|3137-7,http://snomed.info/sct|1153637007"
            + System.lineSeparator()
            + "bmi;weight;observation;kg;http://loinc.org|3141-9,http://snomed.info/sct|27113001"
            + System.lineSeparator();

    assertEquals(codesRequired, new CSV().toStringCodes(entities));
  }
}
