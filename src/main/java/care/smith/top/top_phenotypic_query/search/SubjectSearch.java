package care.smith.top.top_phenotypic_query.search;

import java.util.Map;
import java.util.Objects;

import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.Restriction;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectQuery;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectQueryBuilder;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;

public class SubjectSearch extends PhenotypeSearch {

  private Phenotype sex;
  private Phenotype birthdate;
  private DataAdapter adapter;
  private DataAdapterConfig config;
  private int type = 0;

  public SubjectSearch(Query query, Phenotype sex, Phenotype birthdate, DataAdapter adapter) {
    super(query);
    this.sex = sex;
    this.birthdate = birthdate;
    this.adapter = adapter;
    this.config = adapter.getConfig();
  }

  protected boolean isVariable() {
    return type == 0;
  }

  protected SubjectSearch setIsVariable() {
    type = 0;
    return this;
  }

  protected boolean isCriterion() {
    return type != 0;
  }

  protected boolean isInclusion() {
    return type == 1;
  }

  protected SubjectSearch setIsInclusion() {
    type = 1;
    return this;
  }

  protected boolean isExclusion() {
    return type == 2;
  }

  protected SubjectSearch setIsExclusion() {
    type = 2;
    return this;
  }

  public Phenotype getSex() {
    return sex;
  }

  public Phenotype getBirthdate() {
    return birthdate;
  }

  public CodeMapping getSexMapping() {
    return config.getSexMapping();
  }

  public CodeMapping getBirthdateMapping() {
    return config.getBirthdateMapping();
  }

  public SubjectQuery getSubjectQuery() {
    return config.getSubjectQuery();
  }

  public SubjectOutput getOutput() {
    return getSubjectQuery().getOutput();
  }

  public String getQueryString() {
    SubjectQueryBuilder builder = getSubjectQuery().getQueryBuilder().baseQuery();

    if (sex != null) {
      Restriction sexR = sex.getRestriction();
      if (RestrictionUtil.hasValues(sexR))
        builder.sexList(
            RestrictionUtil.getValuesAsString(
                getSexMapping().getSourceRestriction(sexR), adapter.getFormat()));
    }

    if (birthdate != null) {
      Restriction birthdateR = birthdate.getRestriction();
      if (RestrictionUtil.hasInterval(birthdateR)) {
        Map<String, String> interval =
            RestrictionUtil.getInterval(
                getBirthdateMapping().getSourceRestriction(birthdateR), adapter.getFormat());
        for (String key : interval.keySet()) builder.birthdateIntervalLimit(key, interval.get(key));
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
    return Objects.hash(birthdate.getId(), sex.getId(), type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SubjectSearch other = (SubjectSearch) obj;
    return Objects.equals(birthdate.getId(), other.birthdate.getId())
        && Objects.equals(sex.getId(), other.sex.getId())
        && type == other.type;
  }
}
