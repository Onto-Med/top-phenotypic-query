package care.smith.top.top_phenotypic_query.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import care.smith.top.simple_onto_api.c2reasoner.function.Function;
import care.smith.top.simple_onto_api.model.property.data.range.DataRange;
import care.smith.top.simple_onto_api.model.property.data.range.DateRange;
import care.smith.top.simple_onto_api.util.ToString;

public class SinglePhenotypeSearch {

  private String className;
  private String type;
  private List<String> phenotypes = new ArrayList<>();
  private List<String> ids = new ArrayList<>();
  private List<String> subjectIds = new ArrayList<>();
  private List<DataRange> dataRanges = new ArrayList<>();
  private List<DateRange> dateRanges = new ArrayList<>();
  private Function function;

  public String getClassName() {
    return className;
  }

  public SinglePhenotypeSearch setClassName(String className) {
    this.className = className;
    return this;
  }

  public String getType() {
    return type;
  }

  public SinglePhenotypeSearch setType(String type) {
    this.type = type;
    return this;
  }

  public List<String> getPhenotypes() {
    return phenotypes;
  }

  public SinglePhenotypeSearch addPhenotype(String phenotype) {
    this.phenotypes.add(phenotype);
    return this;
  }

  public SinglePhenotypeSearch setPhenotypes(List<String> phenotypes) {
    this.phenotypes = phenotypes;
    return this;
  }

  public SinglePhenotypeSearch setPhenotypes(String... phenotypes) {
    return setPhenotypes(Arrays.asList(phenotypes));
  }

  public List<String> getIds() {
    return ids;
  }

  public SinglePhenotypeSearch addId(String id) {
    this.ids.add(id);
    return this;
  }

  public SinglePhenotypeSearch setIds(List<String> ids) {
    this.ids = ids;
    return this;
  }

  public SinglePhenotypeSearch setIds(String... ids) {
    return setIds(Arrays.asList(ids));
  }

  public List<String> getSubjectIds() {
    return subjectIds;
  }

  public SinglePhenotypeSearch addSubjectId(String subjectId) {
    this.subjectIds.add(subjectId);
    return this;
  }

  public SinglePhenotypeSearch setSubjectIds(List<String> subjectIds) {
    this.subjectIds = subjectIds;
    return this;
  }

  public SinglePhenotypeSearch setSubjectIds(String... subjectIds) {
    return setSubjectIds(Arrays.asList(subjectIds));
  }

  public List<DataRange> getDataRanges() {
    return dataRanges;
  }

  public SinglePhenotypeSearch addDataRange(DataRange range) {
    this.dataRanges.add(range);
    return this;
  }

  public SinglePhenotypeSearch setDataRanges(List<DataRange> ranges) {
    this.dataRanges = ranges;
    return this;
  }

  public SinglePhenotypeSearch setDataRanges(DataRange... ranges) {
    return setDataRanges(Arrays.asList(ranges));
  }

  public List<DateRange> getDateRanges() {
    return dateRanges;
  }

  public SinglePhenotypeSearch addDateRange(DateRange range) {
    this.dateRanges.add(range);
    return this;
  }

  public SinglePhenotypeSearch setDateRanges(List<DateRange> ranges) {
    this.dateRanges = ranges;
    return this;
  }

  public SinglePhenotypeSearch setDateRanges(DateRange... ranges) {
    return setDateRanges(Arrays.asList(ranges));
  }

  public Function getFunction() {
    return function;
  }

  public SinglePhenotypeSearch setFunction(Function function) {
    this.function = function;
    return this;
  }

  @Override
  public String toString() {
    return ToString.get(this)
        .add("className", className)
        .add("type", type)
        .add("phenotypes", phenotypes)
        .add("ids", ids)
        .add("subjectIds", subjectIds)
        .add("dataRanges", dataRanges)
        .add("dateRanges", dateRanges)
        .add("function", function)
        .toString();
  }
}
