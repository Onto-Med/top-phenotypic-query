package care.smith.top.top_phenotypic_query.terminology;

import java.util.Map;

public interface Terminology {
  public String getID();

  public boolean isBirthDateCode(String code);

  public boolean isSexCode(String code);

  public Map<String, String> getMappings(String code);
}
