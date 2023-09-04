package care.smith.top.top_phenotypic_query.adapter.config;

public class SubjectOutput {

  private String id;
  private String sex;
  private String birthdate;
  private String patientReference;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(String birthdate) {
    this.birthdate = birthdate;
  }

  public String getPatientReference() {
    return patientReference;
  }

  public void setPatientReference(String patientReference) {
    this.patientReference = patientReference;
  }

  @Override
  public String toString() {
    return "SubjectOutput [id="
        + id
        + ", sex="
        + sex
        + ", birthdate="
        + birthdate
        + ", patientReference="
        + patientReference
        + "]";
  }
}
