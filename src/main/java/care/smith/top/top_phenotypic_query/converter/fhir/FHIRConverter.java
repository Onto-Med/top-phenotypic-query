package care.smith.top.top_phenotypic_query.converter.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

public class FHIRConverter {

  /** Converts the given ResultSet to FHIR and writes it as JSON to the given OutputStream. */
  public static void write(ResultSet rs, OutputStream out) {
    Bundle bundle = new Bundle();

    for (SubjectPhenotypes sbjPhes : rs.values()) {
      Patient p = new Patient();
      // todo: add patient to bundle?
      for (PhenotypeValues pheVals : sbjPhes.values()) {

        for (List<Value> vals : pheVals.values()) {
          for (Value val : vals) {
            Observation o = new Observation();
            // pheVals.g
            // o.setCode(new CodeableConcept()); //
            o.setSubject(new Reference(p));
            var component = bundle.addEntry();
            component.setResource(o);
            // sbjPhes.
            // writer.write(new CSVDataRecord(sbjPhes.getSubjectId(), pheVals.getPhenotypeName(),
            // val));
          }
        }

        FhirContext ctx = FhirContext.forR4(); // Use the appropriate FHIR version
        IParser parser = ctx.newJsonParser(); // or ctx.newXmlParser() for XML
        String bundleJson = parser.encodeResourceToString(bundle);

        try (PrintWriter writer = new PrintWriter(out)) {
          writer.print(bundleJson);
        }
      }
    }
  }
}
