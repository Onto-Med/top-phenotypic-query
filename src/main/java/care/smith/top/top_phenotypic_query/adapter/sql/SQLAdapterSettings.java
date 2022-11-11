package care.smith.top.top_phenotypic_query.adapter.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;
import care.smith.top.model.RestrictionOperator;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterSettings;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQueryBuilder;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Restrictions;

public class SQLAdapterSettings extends DataAdapterSettings {

  private static SQLAdapterSettings instance = null;

  private SQLAdapterSettings() {}

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
    return generateQuestionMarks(Restrictions.getValuesCount(r));
  }

  @Override
  protected Map<String, String> getBirthdateInterval(Restriction r, SubjectSearch search) {
    return generateQuestionMarks(Restrictions.getIntervalAsStringMap(r));
  }

  @Override
  protected void addValueInterval(
      Restriction r, PhenotypeQueryBuilder builder, SingleSearch search) {
    Map<String, String> interval = generateQuestionMarks(Restrictions.getIntervalAsStringMap(r));
    for (String key : interval.keySet()) builder.valueIntervalLimit(key, interval.get(key));
  }

  @Override
  protected void addValueList(Restriction r, PhenotypeQueryBuilder builder, SingleSearch search) {
    String valuesAsString = generateQuestionMarks(Restrictions.getValuesCount(r));
    builder.valueList(valuesAsString);
  }

  @Override
  protected void addDateInterval(
      Restriction r, PhenotypeQueryBuilder builder, SingleSearch search) {
    Map<String, String> interval = generateQuestionMarks(Restrictions.getIntervalAsStringMap(r));
    for (String key : interval.keySet()) builder.dateIntervalLimit(key, interval.get(key));
  }

  @Override
  public Map<String, String> getPhenotypeMappings(SingleSearch search) {
    CodeMapping codeMap = search.getAdapterConfig().getCodeMapping(search.getPhenotype());
    if (codeMap == null) return null;
    return codeMap.getPhenotypeMappings();
  }

  public PreparedStatement getSubjectPreparedStatement(
      String query, Connection con, SubjectSearch search) throws SQLException {
    PreparedStatement ps = con.prepareStatement(query);

    if (search.hasSexRestriction()) {
      Restriction sexR = search.getSexRestriction();
      if (Restrictions.hasValues(sexR)) {
        Restriction sourceSexR = search.getSexMapping().getSourceRestriction(sexR);
        if (Restrictions.hasNumberType(sourceSexR)) setNumberValues(ps, sourceSexR);
        else if (Restrictions.hasBooleanType(sourceSexR)) setBooleanValues(ps, sourceSexR);
        else if (Restrictions.hasStringType(sourceSexR)) setStringValues(ps, sourceSexR);
      }
    }

    if (search.hasBirthdateRestriction()) {
      Restriction birthdateR = search.getBirthdateRestriction();
      if (Restrictions.hasInterval(birthdateR))
        setDateTimeValues(ps, search.getBirthdateMapping().getSourceRestriction(birthdateR));
    }

    return ps;
  }

  public PreparedStatement getSinglePreparedStatement(
      String query, Connection con, SingleSearch search) throws SQLException {
    PreparedStatement ps = con.prepareStatement(query);

    if (search.hasRestriction()) {
      Phenotype superPhe = search.getSuperPhenotype();
      Restriction r = search.getRestriction();
      if (r.getQuantifier() != Quantifier.ALL) {
        Restriction sourceR = search.getCodeMapping().getSourceRestriction(r, superPhe);
        if (Restrictions.hasInterval(r) || Restrictions.hasValues(r)) {
          if (Restrictions.hasNumberType(sourceR)) setNumberValues(ps, sourceR);
          else if (Restrictions.hasBooleanType(sourceR)) setBooleanValues(ps, sourceR);
          else if (Restrictions.hasStringType(sourceR)) setStringValues(ps, sourceR);
          else setDateTimeValues(ps, sourceR);
        }
      }
    }

    if (search.hasDateTimeRestriction()) {
      DateTimeRestriction dtr = search.getDateTimeRestriction();
      if (Restrictions.hasInterval(dtr)) setDateTimeValues(ps, dtr);
    }

    return ps;
  }

  private String generateQuestionMarks(int num) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < num; i++) builder.append("?,");
    return builder.deleteCharAt(builder.length() - 1).toString();
  }

  private Map<String, String> generateQuestionMarks(Map<String, String> interval) {
    for (String oper : interval.keySet()) interval.put(oper, "?");
    return interval;
  }

  private void setStringValues(PreparedStatement ps, Restriction r) throws SQLException {
    List<String> vals = Restrictions.getStringValues(r);
    for (int i = 0; i < vals.size(); i++) {
      String v = vals.get(i);
      if (v != null) ps.setString(i + 1, v);
    }
  }

  private void setNumberValues(PreparedStatement ps, Restriction r) throws SQLException {
    List<BigDecimal> vals = Restrictions.getNumberValues(r);
    for (int i = 0; i < vals.size(); i++) {
      BigDecimal v = vals.get(i);
      if (v != null) ps.setBigDecimal(i + 1, v);
    }
  }

  private void setBooleanValues(PreparedStatement ps, Restriction r) throws SQLException {
    List<Boolean> vals = Restrictions.getBooleanValues(r);
    for (int i = 0; i < vals.size(); i++) {
      Boolean v = vals.get(i);
      if (v != null) ps.setBoolean(i + 1, v);
    }
  }

  private void setDateTimeValues(PreparedStatement ps, Restriction r) throws SQLException {
    List<LocalDateTime> vals = Restrictions.getDateTimeValues(r);
    for (int i = 0; i < vals.size(); i++) {
      LocalDateTime v = vals.get(i);
      if (v != null) ps.setTimestamp(i + 1, Timestamp.valueOf(v));
    }
  }
}
