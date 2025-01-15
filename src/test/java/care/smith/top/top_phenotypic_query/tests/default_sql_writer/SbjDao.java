package care.smith.top.top_phenotypic_query.tests.default_sql_writer;

import java.util.ArrayList;
import java.util.List;

public class SbjDao {
  private String subjectId;
  private String birthDate;
  private String sex;
  private List<EncDao> encounters = new ArrayList<>();

  public static SbjDao get(String subjectId, String birthDate, String sex) {
    return new SbjDao().subjectId(subjectId).birthDate(birthDate).sex(sex);
  }

  public String getSubjectId() {
    return subjectId;
  }

  public SbjDao subjectId(String subjectId) {
    this.subjectId = subjectId;
    return this;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public SbjDao birthDate(String birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  public String getSex() {
    return sex;
  }

  public SbjDao sex(String sex) {
    this.sex = sex;
    return this;
  }

  public List<EncDao> getEncounters() {
    return encounters;
  }

  public SbjDao encounter(EncDao enc) {
    this.encounters.add(enc);
    return this;
  }
}
