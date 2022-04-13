package care.smith.top.top_phenotypic_query;

public class GlobalParameters {

  private String onlyId;
  private String onlyCount;
  private String onlySubject;
  private String limit;
  private String id;
  private String subject;

  public String getOnlyId() {
    return onlyId;
  }

  public void setOnlyId(String onlyId) {
    this.onlyId = onlyId;
  }

  public String getOnlyCount() {
    return onlyCount;
  }

  public void setOnlyCount(String onlyCount) {
    this.onlyCount = onlyCount;
  }

  public String getOnlySubject() {
    return onlySubject;
  }

  public void setOnlySubject(String onlySubject) {
    this.onlySubject = onlySubject;
  }

  public String getLimit() {
    return limit;
  }

  public String getLimit(int limit) {
    return getLimit().replace("{limit}", Integer.valueOf(limit).toString());
  }

  public void setLimit(String limit) {
    this.limit = limit;
  }

  public String getId() {
    return id;
  }

  public String getId(String id) {
    return getId().replace("{id}", id);
  }

  public String getId(DataAdapterConfig conf, String... ids) {
    return getId().replace("{id}", conf.getValuesAsString(ids));
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSubject() {
    return subject;
  }

  public String getSubject(String subject) {
    return getSubject().replace("{subject}", subject);
  }

  public String getSubject(DataAdapterConfig conf, String... subjects) {
    return getSubject().replace("{subject}", conf.getValuesAsString(subjects));
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Override
  public String toString() {
    return "GlobalParameters [onlyId="
        + onlyId
        + ", onlyCount="
        + onlyCount
        + ", onlySubject="
        + onlySubject
        + ", limit="
        + limit
        + ", id="
        + id
        + ", subject="
        + subject
        + "]";
  }
}
