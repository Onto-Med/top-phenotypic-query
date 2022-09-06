package care.smith.top.top_phenotypic_query.adapter.mapping;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import care.smith.top.backend.model.Code;

public class DataAdapterMapping {

  private String id;
  private CodeMapping birthdateMapping;
  private CodeMapping ageMapping;
  private CodeMapping sexMapping;
  private Map<String, CodeMapping> codeMappings = new HashMap<>();

  public static DataAdapterMapping getInstance(String yamlFilePath) {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    DataAdapterMapping config = null;
    try {
      config = mapper.readValue(new File(yamlFilePath), DataAdapterMapping.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return config;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CodeMapping getBirthdateMapping() {
    return birthdateMapping;
  }

  public void setBirthdateMapping(CodeMapping birthdateMapping) {
    this.birthdateMapping = birthdateMapping;
  }

  public CodeMapping getAgeMapping() {
    return ageMapping;
  }

  public void setAgeMapping(CodeMapping ageMapping) {
    this.ageMapping = ageMapping;
  }

  public CodeMapping getSexMapping() {
    return sexMapping;
  }

  public void setSexMapping(CodeMapping sexMapping) {
    this.sexMapping = sexMapping;
  }

  public CodeMapping getCodeMapping(String code) {
    return codeMappings.get(code);
  }

  public CodeMapping getCodeMapping(List<Code> codes) {
    for (Code code : codes) {
      CodeMapping map = getCodeMapping(code.getCodeSystem() + "|" + code.getCode());
      if (map != null) return map;
    }
    return null;
  }

  public void setCodeMappings(List<CodeMapping> codeMappings) {
    if (codeMappings != null)
      for (CodeMapping cm : codeMappings) this.codeMappings.put(cm.getCode(), cm);
  }

  @Override
  public String toString() {
    return "DataAdapterMapping [id="
        + id
        + ", birthdateMapping="
        + birthdateMapping
        + ", ageMapping="
        + ageMapping
        + ", sexMapping="
        + sexMapping
        + ", codeMappings="
        + codeMappings
        + "]";
  }
}
