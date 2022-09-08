package care.smith.top.top_phenotypic_query.adapter.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import care.smith.top.backend.model.Restriction;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;

public class CodeMapping {

  private String code;
  private String type;
  private Map<String, String> phenotypeMappings = new HashMap<>();
  private Map<Restriction, Restriction> restrictionMappings = new HashMap<>();

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPhenotypeMapping(String name) {
    return phenotypeMappings.get(name);
  }

  public Map<String, String> getPhenotypeMappings() {
    return phenotypeMappings;
  }

  public void setPhenotypeMappings(Map<String, String> phenotypeMappings) {
    this.phenotypeMappings = phenotypeMappings;
  }

  public void setRestrictionMappings(List<RestrictionMapping> restrictionMappings) {
    for (RestrictionMapping rm : restrictionMappings)
      this.restrictionMappings.put(rm.getModelRestriction(), rm.getSourceRestriction());
  }

  public boolean hasRestrictionMappings() {
    return !restrictionMappings.isEmpty();
  }

  public Restriction getSourceRestriction(Restriction modelRestriction) {
    Restriction sourceRestriction =
        restrictionMappings.get(RestrictionUtil.copyWithoutBasicAttributes(modelRestriction));
    if (sourceRestriction == null) return modelRestriction;
    return RestrictionUtil.setType(
        sourceRestriction
            .cardinality(modelRestriction.getCardinality())
            .quantifier(modelRestriction.getQuantifier())
            .negated(modelRestriction.isNegated()));
  }

  @Override
  public String toString() {
    return "CodeMapping [code="
        + code
        + ", type="
        + type
        + ", phenotypeMappings="
        + phenotypeMappings
        + ", restrictionMappings="
        + restrictionMappings
        + "]";
  }
}
