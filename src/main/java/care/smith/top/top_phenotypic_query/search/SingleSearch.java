package care.smith.top.top_phenotypic_query.search;

import java.util.Objects;

import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;

public class SingleSearch extends PhenotypeSearch {

  private QueryCriterion criterion;
  private Phenotype phenotype;
  private DataAdapter adapter;
  private int type;

  protected SingleSearch(Query query, QueryCriterion criterion, DataAdapter adapter) {
    super(query);
    this.criterion = criterion;
    this.adapter = adapter;
    this.phenotype = criterion.getSubject();
    if (criterion.isExclusion()) this.type = 2;
    else this.type = 1;
  }

  protected SingleSearch(
      Query query, QueryCriterion criterion, Phenotype phenotype, DataAdapter adapter) {
    this(query, criterion, adapter);
    this.phenotype = phenotype;
    this.type = 0;
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

  @Override
  public ResultSet execute() {
    return adapter.execute(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(criterion.getDateTimeRestrictions(), phenotype.getId());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SingleSearch other = (SingleSearch) obj;
    return Objects.equals(
            criterion.getDateTimeRestrictions(), other.criterion.getDateTimeRestrictions())
        && Objects.equals(phenotype.getId(), other.phenotype.getId());
  }
}
