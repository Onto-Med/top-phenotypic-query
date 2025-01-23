package care.smith.top.top_phenotypic_query.tests.default_sql_writer;

import java.util.ArrayList;
import java.util.List;

public class EncDao {
  private String encounterId;
  private String type;
  private String startDateTime;
  private String endDateTime;
  private List<PheDao> phenotypes = new ArrayList<>();

  public static EncDao get(String encounterId) {
    return new EncDao().encounterId(encounterId);
  }

  public static EncDao get(
      String encounterId, String type, String startDateTime, String endDateTime) {
    return new EncDao()
        .encounterId(encounterId)
        .type(type)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime);
  }

  public String getEncounterId() {
    return encounterId;
  }

  public EncDao encounterId(String encounterId) {
    this.encounterId = encounterId;
    return this;
  }

  public String getType() {
    return type;
  }

  public EncDao type(String type) {
    this.type = type;
    return this;
  }

  public String getStartDateTime() {
    return startDateTime;
  }

  public EncDao startDateTime(String startDateTime) {
    this.startDateTime = startDateTime;
    return this;
  }

  public String getEndDateTime() {
    return endDateTime;
  }

  public EncDao endDateTime(String endDateTime) {
    this.endDateTime = endDateTime;
    return this;
  }

  public List<PheDao> getPhenotypes() {
    return phenotypes;
  }

  public EncDao phenotype(PheDao phenotype) {
    this.phenotypes.add(phenotype);
    return this;
  }
}
