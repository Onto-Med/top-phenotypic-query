package care.smith.top.top_phenotypic_query.search;

import java.util.Map;
import java.util.Objects;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.EntityType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.model.Restriction;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQuery;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQueryBuilder;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Restrictions;

public class SingleSearch extends PhenotypeSearch {

  private QueryCriterion criterion;
  private Phenotype phenotype;
  private DataAdapter adapter;
  private DataAdapterConfig config;
  private Map<String, Phenotype> phenotypes;
  private int type;

  public SingleSearch(
      Query query,
      QueryCriterion criterion,
      Phenotype phenotype,
      DataAdapter adapter,
      Map<String, Phenotype> phenotypes,
      boolean isCriterion) {
    super(query);
    this.criterion = criterion;
    this.phenotype = phenotype;
    this.adapter = adapter;
    this.config = adapter.getConfig();
    this.phenotypes = phenotypes;
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

  public DateTimeRestriction getDateTimeRestriction() {
    return criterion.getDateTimeRestriction();
  }

  private CodeMapping getCodeMapping() {
    return config.getCodeMapping(phenotype, phenotypes);
  }

  public String getModelUnit() {
    return phenotype.getUnit();
  }

  public String getSourceUnit() {
    return getCodeMapping().getUnit();
  }

  public String getType() {
    return getCodeMapping().getType();
  }

  public Map<String, String> getPhenotypeMappings() {
    return adapter.getPhenotypeMappings(phenotype, config, phenotypes);
  }

  public PhenotypeQuery getPhenotypeQuery() {
    return config.getPhenotypeQuery(getType());
  }

  public PhenotypeOutput getOutput() {
    return getPhenotypeQuery().getOutput().mapping(getPhenotypeMappings());
  }

  public String getQueryString() {
    PhenotypeQueryBuilder builder =
        getPhenotypeQuery().getQueryBuilder(getPhenotypeMappings()).baseQuery();
    CodeMapping codeMap = getCodeMapping();
    DateTimeRestriction dtr = getDateTimeRestriction();

    if (phenotype.getEntityType() == EntityType.SINGLE_RESTRICTION) {
      Phenotype superPhe = phenotypes.get(phenotype.getSuperPhenotype().getId());
      Restriction r = phenotype.getRestriction();
      if (r.getQuantifier() != Quantifier.ALL) {
        if (Restrictions.hasInterval(r)) {
          Map<String, String> interval =
              Restrictions.getIntervalAsStringMap(
                  codeMap.getSourceRestriction(r, superPhe), adapter.getFormat());
          for (String key : interval.keySet())
            adapter.addValueIntervalLimit(key, interval.get(key), builder, r);
        } else if (Restrictions.hasValues(r)) {
          String values =
              Restrictions.getValuesAsString(
                  codeMap.getSourceRestriction(r, superPhe), adapter.getFormat());
          adapter.addValueList(values, builder, r);
        }
      }
    }

    if (dtr != null) {
      if (Restrictions.hasInterval(dtr)) {
        Map<String, String> interval =
            Restrictions.getIntervalAsStringMap(dtr, adapter.getFormat());
        for (String key : interval.keySet())
          adapter.addDateIntervalLimit(key, interval.get(key), builder);
      }
    }

    return builder.build();
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
