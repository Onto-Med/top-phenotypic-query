package care.smith.top.top_phenotypic_query.adapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public class FHIRAdapter extends DataAdapter {

  private IGenericClient client;
  private IParser parser;

  private Logger log = LoggerFactory.getLogger(FHIRAdapter.class);

  public FHIRAdapter(DataAdapterConfig config) {
    super(config);
    initConnection();
  }

  public FHIRAdapter(String configFilePath) {
    super(configFilePath);
    initConnection();
  }

  private void initConnection() {
    FhirContext ctx = FhirContext.forR4();
    ctx.getRestfulClientFactory().setConnectTimeout(2 * 60 * 60 * 1000);
    ctx.getRestfulClientFactory().setSocketTimeout(2 * 60 * 60 * 1000);
    this.client = ctx.newRestfulGenericClient(config.getConnectionAttribute("endpoint"));
    this.parser = ctx.newJsonParser();

    String user = config.getConnectionAttribute("user");
    String password = config.getConnectionAttribute("password");
    String token = config.getConnectionAttribute("token");

    if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password))
      client.registerInterceptor(new BasicAuthInterceptor(user, password));
    else if (StringUtils.isNotBlank(token))
      client.registerInterceptor(new BearerTokenAuthInterceptor(token));
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    ResultSet rs = new ResultSet();
    try {
      java.sql.ResultSet sqlRS = con.createStatement().executeQuery(search.getQueryString());
      PhenotypeOutput out = search.getOutput();
      String sbjCol = out.getSubject();
      String pheCol = out.getPhenotype();
      String dateCol = out.getDate();
      Phenotype phe = search.getPhenotype();
      DataType datatype = phe.getDataType();

      while (sqlRS.next()) {
        String sbj = sqlRS.getString(sbjCol);
        LocalDateTime date = sqlRS.getTimestamp(dateCol).toLocalDateTime();
        Value val = null;
        if (datatype == DataType.BOOLEAN) val = new BooleanValue(sqlRS.getBoolean(pheCol), date);
        else if (datatype == DataType.DATE_TIME)
          val = new DateTimeValue(sqlRS.getTimestamp(pheCol).toLocalDateTime(), date);
        else if (datatype == DataType.NUMBER)
          val = new DecimalValue(sqlRS.getBigDecimal(pheCol), date);
        else val = new StringValue(sqlRS.getString(pheCol), date);
        if (val != null)
          rs.addValueWithRestriction(
              sbj,
              phe,
              search.getDateTimeRestriction(),
              val,
              search.getSourceUnit(),
              search.getModelUnit());
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  @Override
  public ResultSet execute(SubjectSearch search) {
    ResultSet rs = new ResultSet();
    try {
      java.sql.ResultSet sqlRS = con.createStatement().executeQuery(search.getQueryString());
      SubjectOutput out = search.getOutput();
      String sbjCol = out.getId();
      String bdCol = out.getBirthdate();
      String sexCol = out.getSex();
      Phenotype sex = search.getSex();
      Phenotype bd = search.getBirthdateDerived();
      Phenotype age = search.getAge();

      while (sqlRS.next()) {
        String sbj = sqlRS.getString(sbjCol);
        if (bd != null) {
          Timestamp bdSqlVal = sqlRS.getTimestamp(bdCol);
          if (bdSqlVal != null) {
            DateTimeValue val = new DateTimeValue(bdSqlVal.toLocalDateTime());
            if (search.getBirthdate() != null) rs.addValueWithRestriction(sbj, bd, null, val);
            else rs.addValue(sbj, bd, null, val);
            if (age != null) {
              Value ageVal =
                  new DecimalValue(SubjectSearch.birthdateToAge(bdSqlVal.toLocalDateTime()));
              rs.addValueWithRestriction(sbj, age, null, ageVal);
            }
          }
        }
        if (sex != null) {
          if (sex.getDataType() == DataType.BOOLEAN) {
            Boolean sexSqlVal = sqlRS.getBoolean(sexCol);
            if (sexSqlVal != null)
              rs.addValueWithRestriction(sbj, sex, null, new BooleanValue(sexSqlVal));
          } else if (sex.getDataType() == DataType.NUMBER) {
            BigDecimal sexSqlVal = sqlRS.getBigDecimal(sexCol);
            if (sexSqlVal != null)
              rs.addValueWithRestriction(sbj, sex, null, new DecimalValue(sexSqlVal));
          } else {
            String sexSqlVal = sqlRS.getString(sexCol);
            if (sexSqlVal != null)
              rs.addValueWithRestriction(sbj, sex, null, new StringValue(sexSqlVal));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  @Override
  public ResultSet executeAllSubjectsQuery() {
    ResultSet rs = new ResultSet();
    try {
      java.sql.ResultSet sqlRS =
          con.createStatement().executeQuery(SubjectSearch.getBaseQuery(config));
      String sbjCol = SubjectSearch.getIdColumn(config);
      while (sqlRS.next()) rs.addSubject(sqlRS.getString(sbjCol));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  public ResourceMap executeQuery(String query) {
    Bundle firstPage =
        client
            .search()
            .byUrl(query)
            .returnBundle(Bundle.class)
            .cacheControl(new CacheControlDirective().setNoCache(true))
            .execute();
    ResourceMap results = new ResourceMap(firstPage);
    addPages(firstPage, results);
    return results;
  }

  private void addPages(Bundle lastPage, ResourceMap results) {
    if (lastPage.getLink(Bundle.LINK_NEXT) != null) {
      Bundle nextPage = client.loadPage().next(lastPage).execute();
      results.add(nextPage);
      addPages(nextPage, results);
    }
  }

  public Optional<String> createResource(Resource res) {
    log.info("CREATE RESOURCE: {}", FHIRUtil.toString(res));
    return create(client.create().resource(res).execute());
  }

  public Bundle parseBundle(File file) throws IOException {
    Bundle bundle;
    try {
      bundle = parser.parseResource(Bundle.class, new FileInputStream(file));
    } catch (DataFormatException e) {
      throw new IOException();
    }
    return bundle;
  }

  public void importBundle(Bundle bundle) {
    Bundle resp = client.transaction().withBundle(bundle).execute();
    log.info("IMPORT BUNDLE: {}", bundle.getId());
    log.info(FHIRUtil.toString(resp));
  }

  public boolean deleteResource(String resourceType, String id) {
    log.info("DELETE {}: {}", resourceType.toUpperCase(), id);
    return delete(client.delete().resourceById(new IdDt(resourceType, id)).execute());
  }

  public Bundle findPatients() {
    return client
        .search()
        .forResource(Patient.class)
        .where(Patient.IDENTIFIER.hasSystemWithAnyCode(PhenoManCodeSystem.SYSTEM))
        .returnBundle(Bundle.class)
        .execute();
  }

  public Optional<String> findPatientId(String identifier) {
    Bundle b =
        client
            .search()
            .forResource(Patient.class)
            .where(
                Patient.IDENTIFIER.exactly().systemAndCode(PhenoManCodeSystem.SYSTEM, identifier))
            .returnBundle(Bundle.class)
            .execute();
    if (b.getTotal() == 0) return Optional.empty();
    else return Optional.of(b.getEntryFirstRep().getResource().getIdElement().getIdPart());
  }

  public Bundle findObservationsByMethod() {
    return client
        .search()
        .forResource(Observation.class)
        .where(Observation.METHOD.hasSystemWithAnyCode(PhenoManCodeSystem.SYSTEM))
        .returnBundle(Bundle.class)
        .execute();
  }

  public Bundle findItemsByIdentifier(String resourceType) {
    return client
        .search()
        .byUrl(resourceType + "?identifier=" + PhenoManCodeSystem.SYSTEM + "|")
        .returnBundle(Bundle.class)
        .execute();
  }

  public Optional<Resource> findResource(String type, String id) {
    Bundle b = client.search().byUrl(type + "/" + id).returnBundle(Bundle.class).execute();
    if (b.getTotal() == 0) return Optional.empty();
    else return Optional.of(b.getEntryFirstRep().getResource());
  }

  public Optional<Resource> findItemOfPatient(
      String resourceType, String codeUri, String patientId) {
    Bundle b = findItemsOfPatient(resourceType, codeUri, patientId);
    if (b.getTotal() == 0) return Optional.empty();
    else return Optional.of(b.getEntryFirstRep().getResource());
  }

  public Bundle findItemsOfPatient(String resourceType, String codeUri, String patientId) {
    String q = resourceType + "?subject=Patient/" + patientId + "&code=" + codeUri;
    return client.search().byUrl(q).returnBundle(Bundle.class).execute();
  }

  public Bundle findItemsOfPatient(String resourceType, String patientId) {
    String q = resourceType + "?subject=Patient/" + patientId;
    return client.search().byUrl(q).returnBundle(Bundle.class).execute();
  }

  private boolean deleteAllPatients() {
    log.info("DELETE ALL PATIENTS:");

    IBaseOperationOutcome resp =
        client
            .delete()
            .resourceConditionalByType("Patient")
            .where(Patient.IDENTIFIER.hasSystemWithAnyCode(PhenoManCodeSystem.SYSTEM))
            .execute();

    return delete(resp);
  }

  private boolean deleteAllItemsByIdentifier(String resourceType) {
    log.info("DELETE ALL {}S:", resourceType.toUpperCase());

    IBaseOperationOutcome resp =
        client
            .delete()
            .resourceConditionalByUrl(
                resourceType + "?identifier=" + PhenoManCodeSystem.SYSTEM + "|")
            .execute();

    return delete(resp);
  }

  private boolean deleteMDRItems(String resourceType) {
    log.info("DELETE ALL MDR {}S:", resourceType.toUpperCase());

    IBaseOperationOutcome resp =
        client
            .delete()
            .resourceConditionalByUrl(resourceType + "?identifier=" + MDRMan.getMDRUri() + "|")
            .execute();

    return delete(resp);
  }

  public Optional<String> findResourceId(String resourceType, String system, String identifier) {
    Bundle b =
        client
            .search()
            .byUrl(resourceType + "?identifier=" + system + "|" + identifier)
            .returnBundle(Bundle.class)
            .execute();
    if (b.getTotal() == 0) return Optional.empty();
    else return Optional.of(b.getEntryFirstRep().getResource().getIdElement().getIdPart());
  }

  private boolean deleteAllObservationsByMethod() {
    log.info("DELETE ALL OBSERVATIONS:");

    IBaseOperationOutcome resp =
        client
            .delete()
            .resourceConditionalByType("Observation")
            .where(Observation.METHOD.hasSystemWithAnyCode(PhenoManCodeSystem.SYSTEM))
            .execute();

    return delete(resp);
  }

  private void deleteAllItems() {
    deleteAllItemsByIdentifier("Condition");
    deleteAllItemsByIdentifier("Procedure");
    deleteAllItemsByIdentifier("MedicationRequest");
    deleteAllItemsByIdentifier("MedicationAdministration");
    deleteAllItemsByIdentifier("MedicationStatement");
    deleteAllItemsByIdentifier("ClinicalImpression");
    deleteAllItemsByIdentifier("Observation");
    deleteAllItemsByIdentifier("AllergyIntolerance");
    deleteAllObservationsByMethod();
  }

  public void deleteAllPatientsWithItems() {
    deleteAllItems();
    deleteAllPatients();
  }

  private Optional<String> create(MethodOutcome outcome) {
    if (outcome != null && outcome.getCreated() != null && outcome.getCreated().booleanValue()) {
      String id = outcome.getId().getIdPart();
      log.info("SUCCESSFULLY CREATED (ID:{})!", id);
      return Optional.of(id);
    } else {
      log.warn("CREATION FAILED!");
      return Optional.empty();
    }
  }

  private boolean delete(IBaseOperationOutcome resp) {
    if (resp == null) {
      log.warn("NO RESPONSE");
      return false;
    } else {
      OperationOutcome outcome = (OperationOutcome) resp;
      for (OperationOutcomeIssueComponent issue : outcome.getIssue()) {
        if (issue.getCode() != IssueType.INFORMATIONAL) {
          log.warn(outcome.getIssueFirstRep().getDiagnostics());
          return false;
        } else log.info(outcome.getIssueFirstRep().getDiagnostics());
      }

      return true;
    }
  }

  @Override
  public DataAdapterFormat getFormat() {
    return SQLAdapterFormat.get();
  }

  @Override
  public void close() {
    try {
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void printSize(java.sql.ResultSet rs) {
    if (rs == null) System.out.println("SIZE: NULL");
    else {
      try {
        rs.last();
        System.out.println("SIZE: " + rs.getRow());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void print(java.sql.ResultSet rs) {
    try {
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnsNumber = rsmd.getColumnCount();
      while (rs.next()) {
        for (int i = 1; i <= columnsNumber; i++) {
          if (i > 1) System.out.print(" | ");
          System.out.print(rs.getString(i));
        }
        System.out.println("");
      }
      for (int i = 1; i <= columnsNumber; i++) {
        if (i > 1) System.out.print(" | ");
        System.out.print(rsmd.getColumnName(i));
      }
      System.out.println("");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
