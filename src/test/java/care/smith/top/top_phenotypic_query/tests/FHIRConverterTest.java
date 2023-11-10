package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.top_phenotypic_query.converter.fhir.FHIRConverter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;

class FHIRConverterTest {

  @Test
  void test() {
    var out = new ByteArrayOutputStream();
    ResultSet testSet = BMIAgeTest.getResultSet();
    FHIRConverter.write(testSet, out);
    String s = out.toString();

    System.out.println(out.toString());
  }
}
