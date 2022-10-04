package care.smith.top.top_phenotypic_query.adapter.fhir;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Base;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.util.CollectionUtils;

import ca.uhn.fhir.context.FhirContext;

public class FHIRUtil {

  public static void print(IBaseResource r) {
    System.out.print(toString(r));
  }

  public static String toString(IBaseResource r) {
    return FhirContext.forR4().newJsonParser().setPrettyPrint(true).encodeResourceToString(r);
  }

  public static String getId(IBaseResource r) {
    return r.getIdElement().getResourceType() + "/" + r.getIdElement().getIdPart();
  }

  public static String getId(Reference r) {
    return r.getReferenceElement().getResourceType() + "/" + r.getReferenceElement().getIdPart();
  }

  public static String getIdPart(IBaseResource r) {
    return r.getIdElement().getIdPart();
  }

  public static String getIdPart(Reference r) {
    return r.getReferenceElement().getIdPart();
  }

  public static String getString(List<Base> values) {
    if (CollectionUtils.isEmpty(values)) return null;
    return values.get(0).toString();
  }

  public static LocalDateTime getDate(List<Base> values) {
    if (CollectionUtils.isEmpty(values)) return null;
    return new Timestamp((((DateTimeType) values.get(0)).getValue()).getTime()).toLocalDateTime();
  }

  public static BigDecimal getNumber(List<Base> values) {
    if (CollectionUtils.isEmpty(values)) return null;
    return ((DecimalType) values.get(0)).getValue();
  }

  public static Stream<String> codeableConceptToCodeUrisStream(CodeableConcept cc) {
    return cc.getCoding().stream().map(c -> c.getSystem() + "|" + c.getCode());
  }

  public static String codeableConceptToString(CodeableConcept cc, String separator) {
    return codeableConceptToCodeUrisStream(cc).collect(Collectors.joining(separator));
  }
}
