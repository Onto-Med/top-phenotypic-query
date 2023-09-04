package care.smith.top.top_phenotypic_query.adapter.fhir;

import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterSettings;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FHIRAdapter extends DataAdapter {

  private FHIRClient client;
  private static final Logger log = LoggerFactory.getLogger(FHIRAdapter.class);

  public FHIRAdapter(DataAdapterConfig config) {
    super(mergeDefault(config, "Default_FHIR_Adapter"));
    this.client = new FHIRClient(this.config);
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    String query = getSettings().createSinglePreparedQuery(search);
    log.debug("Execute FHIR query: {}", query);
    ResultSet rs = client.findPhenotypes(query, search);
    checkQuantifier(search, rs);
    return rs;
  }

  @Override
  public ResultSet execute(SubjectSearch search) {
    String query = getSettings().createSubjectPreparedQuery(search);
    log.debug("Execute FHIR query: {}", query);
    ResultSet rs = client.findPatients(query, search);
    Set<String> patRefs = new HashSet<>();
    for (String sbjId : rs.getSubjectIds()) {
      String patRef = rs.getStringValue(sbjId, "PatientReference", null);
      if (patRef == null) continue;
      SubjectPhenotypes newSbjPhens = new SubjectPhenotypes(sbjId);
      SubjectPhenotypes patSbjPhens = rs.get(patRef);
      for (PhenotypeValues patVals : patSbjPhens.values())
        newSbjPhens.put(patVals.getPhenotypeName(), patVals);
      rs.put(sbjId, newSbjPhens);
      patRefs.add(patRef);
    }
    for (String patRef : patRefs) rs.remove(patRef);
    return rs;
  }

  @Override
  public DataAdapterSettings getSettings() {
    return FHIRAdapterSettings.get();
  }

  @Override
  public void close() {}
}
