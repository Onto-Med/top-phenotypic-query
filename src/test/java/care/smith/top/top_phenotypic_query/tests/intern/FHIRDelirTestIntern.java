package care.smith.top.top_phenotypic_query.tests.intern;

import org.junit.jupiter.api.Test;

import care.smith.top.top_phenotypic_query.tests.DelirTest;

public class FHIRDelirTestIntern extends DelirTest {

  private static final String CONFIG = "config/Delir_FHIR_Adapter_Test.yml";

  @Test
  public void testFHIRAlgA() throws InstantiationException {
    testAlgA(CONFIG, true);
  }

  @Test
  public void testFHIRExpIcd() throws InstantiationException {
    testExpIcd(CONFIG, true);
  }

  @Test
  public void testFHIRAlgB() throws InstantiationException {
    testAlgB(CONFIG, true);
  }

  @Test
  public void testFHIR2of3() throws InstantiationException {
    test2of3(CONFIG, true);
  }

  @Test
  public void testFHIRExtAlg() throws InstantiationException {
    testExtAlg(CONFIG, true);
  }

  @Test
  public void testFHIRAntiPsyAndOp() throws InstantiationException {
    testAntiPsyAndOp(CONFIG, true);
  }

  @Test
  public void testFHIRImpIcdAndAntiPsyOrOp() throws InstantiationException {
    testImpIcdAndAntiPsyOrOp(CONFIG, true);
  }

  @Test
  public void testFHIRCognitAnd2of3() throws InstantiationException {
    testCognitAnd2of3(CONFIG, true);
  }
}
