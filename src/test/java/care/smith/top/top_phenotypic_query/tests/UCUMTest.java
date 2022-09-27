package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;

import org.fhir.ucum.UcumException;
import org.junit.jupiter.api.Test;

import care.smith.top.top_phenotypic_query.ucum.UCUM;

public class UCUMTest extends AbstractTest {

  @Test
  public void test1() throws URISyntaxException, FileNotFoundException, UcumException {
    assertEquals(
        BigDecimal.valueOf(1.8),
        UCUM.convert(BigDecimal.valueOf(180), "cm", "m").setScale(1, RoundingMode.HALF_UP));
  }

  @Test
  public void test2() throws URISyntaxException, FileNotFoundException, UcumException {
    assertEquals(
        BigDecimal.valueOf(180.0),
        UCUM.convert(BigDecimal.valueOf(1.8), "m", "cm").setScale(1, RoundingMode.HALF_UP));
  }
}
