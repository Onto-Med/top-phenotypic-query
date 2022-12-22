package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.ucum.UCUM;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.ValBuild;

public class ResultSet extends HashMap<String, SubjectPhenotypes> {

  private static final long serialVersionUID = 1L;

  public ResultSet() {}

  private ResultSet(ResultSet rs2) {
    super(rs2);
  }

  public void setPhenotypes(SubjectPhenotypes phenotypes) {
    put(phenotypes.getSubjectId(), phenotypes);
  }

  public void setPhenotypes(SubjectPhenotypes... phenotypes) {
    for (SubjectPhenotypes p : phenotypes) setPhenotypes(p);
  }

  public Set<String> getSubjectIds() {
    return keySet();
  }

  public SubjectPhenotypes getPhenotypes(String subjectId) {
    return get(subjectId);
  }

  public void addValue(
      String subjectId, String phenotypeName, DateTimeRestriction dateRange, Value val) {
    SubjectPhenotypes phes = get(subjectId);
    if (phes == null) {
      phes = new SubjectPhenotypes(subjectId);
      setPhenotypes(phes);
    }
    phes.addValue(phenotypeName, dateRange, val);
  }

  public void addValue(
      String subjectId, Phenotype phenotype, DateTimeRestriction dateRange, Value val) {
    if (val != null) addValue(subjectId, Phenotypes.getPhenotypeId(phenotype), dateRange, val);
  }

  public void addValue(
      String subjectId,
      Phenotype phenotype,
      DateTimeRestriction dateRange,
      Value val,
      String sourceUnit,
      String modelUnit) {
    if (sourceUnit != null && modelUnit != null && Values.hasNumberType(val))
      val =
          ValBuild.of(
              UCUM.convert(Values.getNumberValue(val), sourceUnit, modelUnit), val.getDateTime());
    addValue(subjectId, phenotype, dateRange, val);
  }

  private void addRestriction(
      String subjectId, Phenotype phenotype, DateTimeRestriction dateRange, Value val) {
    if (val != null && Phenotypes.hasExistentialQuantifier(phenotype)) {
      List<Value> vals = getValues(subjectId, phenotype.getId(), dateRange);
      if (vals == null || vals.isEmpty())
        addValue(subjectId, phenotype.getId(), dateRange, ValBuild.ofTrue());
    }
  }

  public void addValueWithRestriction(
      String subjectId, Phenotype phenotype, DateTimeRestriction dateRange, Value val) {
    addValue(subjectId, phenotype, dateRange, val);
    addRestriction(subjectId, phenotype, dateRange, val);
  }

  public void addValueWithRestriction(
      String subjectId,
      Phenotype phenotype,
      DateTimeRestriction dateRange,
      Value val,
      String sourceUnit,
      String modelUnit) {
    addValue(subjectId, phenotype, dateRange, val, sourceUnit, modelUnit);
    addRestriction(subjectId, phenotype, dateRange, val);
  }

  public void addSubject(String subjectId) {
    if (!getSubjectIds().contains(subjectId)) put(subjectId, new SubjectPhenotypes(subjectId));
  }

  public PhenotypeValues getValues(String subjectId, String phenotypeName) {
    SubjectPhenotypes subPhens = get(subjectId);
    if (subPhens == null) return null;
    return subPhens.getValues(phenotypeName);
  }

  public List<Value> getValues(
      String subjectId, String phenotypeName, DateTimeRestriction dateRange) {
    PhenotypeValues values = getValues(subjectId, phenotypeName);
    if (values == null) return null;
    return values.getValues(dateRange);
  }

  public ResultSet intersect(ResultSet rs2) {
    ResultSet intersection = new ResultSet(this);
    intersection.keySet().retainAll(rs2.keySet());

    for (String sbjId : intersection.getSubjectIds()) {
      SubjectPhenotypes phenotypes1 = intersection.getPhenotypes(sbjId);
      SubjectPhenotypes phenotypes2 = rs2.getPhenotypes(sbjId);
      for (String pheName : phenotypes2.getPhenotypeNames()) {
        if (!phenotypes1.hasPhenotype(pheName))
          phenotypes1.setValues(phenotypes2.getValues(pheName));
        else {
          PhenotypeValues values1 = phenotypes1.getValues(pheName);
          PhenotypeValues values2 = phenotypes2.getValues(pheName);
          for (DateTimeRestriction dateRange : values2.getDateTimeRestrictions()) {
            if (!values1.hasDateTimeRestriction(dateRange))
              values1.setValues(dateRange, values2.getValues(dateRange));
          }
        }
      }
    }

    return intersection;
  }

  public ResultSet subtract(ResultSet rs2) {
    ResultSet difference = new ResultSet(this);
    difference.keySet().removeAll(rs2.keySet());
    return difference;
  }

  public ResultSet insert(ResultSet rs2) {
    ResultSet insert = new ResultSet(this);

    for (String sbjId : insert.getSubjectIds()) {
      SubjectPhenotypes phenotypes1 = insert.getPhenotypes(sbjId);
      SubjectPhenotypes phenotypes2 = rs2.getPhenotypes(sbjId);
      if (phenotypes2 == null) continue;
      for (String pheName : phenotypes2.getPhenotypeNames()) {
        if (!phenotypes1.hasPhenotype(pheName))
          phenotypes1.setValues(phenotypes2.getValues(pheName));
        else {
          PhenotypeValues values1 = phenotypes1.getValues(pheName);
          PhenotypeValues values2 = phenotypes2.getValues(pheName);
          for (DateTimeRestriction dateRange : values2.getDateTimeRestrictions()) {
            if (!values1.hasDateTimeRestriction(dateRange))
              values1.setValues(dateRange, values2.getValues(dateRange));
          }
        }
      }
    }

    return insert;
  }

  @Override
  public String toString() {
    StringBuffer sb =
        new StringBuffer("Result Set:")
            .append(System.lineSeparator())
            .append(System.lineSeparator());
    for (SubjectPhenotypes phes : values()) sb.append(phes);
    return sb.toString();
  }

  public String toString(Entities phenotypes) {
    String str = toString();
    for (Phenotype p : phenotypes.getPhenotypes())
      str = str.replaceAll(p.getId(), p.getId() + "::" + Entities.getFirstTitle(p));
    return str;
  }
}
