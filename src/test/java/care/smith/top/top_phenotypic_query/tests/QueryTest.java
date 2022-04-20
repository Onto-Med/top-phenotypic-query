package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import care.smith.top.top_phenotypic_query.config.DataAdapterConfig;

public class QueryTest {

  private DataAdapterConfig getConfig(String name) {
    return DataAdapterConfig.getInstance(getClass().getClassLoader().getResource(name).getFile());
  }

  @Test
  public void test1() {
    DataAdapterConfig conf = getConfig("SMITH_FHIR_Adapter.yaml");

    String qExp =
        "Patient?gender=male,female&birthdate=ge1990-01-02&birthdate=lt2000-03-04&_id=id1,id2,id3&_count=5&_summary=count&_elements=id&_elements=subject&subject=s1,s2&_has:Observation:patient:code=O1,O2&_has:Condition:patient:code=C1,C2";

    String qAct =
        conf.getPatientQuery()
            .getQueryBuilder()
            .gender("male", "female")
            .birthdateGE("1990-01-02")
            .birthdateLT("2000-03-04")
            .id("id1", "id2", "id3")
            .limit(5)
            .onlyCount()
            .onlyId()
            .onlySubject()
            .subject("s1", "s2")
            .hasProp("SingleObservation", "O1", "O2")
            .hasProp("Condition", "C1", "C2")
            .build();

    assertEquals(qExp, qAct);
  }

  @Test
  public void test2() {
    DataAdapterConfig conf = getConfig("SMITH_FHIR_Adapter.yaml");

    String qExp =
        "Observation?code=c1,c2&value-concept=vc1,vc2&value-quantity=gt1.1&value-quantity=le2.2&date=2000-01-01&_sort=-date";

    String qAct =
        conf.getPropertyQuery("SingleObservation")
            .getQueryBuilder()
            .code("c1", "c2")
            .valueConcept("vc1", "vc2")
            .valueQuantityGT("1.1")
            .valueQuantityLE("2.2")
            .dateEQ("2000-01-01")
            .sortDec()
            .build();

    assertEquals(qExp, qAct);
  }
}
