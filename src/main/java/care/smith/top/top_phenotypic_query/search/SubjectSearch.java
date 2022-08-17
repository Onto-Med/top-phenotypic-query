package care.smith.top.top_phenotypic_query.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class SubjectSearch extends PhenotypeSearch {

  private DataAdapter adapter;

  private List<QueryCriterion> ageRanges = new ArrayList<>();
  private List<QueryCriterion> genders = new ArrayList<>();
  private List<QueryCriterion> requiredPhenotypes = new ArrayList<>();

  public SubjectSearch(Query query, DataAdapter adapter) {
    super(query);
    this.adapter = adapter;
  }

  public List<QueryCriterion> getAgeRanges() {
    return ageRanges;
  }

  public void addAgeRange(QueryCriterion range) {
    this.ageRanges.add(range);
  }

  public void setAgeRanges(List<QueryCriterion> ranges) {
    this.ageRanges = ranges;
  }

  public void setAgeRanges(QueryCriterion... ranges) {
    setAgeRanges(Arrays.asList(ranges));
  }

  public List<QueryCriterion> getGenders() {
    return genders;
  }

  public void addGender(QueryCriterion gender) {
    this.genders.add(gender);
  }

  public void setGenders(List<QueryCriterion> genders) {
    this.genders = genders;
  }

  public void setGenders(QueryCriterion... genders) {
    setGenders(Arrays.asList(genders));
  }

  public List<QueryCriterion> getRequiredPhenotypes() {
    return requiredPhenotypes;
  }

  public void addRequiredPhenotypes(QueryCriterion requiredPhenotype) {
    this.requiredPhenotypes.add(requiredPhenotype);
  }

  public void setRequiredPhenotypes(List<QueryCriterion> requiredPhenotypes) {
    this.requiredPhenotypes = requiredPhenotypes;
  }

  public void setRequiredPhenotypes(QueryCriterion... requiredPhenotypes) {
    setRequiredPhenotypes(Arrays.asList(requiredPhenotypes));
  }

  @Override
  public ResultSet execute() {
    return adapter.execute(this);
  }
}
