package care.smith.top.top_phenotypic_query.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import care.smith.top.simple_onto_api.model.property.data.range.DateRange;
import care.smith.top.simple_onto_api.util.ToString;

public class PhenotypeSearch {

  private String className;
  private List<Code> codes = new ArrayList<>();
  private List<DateRange> dateRanges = new ArrayList<>();

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public List<Code> getCodes() {
    return codes;
  }

  public void addCode(Code code) {
    this.codes.add(code);
  }

  public void setCodes(List<Code> codes) {
    this.codes = codes;
  }

  public void setCodes(Code... codes) {
    setCodes(Arrays.asList(codes));
  }

  public List<DateRange> getDateRanges() {
    return dateRanges;
  }

  public void addDateRange(DateRange range) {
    this.dateRanges.add(range);
  }

  public void setDateRanges(List<DateRange> ranges) {
    this.dateRanges = ranges;
  }

  public void setDateRanges(DateRange... ranges) {
    setDateRanges(Arrays.asList(ranges));
  }

  @Override
  public String toString() {
    return ToString.get(this)
        .add("className", className)
        .add("codes", codes)
        .add("dateRanges", dateRanges)
        .toString();
  }
}
