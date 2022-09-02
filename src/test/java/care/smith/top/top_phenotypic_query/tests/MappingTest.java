package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import care.smith.top.top_phenotypic_query.adapter.mapping.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.mapping.DataAdapterMapping;

public class MappingTest {

  @Test
  public void test() {
    DataAdapterMapping map = DataAdapterMapping.getInstance("test_files/Simple_SQL_Mapping.yaml");
    assertEquals("http://loinc.org|21112-8", map.getBirthdateCode());
    assertEquals("http://loinc.org|30525-0", map.getAgeCode());
    assertEquals("http://loinc.org|46098-0", map.getSexCode());

    CodeMapping heightMap = map.getCodeMapping("http://loinc.org|3137-7");
    assertEquals("Assessment1", heightMap.getType());
    assertEquals("height", heightMap.getValue("phenotype"));

    CodeMapping weightMap = map.getCodeMapping("http://loinc.org|3141-9");
    assertEquals("Assessment1", weightMap.getType());
    assertEquals("weight", weightMap.getValue("phenotype"));
  }
}
