package care.smith.top.top_phenotypic_query.converter.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

public class FHIRConverter {

  /** Converts the given ResultSet to FHIR and writes it as JSON to the given OutputStream. */
  public static void write(ResultSet rs, OutputStream out) {
    Bundle bundle = new Bundle();

    // nicht nur observations ausgeben
    // bundle aus questioner responses
    // questionaire überlegen, generische darstellen
    // pro Datensatz/Patient gibt es eine Questionaire Response (ausgefülltes Formular)
    // wie mächtig sind Tools, können sie so etwas darstellen?
    // Resultset muss übersetzt werden
    // ob es da Suche, Filter und ähnliches

    for (SubjectPhenotypes sbjPhes : rs.values()) {
      Patient p = new Patient();
      var name = new HumanName();
      name.setGiven(Collections.singletonList(new StringType(sbjPhes.getSubjectId())));
      p.setName(Collections.singletonList(name));
      // todo: add patient to bundle?
      for (PhenotypeValues pheVals : sbjPhes.values()) {

        for (List<Value> vals : pheVals.values()) {
          for (Value val : vals) {
            Observation o = new Observation();
            // pheVals.g
            // o.setCode(new CodeableConcept()); //
            o.setSubject(new Reference(p));
            var q = new Quantity();
            // q.setId(vals.getPhenotypeName().toString());
            // q.setValue(val.get);
            o.setValue(q);

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

  public static String toString(ResultSet rs) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    write(rs, out);
    return out.toString();
  }
}
