package care.smith.top.top_phenotypic_query.tests.INTERPOLAR_DB_2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import module java.base;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import org.junit.jupiter.api.Test;

public class ObsRefRangeTest {

  private static final String CONFIG = "config/Interpolar_Adapter_Test.yml";

  private static final Phenotype obs =
      new Phe("obs", "http://loinc.org", "123").itemType(ItemType.OBSERVATION).number().get();

  @Test
  void allEligibleObservationsFoundExactlyOnce() throws InstantiationException {
    ResultSet rs = executeQuery();

    IO.println(rs);

    Set<String> expectedEncounters =
        Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");
    assertEquals(expectedEncounters, rs.getSubjectIds());

    for (String encId : expectedEncounters) {
      assertEquals(
          1,
          getValues(rs, encId).size(),
          "Encounter " + encId + " sollte genau 1 Observation haben");
    }
  }

  @Test
  void normalReferenceRangePreferredOverTreatment() throws InstantiationException {
    ResultSet rs = executeQuery();
    Value val = getSingleValue(rs, "1");

    assertEquals(15.0, Values.getNumberValue(val).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("unit")));
    assertEquals(10.0, Values.getNumberValue(val.getFields().get("refLowVal")).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("refLowUnit")));
    assertEquals(20.0, Values.getNumberValue(val.getFields().get("refHighVal")).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("refHighUnit")));
    assertEquals(123.0, Values.getNumberValue(val.getFields().get("origVal")).doubleValue());
    assertEquals("abc", Values.getStringValue(val.getFields().get("origUnit")));
  }

  @Test
  void nullTypeReferenceRangeDeduplicatedToFirst() throws InstantiationException {
    ResultSet rs = executeQuery();
    Value val = getSingleValue(rs, "2");

    assertEquals(12.0, Values.getNumberValue(val).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("unit")));
    assertEquals(5.0, Values.getNumberValue(val.getFields().get("refLowVal")).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("refLowUnit")));
    assertEquals(15.0, Values.getNumberValue(val.getFields().get("refHighVal")).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("refHighUnit")));
  }

  @Test
  void emptyStringTypeReferenceRangeTreatedLikeNull() throws InstantiationException {
    ResultSet rs = executeQuery();
    Value val = getSingleValue(rs, "3");

    assertEquals(13.0, Values.getNumberValue(val).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("unit")));
    assertEquals(6.0, Values.getNumberValue(val.getFields().get("refLowVal")).doubleValue());
    assertEquals(16.0, Values.getNumberValue(val.getFields().get("refHighVal")).doubleValue());
  }

  @Test
  void normalTypePreferredOverNullType() throws InstantiationException {
    ResultSet rs = executeQuery();
    Value val = getSingleValue(rs, "4");

    assertEquals(16.0, Values.getNumberValue(val).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("unit")));
    assertEquals(11.0, Values.getNumberValue(val.getFields().get("refLowVal")).doubleValue());
    assertEquals(21.0, Values.getNumberValue(val.getFields().get("refHighVal")).doubleValue());
  }

  @Test
  void observationKeptWhenOnlyNonNormalReferenceRangeExists() throws InstantiationException {
    ResultSet rs = executeQuery();
    Value val = getSingleValue(rs, "5");

    assertEquals(25.0, Values.getNumberValue(val).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("unit")));
    assertNull(val.getFields().get("refLowVal"));
    assertNull(val.getFields().get("refLowUnit"));
    assertNull(val.getFields().get("refHighVal"));
    assertNull(val.getFields().get("refHighUnit"));
  }

  @Test
  void observationKeptWithoutAnyReferenceRange() throws InstantiationException {
    ResultSet rs = executeQuery();

    // Encounter 6: with obs_id, completely NULL
    Value val6 = getSingleValue(rs, "6");
    assertEquals(30.0, Values.getNumberValue(val6).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val6.getFields().get("unit")));
    assertNull(val6.getFields().get("refLowVal"));
    assertNull(val6.getFields().get("refLowUnit"));
    assertNull(val6.getFields().get("refHighVal"));
    assertNull(val6.getFields().get("refHighUnit"));

    // Encounter 7: with obs_id, NULL values, and codes ''
    Value val7 = getSingleValue(rs, "7");
    assertEquals(31.0, Values.getNumberValue(val7).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val7.getFields().get("unit")));
    assertNull(val7.getFields().get("refLowVal"));
    assertNull(val7.getFields().get("refHighVal"));

    // Encounter 8: without obs_id (NULL), completely without a reference range
    Value val8 = getSingleValue(rs, "8");
    assertEquals(32.0, Values.getNumberValue(val8).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val8.getFields().get("unit")));
    assertNull(val8.getFields().get("refLowVal"));
    assertNull(val8.getFields().get("refHighVal"));

    // Encounter 9: empty obs_id (‘’), completely without reference range
    Value val9 = getSingleValue(rs, "9");
    assertEquals(33.0, Values.getNumberValue(val9).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val9.getFields().get("unit")));
    assertNull(val9.getFields().get("refLowVal"));
    assertNull(val9.getFields().get("refHighVal"));
  }

  @Test
  void oneSidedReferenceRangeWithoutTypeKept() throws InstantiationException {
    ResultSet rs = executeQuery();
    Value val = getSingleValue(rs, "10");

    assertEquals(35.0, Values.getNumberValue(val).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("unit")));
    assertNull(val.getFields().get("refLowVal"));
    assertNull(val.getFields().get("refLowUnit"));
    assertEquals(50.0, Values.getNumberValue(val.getFields().get("refHighVal")).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("refHighUnit")));
  }

  @Test
  void observationWithoutObsIdAndWithoutTypeKeptWithReferenceRange() throws InstantiationException {
    ResultSet rs = executeQuery();
    Value val = getSingleValue(rs, "11");

    assertEquals(7.0, Values.getNumberValue(val).doubleValue());
    assertEquals("mg/dL", Values.getStringValue(val.getFields().get("unit")));
    assertEquals(3.0, Values.getNumberValue(val.getFields().get("refLowVal")).doubleValue());
    assertEquals(9.0, Values.getNumberValue(val.getFields().get("refHighVal")).doubleValue());
  }

  private ResultSet executeQuery() throws InstantiationException {
    Que q = new Que(CONFIG, obs).inc(obs);
    return q.executeSqlFromResources("INTERPOLAR_DB_2/db.sql", "INTERPOLAR_DB_2/obs_ref_range.sql")
        .execute();
  }

  private List<Value> getValues(ResultSet rs, String subjectId) {
    return rs.getPhenotypes(subjectId).get("obs").getValues(null);
  }

  private Value getSingleValue(ResultSet rs, String subjectId) {
    List<Value> values = getValues(rs, subjectId);
    assertEquals(1, values.size());
    return values.get(0);
  }
}
