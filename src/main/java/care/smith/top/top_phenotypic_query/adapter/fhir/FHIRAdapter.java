package care.smith.top.top_phenotypic_query.adapter.fhir;

import java.util.List;

import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterSettings;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public class FHIRAdapter extends DataAdapter {

  private FHIRClient client;
  private static final Logger log = LoggerFactory.getLogger(FHIRAdapter.class);

  public FHIRAdapter(DataAdapterConfig config) {
    super(config);
    this.client = new FHIRClient(config);
  }

  public FHIRAdapter(String configFilePath) {
    super(configFilePath);
    this.client = new FHIRClient(config);
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    String query = getSettings().createSinglePreparedQuery(search);
    log.debug("Execute FHIR query: {}", query);
    return client.findPhenotypes(query, search);
  }

  @Override
  public ResultSet execute(SubjectSearch search) {
    String query = getSettings().createSubjectPreparedQuery(search);
    log.debug("Execute FHIR query: {}", query);
    return client.findPatients(query, search);
  }

  @Override
  public ResultSet executeAllSubjectsQuery() {
    String query = SubjectSearch.getBaseQuery(config);
    log.debug("Execute FHIR query: {}", query);
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.findResources(query);
    for (Resource res : resources) rs.addSubject(FHIRUtil.getId(res));
    return rs;
  }

  @Override
  public DataAdapterSettings getSettings() {
    return FHIRAdapterSettings.get();
  }

  @Override
  public void close() {}
}
