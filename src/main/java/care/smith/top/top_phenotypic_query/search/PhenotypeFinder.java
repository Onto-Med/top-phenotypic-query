package care.smith.top.top_phenotypic_query.search;

import care.smith.top.simple_onto_api.ClassList;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class PhenotypeFinder {

  private ClassList onto;
  private DataAdapter adap;

  public PhenotypeFinder(ClassList onto, DataAdapter adap) {
    this.onto = onto;
    this.adap = adap;
  }

  public ResultSet find() {
    // create SubjectSearch and PropertySearch objects

    // call findSubjects() and findProperties() methods of DataAdapter

    // calculate and return ResultSet
    return null;
  }
}
