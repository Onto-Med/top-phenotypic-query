package care.smith.top.top_phenotypic_query.search;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.NumberRestriction;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Query;
import care.smith.top.backend.model.Restriction;
import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectQuery;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectQueryBuilder;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.PhenotypeUtil;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;

public class SubjectSearch extends PhenotypeSearch {

  private Phenotype sex;
  private Phenotype birthdate;
  private Phenotype age;
  private DataAdapter adapter;
  private DataAdapterConfig config;
  private int type = 0;

  public SubjectSearch(
      Query query, Phenotype sex, Phenotype birthdate, Phenotype age, DataAdapter adapter) {
    super(query);
    this.sex = sex;
    this.birthdate = birthdate;
    this.age = age;
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
    if (birthdate != null) return birthdate;
    if (age != null) return ageToBirthdate();
    return null;
  }

  public Phenotype getAge() {
    return age;
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

  public static String getBaseQuery(DataAdapterConfig config) {
    return config.getSubjectQuery().getQueryBuilder().baseQuery().build();
  }

  public static String getIdColumn(DataAdapterConfig config) {
    return config.getSubjectQuery().getOutput().getId();
  }

  public String getQueryString() {
    SubjectQueryBuilder builder = getSubjectQuery().getQueryBuilder().baseQuery();

    if (sex != null && PhenotypeUtil.isSingleRestriction(sex)) {
      Restriction sexR = sex.getRestriction();
      if (RestrictionUtil.hasValues(sexR))
        builder.sexList(
            RestrictionUtil.getValuesAsString(
                getSexMapping().getSourceRestriction(sexR), adapter.getFormat()));
    }

    Phenotype bd = getBirthdate();
    if (bd != null && PhenotypeUtil.isSingleRestriction(bd)) {
      Restriction birthdateR = bd.getRestriction();
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

  private LocalDateTime ageToBirthdate(long years) {
    return LocalDate.now().minusYears(years).atStartOfDay();
  }

  public static long birthdateToAge(LocalDateTime birthdate) {
    return ChronoUnit.YEARS.between(birthdate.toLocalDate(), LocalDate.now());
  }

  private Phenotype ageToBirthdate() {
    Phenotype bd = new Phenotype().dataType(DataType.DATE_TIME);
    bd.setId("birthdate");
    bd.setEntityType(EntityType.SINGLE_PHENOTYPE);
    if (PhenotypeUtil.isSinglePhenotype(age)) return bd;

    NumberRestriction ageR = (NumberRestriction) age.getRestriction();
    DateTimeRestriction bdR = new DateTimeRestriction();
    bdR.setQuantifier(ageR.getQuantifier());
    bdR.setCardinality(ageR.getCardinality());

    BigDecimal ageMin = RestrictionUtil.getMinIntervalValue(ageR);
    BigDecimal ageMax = RestrictionUtil.getMaxIntervalValue(ageR);

    if (ageMax != null) {
      bdR.addValuesItem(ageToBirthdate(ageMax.longValue()));
      if (ageR.getMaxOperator() == RestrictionOperator.LESS_THAN)
        bdR.setMinOperator(RestrictionOperator.GREATER_THAN);
      else if (ageR.getMaxOperator() == RestrictionOperator.LESS_THAN_OR_EQUAL_TO)
        bdR.setMinOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO);
    }

    if (ageMin != null) {
      bdR.addValuesItem(ageToBirthdate(ageMin.longValue()));
      if (ageR.getMinOperator() == RestrictionOperator.GREATER_THAN)
        bdR.setMaxOperator(RestrictionOperator.LESS_THAN);
      else if (ageR.getMinOperator() == RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
        bdR.setMaxOperator(RestrictionOperator.LESS_THAN_OR_EQUAL_TO);
    }

    Phenotype bdRestricted = new Phenotype().restriction(bdR).superPhenotype(bd);
    bdRestricted.setId(age.getId());
    bdRestricted.setEntityType(EntityType.SINGLE_RESTRICTION);

    return bdRestricted;
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
