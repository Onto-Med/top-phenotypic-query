package care.smith.top.top_phenotypic_query.search;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.model.Restriction;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQuery;
import care.smith.top.top_phenotypic_query.adapter.config.Props;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Phenotypes;

public class SingleSearch extends PhenotypeSearch {

  private QueryCriterion criterion;
  private Phenotype phenotype;
  private DataAdapter adapter;
  private DataAdapterConfig config;
  private int type;

  public SingleSearch(
      Query query,
      QueryCriterion criterion,
      Phenotype phenotype,
      DataAdapter adapter,
      boolean isCriterion) {
    super(query);
    this.criterion = criterion;
    this.phenotype = phenotype;
    this.adapter = adapter;
    this.config = adapter.getConfig();
    if (isCriterion) {
      if (criterion.isInclusion()) this.type = 1;
      else this.type = 2;
    } else this.type = 0;
  }

  protected boolean isVariable() {
    return type == 0;
  }

  protected boolean isCriterion() {
    return type != 0;
  }

  protected boolean isInclusion() {
    return type == 1;
  }

  protected boolean isExclusion() {
    return type == 2;
  }

  public QueryCriterion getCriterion() {
    return criterion;
  }

  public Phenotype getPhenotype() {
    return phenotype;
  }

  public Phenotype getSuperPhenotype() {
    return phenotype.getSuperPhenotype();
  }

  public boolean hasDateTimeRestriction() {
    return criterion.getDateTimeRestriction() != null;
  }

  public DateTimeRestriction getDateTimeRestriction() {
    return criterion.getDateTimeRestriction();
  }

  public CodeMapping getCodeMapping() {
    return config.getCodeMapping(phenotype);
  }

  public String getModelUnit() {
    return phenotype.getUnit();
  }

  public String getSourceUnit() {
    return (getCodeMapping() == null) ? null : getCodeMapping().getUnit();
  }

  public String getType() {
    if (getCodeMapping() != null && getCodeMapping().getType() != null)
      return getCodeMapping().getType();
    if (config.getPhenotypeQuery(phenotype.getItemType().getValue()) != null)
      return phenotype.getItemType().getValue();
    return Props.DEFAULT_ITEM_TYPE;
  }

  public Map<String, String> getPhenotypeMappings() {
    if (getCodeMapping() != null && getCodeMapping().getPhenotypeMappings() != null) {
      Map<String, String> pheMap = getCodeMapping().getPhenotypeMappings();
      if (!pheMap.containsKey(Props.CODES))
        pheMap.put(Props.CODES, adapter.getSettings().getCodeUrisAsString(phenotype));
      return pheMap;
    }
    return Collections.singletonMap(
        Props.CODES, adapter.getSettings().getCodeUrisAsString(phenotype));
  }

  public PhenotypeQuery getPhenotypeQuery() {
    return config.getPhenotypeQuery(getType());
  }

  public PhenotypeOutput getOutput() {
    return getPhenotypeQuery().getOutput().mapping(getPhenotypeMappings());
  }

  public boolean hasRestriction() {
    return Phenotypes.isSingleRestriction(phenotype);
  }

  public Restriction getRestriction() {
    return phenotype.getRestriction();
  }

  public DataAdapter getAdapter() {
    return adapter;
  }

  public DataAdapterConfig getAdapterConfig() {
    return config;
  }

  @Override
  public ResultSet execute() {
    return adapter.execute(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(criterion.getDateTimeRestriction(), phenotype.getId());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SingleSearch other = (SingleSearch) obj;
    return Objects.equals(
            criterion.getDateTimeRestriction(), other.criterion.getDateTimeRestriction())
        && Objects.equals(phenotype.getId(), other.phenotype.getId());
  }
}
