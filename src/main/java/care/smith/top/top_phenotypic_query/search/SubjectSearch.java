package care.smith.top.top_phenotypic_query.search;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

import care.smith.top.model.DataType;
import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.EntityType;
import care.smith.top.model.NumberRestriction;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectQuery;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectQueryBuilder;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Restrictions;

public class SubjectSearch extends PhenotypeSearch {

  private Phenotype sex;
  private Phenotype birthdate;
  private Phenotype age;
  private DataAdapter adapter;
  private DataAdapterConfig config;

  public SubjectSearch(
      Query query, Phenotype sex, Phenotype birthdate, Phenotype age, DataAdapter adapter) {
    super(query);
    this.sex = sex;
    this.birthdate = birthdate;
    this.age = age;
    this.adapter = adapter;
    this.config = adapter.getConfig();
  }

  public Phenotype getSex() {
    return sex;
  }

  public Phenotype getBirthdate() {
    return birthdate;
  }

  public Phenotype getAge() {
    return age;
  }

  public Phenotype getAgeToBirthdate() {
    if (age == null) return null;
    Phenotype bd = new Phenotype().dataType(DataType.DATE_TIME);
    bd.setId("birthdate");
    bd.setEntityType(EntityType.SINGLE_PHENOTYPE);
    if (Phenotypes.isSinglePhenotype(age)) return bd;

    NumberRestriction ageR = (NumberRestriction) age.getRestriction();
    DateTimeRestriction bdR = new DateTimeRestriction();
    bdR.setQuantifier(ageR.getQuantifier());
    bdR.setCardinality(ageR.getCardinality());

    BigDecimal ageMin = Restrictions.getMinIntervalValue(ageR);
    BigDecimal ageMax = Restrictions.getMaxIntervalValue(ageR);

    if (ageMax != null) {
      bdR.addValuesItem(ageToBirthdate(ageMax.longValue()));
      if (ageR.getMaxOperator() == RestrictionOperator.LESS_THAN)
        bdR.setMinOperator(RestrictionOperator.GREATER_THAN);
      else if (ageR.getMaxOperator() == RestrictionOperator.LESS_THAN_OR_EQUAL_TO)
        bdR.setMinOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO);
    }

    if (ageMin != null) {
      if (ageMax == null) bdR.addValuesItem(null);
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

  public Phenotype getBirthdateDerived() {
    return (birthdate != null) ? birthdate : getAgeToBirthdate();
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

    if (sex != null && Phenotypes.isSingleRestriction(sex)) {
      Restriction sexR = sex.getRestriction();
      if (Restrictions.hasValues(sexR))
        builder.sexList(
            Restrictions.getValuesAsString(
                getSexMapping().getSourceRestriction(sexR), adapter.getFormat()));
    }

    Phenotype bd = getBirthdateDerived();
    if (bd != null && Phenotypes.isSingleRestriction(bd)) {
      Restriction birthdateR = bd.getRestriction();
      if (Restrictions.hasInterval(birthdateR)) {
        Map<String, String> interval =
            Restrictions.getIntervalAsStringMap(
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

  @Override
  public int hashCode() {
    return Objects.hash(birthdate, age, sex);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SubjectSearch other = (SubjectSearch) obj;
    return Objects.equals(birthdate, other.birthdate)
        && Objects.equals(age, other.age)
        && Objects.equals(sex, other.sex);
  }
}
