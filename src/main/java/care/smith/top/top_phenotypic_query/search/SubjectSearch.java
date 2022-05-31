package care.smith.top.top_phenotypic_query.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import care.smith.top.simple_onto_api.model.property.data.range.DateRange;
import care.smith.top.simple_onto_api.util.ToString;

public class SubjectSearch {

  private List<DateRange> birthdateRanges = new ArrayList<>();
  private List<String> genders = new ArrayList<>();
  private List<String> ids = new ArrayList<>();

  public List<DateRange> getBirthdateRanges() {
    return birthdateRanges;
  }

  public SubjectSearch addBirthdateRange(DateRange range) {
    this.birthdateRanges.add(range);
    return this;
  }

  public SubjectSearch setBirthdateRanges(List<DateRange> birthdateRanges) {
    this.birthdateRanges = birthdateRanges;
    return this;
  }

  public SubjectSearch setBirthdateRanges(DateRange... ranges) {
    return setBirthdateRanges(Arrays.asList(ranges));
  }

  public List<String> getGenders() {
    return genders;
  }

  public SubjectSearch addGender(String gender) {
    this.genders.add(gender);
    return this;
  }

  public SubjectSearch setGenders(List<String> genders) {
    this.genders = genders;
    return this;
  }

  public SubjectSearch setGenders(String... genders) {
    return setGenders(Arrays.asList(genders));
  }

  public List<String> getIds() {
    return ids;
  }

  public SubjectSearch addId(String id) {
    this.ids.add(id);
    return this;
  }

  public SubjectSearch setIds(List<String> ids) {
    this.ids = ids;
    return this;
  }

  public SubjectSearch setIds(String... ids) {
    return setIds(Arrays.asList(ids));
  }

  @Override
  public String toString() {
    return ToString.get(this)
        .add("birthdateRanges", birthdateRanges)
        .add("genders", genders)
        .add("ids", ids)
        .toString();
  }
}
