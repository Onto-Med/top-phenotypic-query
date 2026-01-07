package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFullBMIAgeTest extends AbstractTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFullBMIAgeTest.class);

  @Test
  public void test1() throws InstantiationException {
    Que que =
        new Que("config/Default_SQL_Adapter_Test.yml", phenotypes)
            .inc(overWeight, getDTR(2000), defAgrFunc.getId())
            .inc(female);

    LOGGER.trace(que.getConfig().toString());

    ResultSet rs = que.execute();

    LOGGER.trace(rs.toString());

    assertEquals(Set.of("1"), rs.getSubjectIds());
    assertEquals(13, rs.getPhenotypes("1").size());
    assertTrue(rs.getPhenotypes("1").hasPhenotype(overWeight.getId()));
    assertTrue(rs.getPhenotypes("1").hasPhenotype(female.getId()));

    SubjectPhenotypes phes = rs.getPhenotypes("1");
    Set<String> phesExpected = Entities.of(phenotypes).getPhenotypeIds();
    phesExpected.add("birthdate");
    phesExpected.remove("BMI27_30");
    phesExpected.remove("BMI19_27");
    phesExpected.remove("Male");
    phesExpected.remove("Heavy");
    phesExpected.remove("Light");
    phesExpected.remove("High");
    phesExpected.remove("LightAndHigh");
    assertEquals(phesExpected, phes.getPhenotypeNames());

    assertEquals(new BigDecimal(22), Values.getNumberValue(getValue("Age", phes)));
    assertFalse(Values.getBooleanValue(getValue("Old", phes)));
    assertTrue(Values.getBooleanValue(getValue("Young", phes)));

    assertEquals("female", Values.getStringValue(getValue("Sex", phes)));
    assertTrue(Values.getBooleanValue(getValue("Female", phes)));

    assertEquals(new BigDecimal("25.95155709342561"), Values.getNumberValue(getValue("BMI", phes)));
    assertFalse(Values.getBooleanValue(getValue("BMI19_25", phes)));
    assertTrue(Values.getBooleanValue(getValue("BMI25_30", phes)));

    assertEquals(BigDecimal.ONE, Values.getNumberValue(getValue("Finding", phes)));
    assertTrue(Values.getBooleanValue(getValue("Overweight", phes)));
  }

  @Test
  public void test2() throws InstantiationException {
    ResultSet rs =
        new Que("config/Default_SQL_Adapter_Test.yml", phenotypes)
            .inc(overWeight, getDTR(2000), defAgrFunc.getId())
            .exc(male)
            .execute();

    LOGGER.trace(rs.toString());

    assertEquals(Set.of("1"), rs.getSubjectIds());
    assertEquals(13, rs.getPhenotypes("1").size());
    assertTrue(rs.getPhenotypes("1").hasPhenotype(overWeight.getId()));

    SubjectPhenotypes phes = rs.getPhenotypes("1");
    Set<String> phesExpected = Entities.of(phenotypes).getPhenotypeIds();
    phesExpected.add("birthdate");
    phesExpected.remove("BMI27_30");
    phesExpected.remove("BMI19_27");
    phesExpected.remove("Female");
    phesExpected.remove("Heavy");
    phesExpected.remove("Light");
    phesExpected.remove("High");
    phesExpected.remove("LightAndHigh");
    assertEquals(phesExpected, phes.getPhenotypeNames());

    assertEquals(new BigDecimal(22), Values.getNumberValue(getValue("Age", phes)));
    assertFalse(Values.getBooleanValue(getValue("Old", phes)));
    assertTrue(Values.getBooleanValue(getValue("Young", phes)));

    assertEquals("female", Values.getStringValue(getValue("Sex", phes)));

    assertEquals(new BigDecimal("25.95155709342561"), Values.getNumberValue(getValue("BMI", phes)));
    assertFalse(Values.getBooleanValue(getValue("BMI19_25", phes)));
    assertTrue(Values.getBooleanValue(getValue("BMI25_30", phes)));

    assertEquals(BigDecimal.ONE, Values.getNumberValue(getValue("Finding", phes)));
    assertTrue(Values.getBooleanValue(getValue("Overweight", phes)));
  }
}
