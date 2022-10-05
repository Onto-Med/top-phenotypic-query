package care.smith.top.top_phenotypic_query.result;

import java.util.HashMap;
import java.util.Set;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Phenotype;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.simple_onto_api.model.property.data.value.list.ValueList;
import care.smith.top.top_phenotypic_query.ucum.UCUM;
import care.smith.top.top_phenotypic_query.util.PhenotypeUtil;

public class ResultSet extends HashMap<String, Phenotypes> {

  private static final long serialVersionUID = 1L;

  public ResultSet() {}

  private ResultSet(ResultSet rs2) {
    super(rs2);
  }

  public void setPhenotypes(Phenotypes phenotypes) {
    put(phenotypes.getSubjectId(), phenotypes);
  }

  public void setPhenotypes(Phenotypes... phenotypes) {
    for (Phenotypes p : phenotypes) setPhenotypes(p);
  }

  public Set<String> getSubjectIds() {
    return keySet();
  }

  public Phenotypes getPhenotypes(String subjectId) {
    return get(subjectId);
  }

  public void addValue(
      String subjectId, String phenotypeName, DateTimeRestriction dateRange, Value val) {
    Phenotypes phes = get(subjectId);
    if (phes == null) {
      phes = new Phenotypes(subjectId);
      setPhenotypes(phes);
    }
    phes.addValue(phenotypeName, dateRange, val);
  }

  public void addValue(
      String subjectId, Phenotype phenotype, DateTimeRestriction dateRange, Value val) {
    if (val != null) addValue(subjectId, PhenotypeUtil.getPhenotypeId(phenotype), dateRange, val);
  }

  public void addValue(
      String subjectId,
      Phenotype phenotype,
      DateTimeRestriction dateRange,
      Value val,
      String sourceUnit,
      String modelUnit) {
    if (sourceUnit != null && modelUnit != null && val instanceof DecimalValue)
      val = new DecimalValue(UCUM.convert(val.getValueDecimal(), sourceUnit, modelUnit));
    addValue(subjectId, phenotype, dateRange, val);
  }

  private void addRestriction(
      String subjectId, Phenotype phenotype, DateTimeRestriction dateRange, Value val) {
    if (val != null && PhenotypeUtil.hasExistentialQuantifier(phenotype))
      addValue(subjectId, phenotype.getId(), dateRange, new BooleanValue(true));
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
    if (!getSubjectIds().contains(subjectId)) put(subjectId, new Phenotypes(subjectId));
  }

  public Values getValues(String subjectId, String phenotypeName) {
    Phenotypes subPhens = get(subjectId);
    if (subPhens == null) return null;
    return subPhens.getValues(phenotypeName);
  }

  public ValueList getValues(
      String subjectId, String phenotypeName, DateTimeRestriction dateRange) {
    Values values = getValues(subjectId, phenotypeName);
    if (values == null) return null;
    return values.getValues(dateRange);
  }

  public ResultSet intersect(ResultSet rs2) {
    ResultSet intersection = new ResultSet(this);
    intersection.keySet().retainAll(rs2.keySet());

    for (String sbjId : intersection.getSubjectIds()) {
      Phenotypes phenotypes1 = intersection.getPhenotypes(sbjId);
      Phenotypes phenotypes2 = rs2.getPhenotypes(sbjId);
      for (String pheName : phenotypes2.getPhenotypeNames()) {
        if (!phenotypes1.hasPhenotype(pheName))
          phenotypes1.setValues(phenotypes2.getValues(pheName));
        else {
          Values values1 = phenotypes1.getValues(pheName);
          Values values2 = phenotypes2.getValues(pheName);
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
      Phenotypes phenotypes1 = insert.getPhenotypes(sbjId);
      Phenotypes phenotypes2 = rs2.getPhenotypes(sbjId);
      if (phenotypes2 == null) continue;
      for (String pheName : phenotypes2.getPhenotypeNames()) {
        if (!phenotypes1.hasPhenotype(pheName))
          phenotypes1.setValues(phenotypes2.getValues(pheName));
        else {
          Values values1 = phenotypes1.getValues(pheName);
          Values values2 = phenotypes2.getValues(pheName);
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
    for (Phenotypes phes : values()) sb.append(phes);
    return sb.toString();
  }
}
