package care.smith.top.top_phenotypic_query;

public class PatientQueryParameters {

  private String gender;
  private String birthdate;

  public String getGender() {
    return gender;
  }

  public String getGender(String gender) {
    return getGender().replace("{gender}", gender);
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getBirthdate() {
    return birthdate;
  }

  public String getBirthdate(String operator, String birthdate) {
    return getBirthdate().replace("{operator}", operator).replace("{birthdate}", birthdate);
  }

  public void setBirthdate(String birthdate) {
    this.birthdate = birthdate;
  }

  @Override
  public String toString() {
    return "PatientQueryParameters [gender=" + gender + ", birthdate=" + birthdate + "]";
  }
}
