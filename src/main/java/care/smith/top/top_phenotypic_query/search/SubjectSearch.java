package care.smith.top.top_phenotypic_query.search;

import care.smith.top.simple_onto_api.model.ClassDef;

public class SubjectSearch {

  private ClassDef[] age;
  private ClassDef[] gender;
  private String[] id;

  public ClassDef[] getAge() {
    return age;
  }

  public SubjectSearch age(ClassDef... age) {
    this.age = age;
    return this;
  }

  public ClassDef[] getGender() {
    return gender;
  }

  public SubjectSearch gender(ClassDef... gender) {
    this.gender = gender;
    return this;
  }

  public String[] getId() {
    return id;
  }

  public SubjectSearch id(String... id) {
    this.id = id;
    return this;
  }
}
