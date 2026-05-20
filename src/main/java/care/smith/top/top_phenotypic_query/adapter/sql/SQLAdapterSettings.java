package care.smith.top.top_phenotypic_query.adapter.sql;

import care.smith.top.model.*;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterSettings;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQueryBuilder;
import care.smith.top.top_phenotypic_query.adapter.config.Props;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Query;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SQLAdapterSettings extends DataAdapterSettings {
  
  private enum ParameterType {
    code,
    sex,
    birthdate,
    date,
    restriction
  }
  
  private static SQLAdapterSettings instance = null;
  
  private SQLAdapterSettings() {
  }
  
  public static SQLAdapterSettings get() {
    if (instance == null) instance = new SQLAdapterSettings();
    return instance;
  }
  
  @Override
  public String formatNumber(BigDecimal num) {
    return num.toPlainString();
  }
  
  @Override
  public String formatDateTime(LocalDateTime date) {
    return "'" + DateUtil.format(date) + "'";
  }
  
  @Override
  public String formatBoolean(Boolean bool) {
    return bool.toString();
  }
  
  @Override
  public String formatString(String str) {
    return "'" + str + "'";
  }
  
  @Override
  public String formatList(Stream<String> values) {
    return values.collect(Collectors.joining(", "));
  }
  
  @Override
  public String formatOperator(RestrictionOperator oper) {
    return oper.getValue();
  }
  
  @Override
  protected String getSexList(Restriction r, SubjectSearch search) {
    return generateNamedPlaceholders(ParameterType.sex, Restrictions.getValuesCount(r));
  }
  
  @Override
  protected Map<String, String> getBirthdateInterval(Restriction r, SubjectSearch search) {
    return generateNamedPlaceholders(ParameterType.birthdate, Restrictions.getIntervalAsStringMap(r));
  }
  
  @Override
  protected void addCodeList(Phenotype p, PhenotypeQueryBuilder builder, SingleSearch search) {
    String valuesAsString =
            generateNamedPlaceholders(ParameterType.code, Phenotypes.getUnrestrictedPhenotypeCodes(p).size());
    builder.baseQuery(valuesAsString, search.getQuery().getDataSource());
  }
  
  @Override
  protected void addValueInterval(
          Restriction r, PhenotypeQueryBuilder builder, SingleSearch search) {
    Map<String, String> interval = generateNamedPlaceholders(ParameterType.restriction, Restrictions.getIntervalAsStringMap(r));
    for (String key : interval.keySet())
      builder.valueIntervalLimit(r.getType(), key, interval.get(key));
  }
  
  @Override
  protected void addValueList(Restriction r, PhenotypeQueryBuilder builder, SingleSearch search) {
    String valuesAsString = generateNamedPlaceholders(ParameterType.restriction, Restrictions.getValuesCount(r));
    builder.valueList(r.getType(), valuesAsString);
  }
  
  @Override
  protected void addDateInterval(
          Restriction r, PhenotypeQueryBuilder builder, SingleSearch search) {
    Map<String, String> interval = generateNamedPlaceholders(ParameterType.date, Restrictions.getIntervalAsStringMap(r));
    for (String key : interval.keySet()) builder.dateTimeIntervalLimit(key, interval.get(key));
  }
  
  public Query getSubjectSqlQuery(
          String queryString, Handle handle, SubjectSearch search) {
    
    Query query = handle.createQuery(queryString);
    
    if (search.hasSexRestriction()) {
      Restriction sexR = search.getSexRestriction();
      if (Restrictions.hasValues(sexR))
        setValues(ParameterType.sex, query, search.getSexMapping().getConvertedRestriction(sexR));
    }
    
    if (search.hasBirthdateRestriction()) {
      Restriction birthdateR = search.getBirthdateRestriction();
      if (Restrictions.hasInterval(birthdateR))
        setDateTimeValues(
                ParameterType.birthdate, query, search.getBirthdateMapping().getConvertedRestriction(birthdateR));
    }
    
    return query;
  }
  
  public Query getSingleSqlQuery(
          String queryString, Handle handle, SingleSearch search) {
    
    Query query = handle.createQuery(queryString);
    
    if (search.getPhenotypeQuery().getBaseQuery().contains(Props.VAR_CODES)) {
      int paramNum = 0;
      for (Code code : Phenotypes.getUnrestrictedPhenotypeCodes(search.getPhenotype()))
        query.bind(ParameterType.code.toString() + paramNum++, Phenotypes.getCodeUri(code));
    }
    
    if (search.hasRestriction()) {
      Restriction r = search.getRestriction();
      if (r.getQuantifier() != Quantifier.ALL
              && (Restrictions.hasInterval(r) || Restrictions.hasValues(r)))
        setValues(ParameterType.restriction, query, search.getConvertedRestriction());
    }
    
    if (search.hasDateTimeRestriction()) {
      DateTimeRestriction dtr = search.getDateTimeRestriction();
      if (Restrictions.hasInterval(dtr)) setDateTimeValues(ParameterType.date, query, dtr);
    }
    
    return query;
  }
  
  private String generateNamedPlaceholders(ParameterType type, int num) {
    return String.join(",", IntStream.range(0, num).mapToObj(i -> String.format(":%s%d", type, i)).toList());
  }
  
  private Map<String, String> generateNamedPlaceholders(ParameterType type, Map<String, String> interval) {
    int i = 0;
    for (String oper : interval.keySet()) interval.put(oper, String.format(":%s%d", type, i++));
    return interval;
  }
  
  private void setValues(ParameterType type, Query query, Restriction r) {
    if (Restrictions.hasNumberType(r)) setNumberValues(type, query, r);
    else if (Restrictions.hasDateTimeType(r)) setDateTimeValues(type, query, r);
    else if (Restrictions.hasBooleanType(r)) setBooleanValues(type, query, r);
    else setStringValues(type, query, r);
  }
  
  private void setStringValues(ParameterType type, Query query, Restriction r) {
    int paramNum = 0;
    for (String v : Restrictions.getStringValues(r)) {
      if (v != null) query.bind(type.toString() + paramNum++, v);
    }
  }
  
  private void setNumberValues(ParameterType type, Query query, Restriction r) {
    int paramNum = 0;
    for (BigDecimal v : Restrictions.getNumberValues(r)) {
      if (v != null) query.bind(type.toString() + paramNum++, v);
    }
  }
  
  private void setBooleanValues(ParameterType type, Query query, Restriction r) {
    int paramNum = 0;
    for (Boolean v : Restrictions.getBooleanValues(r)) {
      if (v != null) query.bind(type.toString() + paramNum++, v);
    }
  }
  
  private void setDateTimeValues(ParameterType type, Query query, Restriction r) {
    int paramNum = 0;
    for (LocalDateTime v : Restrictions.getDateTimeValues(r)) {
      if (v != null) query.bind(type.toString() + paramNum++, Timestamp.valueOf(v));
    }
  }
}
