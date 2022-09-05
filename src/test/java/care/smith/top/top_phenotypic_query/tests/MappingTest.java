package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.NumberRestriction;
import care.smith.top.backend.model.Quantifier;
import care.smith.top.backend.model.Restriction;
import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.backend.model.StringRestriction;
import care.smith.top.top_phenotypic_query.adapter.mapping.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.mapping.DataAdapterMapping;

public class MappingTest {

  @Test
  public void test() {
    DataAdapterMapping map = DataAdapterMapping.getInstance("test_files/Simple_SQL_Mapping.yaml");

    assertEquals("http://loinc.org|21112-8", map.getBirthdateMapping().getCode());
    assertEquals("http://loinc.org|30525-0", map.getAgeMapping().getCode());
    assertEquals("http://loinc.org|46098-0", map.getSexMapping().getCode());

    Restriction femaleModel =
        new StringRestriction()
            .addValuesItem("http://hl7.org/fhir/administrative-gender|female")
            .type(DataType.STRING);
    Restriction femaleSourceExpected =
        new NumberRestriction().addValuesItem(BigDecimal.valueOf(2)).type(DataType.NUMBER);
    Restriction femaleSourceActual = map.getSexMapping().getSourceRestriction(femaleModel);
    assertEquals(femaleSourceExpected, femaleSourceActual);

    Restriction ageModel =
        new NumberRestriction()
            .minOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
            .maxOperator(RestrictionOperator.LESS_THAN)
            .addValuesItem(BigDecimal.valueOf(18))
            .addValuesItem(BigDecimal.valueOf(34))
            .cardinality(1)
            .quantifier(Quantifier.EXACT)
            .type(DataType.NUMBER);
    Restriction ageSourceExpected =
        new NumberRestriction()
            .minOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
            .maxOperator(RestrictionOperator.LESS_THAN)
            .addValuesItem(BigDecimal.valueOf(19))
            .addValuesItem(BigDecimal.valueOf(34))
            .cardinality(1)
            .quantifier(Quantifier.EXACT)
            .type(DataType.NUMBER);
    Restriction ageSourceActual = map.getAgeMapping().getSourceRestriction(ageModel);
    assertEquals(ageSourceExpected, ageSourceActual);

    CodeMapping heightMap = map.getCodeMapping("http://loinc.org|3137-7");
    assertEquals("Assessment1", heightMap.getType());
    assertEquals("height", heightMap.getPhenotypeMapping("phenotype"));

    CodeMapping weightMap = map.getCodeMapping("http://loinc.org|3141-9");
    assertEquals("Assessment1", weightMap.getType());
    assertEquals("weight", weightMap.getPhenotypeMapping("phenotype"));
  }
}
