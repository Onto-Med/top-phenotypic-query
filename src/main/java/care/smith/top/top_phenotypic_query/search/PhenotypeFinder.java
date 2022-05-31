package care.smith.top.top_phenotypic_query.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import care.smith.top.simple_onto_api.ClassList;
import care.smith.top.simple_onto_api.model.ClassDef;
import care.smith.top.simple_onto_api.model.property.data.range.DateRange;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.enums.Order;
import care.smith.top.top_phenotypic_query.enums.PhenotypeClass;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class PhenotypeFinder {

  private ClassList onto;
  private DataAdapter adap;

  private List<String> gender = new ArrayList<>();
  private List<DateRange> birthdate = new ArrayList<>();
  private Multimap<String, DateRange> inclusion = LinkedListMultimap.create();
  private Multimap<String, DateRange> exclusion = LinkedListMultimap.create();
  private HashMap<String, Order> projection = new LinkedHashMap<>();

  public PhenotypeFinder(ClassList onto, DataAdapter adap) {
    this.onto = onto;
    this.adap = adap;
  }

  public PhenotypeFinder gender(String genderClsName) {
    this.genderClsName = genderClsName;
    return this;
  }

  public PhenotypeFinder age(String ageClsName) {
    this.ageClsName = ageClsName;
    return this;
  }

  public PhenotypeFinder inclusion(String clsName, List<DateRange> dateRanges) {
    this.inclusion.putAll(clsName, dateRanges);
    return this;
  }

  public PhenotypeFinder inclusion(String clsName, DateRange... dateRanges) {
    return inclusion(clsName, Arrays.asList(dateRanges));
  }

  public PhenotypeFinder exclusion(String clsName, List<DateRange> dateRanges) {
    this.exclusion.putAll(clsName, dateRanges);
    return this;
  }

  public PhenotypeFinder exclusion(String clsName, DateRange... dateRanges) {
    return exclusion(clsName, Arrays.asList(dateRanges));
  }

  public PhenotypeFinder projection(String clsName, Order order) {
    this.projection.put(clsName, order);
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
