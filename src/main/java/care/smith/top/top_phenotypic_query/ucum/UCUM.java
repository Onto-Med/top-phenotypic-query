package care.smith.top.top_phenotypic_query.ucum;

import org.fhir.ucum.Decimal;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UCUM {

  private static final Logger log = LoggerFactory.getLogger(UCUM.class);

  private static UcumService SERVICE = getService();

  private static UcumService getService() {
    try {
      return new UcumEssenceService(
          UCUM.class.getClassLoader().getResourceAsStream("ucum-essence.xml"));
    } catch (UcumException e) {
      log.error("'ucum-essence.xml' not found!");
      throw new IllegalArgumentException(e);
    }
  }

  public static Double convert(Double value, String inUnit, String outUnit) {
    if (inUnit == null || outUnit == null) return value;
    try {
      return new Double(
          SERVICE.convert(new Decimal(value.toString()), inUnit, outUnit).asDecimal());
    } catch (UcumException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
