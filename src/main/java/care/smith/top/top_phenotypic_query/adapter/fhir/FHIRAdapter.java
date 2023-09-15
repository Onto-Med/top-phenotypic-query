package care.smith.top.top_phenotypic_query.adapter.fhir;

import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterSettings;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.fhir.resource_finder.EncounterPartsFinder;
import care.smith.top.top_phenotypic_query.adapter.fhir.resource_finder.EncounterPatientRef;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FHIRAdapter extends DataAdapter {

  private FHIRClient client;
  private EncounterPartsFinder encParts;
  private static final Logger log = LoggerFactory.getLogger(FHIRAdapter.class);

  public FHIRAdapter(DataAdapterConfig config) {
    super(mergeDefault(config, "Default_FHIR_Adapter"));
    this.client = new FHIRClient(this.config);
    if (config.isEncounterId()) encParts = new EncounterPartsFinder(client);
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    String query = getSettings().createSinglePreparedQuery(search);
    log.debug("Execute FHIR query: {}", query);
    ResultSet rs = client.findPhenotypes(query, search);
    if (encParts != null) encParts.groupParts(rs);
    checkQuantifier(search, rs);
    return rs;
  }

  @Override
  public ResultSet execute(SubjectSearch search) {
    String query = getSettings().createSubjectPreparedQuery(search);
    log.debug("Execute FHIR query: {}", query);
    ResultSet rs = client.findPatients(query, search);
    if (encParts != null) encParts.groupParts(rs);
    EncounterPatientRef.check(rs, config);
    return rs;
  }

  @Override
  public DataAdapterSettings getSettings() {
    return FHIRAdapterSettings.get();
  }

  @Override
  public void close() {}
}
