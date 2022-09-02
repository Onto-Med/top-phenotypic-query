package care.smith.top.top_phenotypic_query.adapter.mapping;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DataAdapterMapping {

  private String birthdateCode;
  private String ageCode;
  private String sexCode;
  private Map<String, CodeMapping> mappings;

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

  public CodeMapping getCodeMapping(String code) {
    return mappings.get(code);
  }

  public String getBirthdateCode() {
    return birthdateCode;
  }

  public void setBirthdateCode(String birthdateCode) {
    this.birthdateCode = birthdateCode;
  }

  public String getAgeCode() {
    return ageCode;
  }

  public void setAgeCode(String ageCode) {
    this.ageCode = ageCode;
  }

  public String getSexCode() {
    return sexCode;
  }

  public void setSexCode(String sexCode) {
    this.sexCode = sexCode;
  }

  public Map<String, CodeMapping> getMappings() {
    return mappings;
  }

  public void setMappings(Map<String, CodeMapping> mappings) {
    this.mappings = mappings;
  }

  @Override
  public String toString() {
    return "DataAdapterMapping [birthdateCode="
        + birthdateCode
        + ", ageCode="
        + ageCode
        + ", sexCode="
        + sexCode
        + ", mappings="
        + mappings
        + "]";
  }
}
