package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.top_phenotypic_query.converter.fhir.FHIRConverter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import org.junit.jupiter.api.Test;

class FHIRConverterTest {

  @Test
  void test() {
    ResultSet testSet = BMIAgeTest.getResultSet();
    System.out.println(testSet);
    String s = FHIRConverter.toString(testSet);
    System.out.println(s);
  }
}
