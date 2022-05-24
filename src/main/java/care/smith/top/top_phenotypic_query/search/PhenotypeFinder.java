package care.smith.top.top_phenotypic_query.search;

import care.smith.top.simple_onto_api.ClassList;
import care.smith.top.simple_onto_api.model.ClassDef;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.enums.PhenotypeClass;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class PhenotypeFinder {

  private ClassList onto;
  private DataAdapter adap;

  private ClassDef genderCls;
  private ClassDef ageCls;
  private ClassDef[] inclusion;
  private ClassDef[] exclusion;
  private ClassDef[] projection;

  public PhenotypeFinder(ClassList onto, DataAdapter adap) {
    this.onto = onto;
    this.adap = adap;
  }

  public PhenotypeFinder genderClass(ClassDef genderCls) {
    this.genderCls = genderCls;
    return this;
  }

  public PhenotypeFinder ageClass(ClassDef ageCls) {
    this.ageCls = ageCls;
    return this;
  }

  public PhenotypeFinder inclusion(ClassDef... inclusion) {
    this.inclusion = inclusion;
    return this;
  }

  public PhenotypeFinder exclusion(ClassDef... exclusion) {
    this.exclusion = exclusion;
    return this;
  }

  public PhenotypeFinder projection(ClassDef... projection) {
    this.projection = projection;
    return this;
  }

  private boolean isCls(ClassDef inputCls, ClassDef outputCls) {
    if (outputCls.getName().equals(inputCls.getName())) return true;
    if (PhenotypeClass.isRSiP(inputCls)
        && outputCls.getName().equals(onto.getSuperClassName(inputCls.getName()))) return true;
    return false;
  }

  private boolean isAge(ClassDef cls) {
    return isCls(cls, ageCls);
  }

  private boolean isGender(ClassDef cls) {
    return isCls(cls, genderCls);
  }

  public ResultSet find() {
    // create SubjectSearch and PropertySearch objects

    // call findSubjects() and findProperties() methods of DataAdapter

    // calculate and return ResultSet
    return null;
  }
}
