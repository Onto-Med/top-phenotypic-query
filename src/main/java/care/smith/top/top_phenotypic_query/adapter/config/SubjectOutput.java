package care.smith.top.top_phenotypic_query.adapter.config;

public class SubjectOutput {

  private String id;
  private String sex;
  private String birthdate;
  private String patient;

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

  public String getPatient() {
    return patient;
  }

  public void setPatient(String patient) {
    this.patient = patient;
  }

  @Override
  public String toString() {
    return "SubjectOutput [id="
        + id
        + ", sex="
        + sex
        + ", birthdate="
        + birthdate
        + ", patient="
        + patient
        + "]";
  }
}
