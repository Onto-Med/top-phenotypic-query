package care.smith.top.top_phenotypic_query.adapter.fhir.resource_finder;

import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.adapter.fhir.FHIRPath;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import java.util.HashSet;
import java.util.Set;
import org.hl7.fhir.r4.model.Resource;

public class EncounterPatientRef {

  public static final String PATIENT_REF = "_PATIENT_REF";

  public static boolean check(
      SubjectSearch search,
      FHIRPath path,
      Resource res,
      SubjectOutput out,
      ResultSet rs,
      String sbj) {
    if (search.getConfig().isEncounterId()) {
      String pat = path.getString(res, out.getPatient());
      if (pat != null) {
        rs.addValue(sbj, PATIENT_REF, null, Val.of(pat));
        return true;
      }
    }
    return false;
  }

  public static void check(ResultSet rs, DataAdapterConfig config) {
    if (config.isPatientId()) return;
    Set<String> patRefs = new HashSet<>();
    for (String sbjId : rs.getSubjectIds()) {
      String patRef = rs.getStringValue(sbjId, PATIENT_REF, null);
      if (patRef == null) continue;
      SubjectPhenotypes sbjPhens = rs.getPhenotypes(sbjId);
      sbjPhens.remove(PATIENT_REF);
      SubjectPhenotypes patSbjPhens = rs.get(patRef);
      for (PhenotypeValues patVals : patSbjPhens.values())
        sbjPhens.put(patVals.getPhenotypeName(), patVals);
      patRefs.add(patRef);
    }
    for (String patRef : patRefs) rs.remove(patRef);
  }
}
