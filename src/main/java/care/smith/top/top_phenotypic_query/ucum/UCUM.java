package care.smith.top.top_phenotypic_query.ucum;

import java.math.BigDecimal;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;

public class UCUM {

  private static UcumService SERVICE = getService();

  private static UcumService getService() {
    try {
      return new UcumEssenceService(
          UCUM.class.getClassLoader().getResource("ucum-essence.xml").getFile());
    } catch (UcumException e) {
      System.out.println("'ucum-essence.xml' not found!");
      throw new IllegalArgumentException(e);
    }
  }

  public static BigDecimal convert(BigDecimal value, String inUnit, String outUnit) {
    if (inUnit == null || outUnit == null) return value;
    try {
      return new BigDecimal(
          SERVICE.convert(new Decimal(value.toPlainString()), inUnit, outUnit).asDecimal());
    } catch (UcumException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
