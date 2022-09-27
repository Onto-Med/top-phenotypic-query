package care.smith.top.top_phenotypic_query.ucum;

import java.io.FileNotFoundException;
import java.math.BigDecimal;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;

public class UCUM {

  public static BigDecimal convert(BigDecimal value, String inUnit, String outUnit)
      throws FileNotFoundException, UcumException {
    UcumService service = new UcumEssenceService(getUCUMFile());
    return new BigDecimal(
        service.convert(new Decimal(value.toPlainString()), inUnit, outUnit).asDecimal());
  }

  private static String getUCUMFile() {
    return new UCUM().getClass().getClassLoader().getResource("ucum-essence.xml").getFile();
  }
}
