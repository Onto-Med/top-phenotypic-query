package care.smith.top.top_phenotypic_query.tests.intern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.sql.SQLException;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.ProjectionEntry.TypeEnum;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Res;

public class BlazeTestIntern {

  private static final String CONFIG = "config/Blaze_Adapter_Test.yml";

  private static Phenotype hemoglobin =
      new Phe("hemoglobin", "http://loinc.org", "59260-0").number("g/dl").get();
  private static Phenotype hemoglobinOver14_5 =
      new Phe("hemoglobinOver14_5").restriction(hemoglobin, Res.gt(14.5)).get();
  private static Entity[] entities = {hemoglobin, hemoglobinOver14_5};

  @Test
  public void test() throws InstantiationException, SQLException {
    QueryCriterion cri =
        (QueryCriterion)
            new QueryCriterion()
                .inclusion(true)
                .subjectId(hemoglobinOver14_5.getId())
                .type(TypeEnum.QUERYCRITERION);
    Query query = new Query().addCriteriaItem(cri);

    URL configFile = Thread.currentThread().getContextClassLoader().getResource(CONFIG);
    assertNotNull(configFile);
    DataAdapter adapter = DataAdapter.getInstance(configFile.getPath());

    PhenotypeFinder pf = new PhenotypeFinder(query, entities, adapter);
    ResultSet rs = pf.execute();

    //    ResultSet rs = new Que(CONFIG, entities).inc(hemoglobinOver14_5).execute();

    System.out.println(rs.getSubjectIds());

    assertEquals(
        Set.of(
            "Patient/1UKE",
            "Patient/UKB013",
            "Patient/UKB014",
            "Patient/UKFAU3",
            "Patient/UKFAU9",
            "Patient/UKFR3"),
        rs.getSubjectIds());

    int obsCount = 0;
    obsCount +=
        rs.getNumberValues("Patient/1UKE", "hemoglobin_values_hemoglobinOver14_5", null).size();
    obsCount +=
        rs.getNumberValues("Patient/UKB013", "hemoglobin_values_hemoglobinOver14_5", null).size();
    obsCount +=
        rs.getNumberValues("Patient/UKB014", "hemoglobin_values_hemoglobinOver14_5", null).size();
    obsCount +=
        rs.getNumberValues("Patient/UKFAU3", "hemoglobin_values_hemoglobinOver14_5", null).size();
    obsCount +=
        rs.getNumberValues("Patient/UKFAU9", "hemoglobin_values_hemoglobinOver14_5", null).size();
    obsCount +=
        rs.getNumberValues("Patient/UKFR3", "hemoglobin_values_hemoglobinOver14_5", null).size();

    System.out.println(
        rs.getNumberValues("Patient/1UKE", "hemoglobin_values_hemoglobinOver14_5", null));

    assertEquals(13, obsCount);
  }
}
