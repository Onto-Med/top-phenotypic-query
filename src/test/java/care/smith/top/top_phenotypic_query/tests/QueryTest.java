package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import care.smith.top.top_phenotypic_query.config.DataAdapterConfig;

public class QueryTest {

  private DataAdapterConfig getConfig(String name) {
    return DataAdapterConfig.getInstance(getClass().getClassLoader().getResource(name).getFile());
  }

  @Test
  public void testFHIRSubject() {
    DataAdapterConfig conf = getConfig("SMITH_FHIR_Adapter2.yaml");

    String qExp =
        "Patient?_format=json&gender=male,female&birthdate=ge1990-01-02&birthdate=lt2000-03-04&_id=id1,id2,id3&_has:Observation:patient:code=O1,O2&_has:Condition:patient:code=C1,C2";

    String qAct =
        conf.getSubjectQuery()
            .getQueryBuilder()
            .baseQuery()
            .gender("male", "female")
            .birthdateGE("1990-01-02")
            .birthdateLT("2000-03-04")
            .id("id1", "id2", "id3")
            .hasProp("Observation", "O1", "O2")
            .hasProp("Condition", "C1", "C2")
            .build();

    assertEquals(qExp, qAct);
  }

  @Test
  public void testFHIRProperty() {
    DataAdapterConfig conf = getConfig("SMITH_FHIR_Adapter2.yaml");

    String qExp =
        "Observation?code=c1,c2&_id=id1,id2,id3&subject=s1,s2&date=2000-01-01&value-quantity=gt1.1&value-quantity=le2.2&_sort=date&_count=1";

    String qAct =
        conf.getPropertyQuery("Observation")
            .getQueryBuilder("c1", "c2")
            .baseQuery()
            .id("id1", "id2", "id3")
            .subject("s1", "s2")
            .dateEQ("2000-01-01")
            .valueQuantityGT("1.1")
            .valueQuantityLE("2.2")
            .firstRecord()
            .build();

    assertEquals(qExp, qAct);
  }

  @Test
  public void testSQLSubject() {
    DataAdapterConfig conf = getConfig("LIFE_SQL_Adapter2.yaml");

    String qExp =
        "SELECT p.id, b.gender, b.birthdate FROM Patient p, Basic_Properties b\n"
            + "WHERE p.id = b.pat_id\n"
            + "AND b.gender IN (0, 1)\n"
            + "AND b.birthdate >= '1990-01-02'::date\n"
            + "AND b.birthdate < '2000-03-04'::date\n"
            + "AND p.id IN (id1, id2, id3)\n"
            + "AND EXISTS (SELECT a.p1 FROM Assessment1 a WHERE a.pat_id = Patient.id AND a.p1 IS NOT NULL)\n"
            + "AND EXISTS (SELECT a.prop_val FROM Assessment2 a, Property p WHERE p.id = a.prop_id AND p.name = 'p2' AND a.prop_val IS NOT NULL)";

    String qAct =
        conf.getSubjectQuery()
            .getQueryBuilder()
            .baseQuery()
            .gender("0", "1")
            .birthdateGE("1990-01-02")
            .birthdateLT("2000-03-04")
            .id("id1", "id2", "id3")
            .hasProp("Assessment1", "p1")
            .hasProp("Assessment2", "p2")
            .build();

    assertEquals(qExp, qAct);
  }

  @Test
  public void testSQLProperty() {
    DataAdapterConfig conf = getConfig("LIFE_SQL_Adapter2.yaml");

    String qExp =
        "SELECT a.pat_id, a.prop_val, a.recorded_date FROM Assessment2 a, Property p\n"
            + "WHERE p.id = a.prop_id AND p.name = 'p1'\n"
            + "AND p.id IN (id1, id2, id3)\n"
            + "AND a.pat_id IN (s1, s2)\n"
            + "AND a.recorded_date = '2000-01-01'::date\n"
            + "AND a.prop_val > 1.1\n"
            + "AND a.prop_val <= 2.2\n"
            + "ORDER BY a.recorded_date\n"
            + "LIMIT 1";

    String qAct =
        conf.getPropertyQuery("Assessment2")
            .getQueryBuilder("p1")
            .baseQuery()
            .id("id1", "id2", "id3")
            .subject("s1", "s2")
            .dateEQ("2000-01-01")
            .valueQuantityGT("1.1")
            .valueQuantityLE("2.2")
            .firstRecord()
            .build();

    assertEquals(qExp, qAct);
  }
}
