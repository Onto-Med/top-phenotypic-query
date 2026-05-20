package care.smith.top.top_phenotypic_query.adapter.sql;

import care.smith.top.model.*;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterSettings;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.MapMapper;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class SQLAdapter extends DataAdapter {
  
  private Handle handle;
  
  private static final Logger log = LoggerFactory.getLogger(SQLAdapter.class);
  
  public SQLAdapter(DataAdapterConfig config) throws SQLException {
    this(config, "Default_SQL_Adapter");
  }
  
  protected SQLAdapter(DataAdapterConfig config, String defConfResName) throws SQLException {
    super(mergeDefault(config, defConfResName));
    initConnection();
  }
  
  private void initConnection() {
    this.handle = Jdbi.create(
            config.getConnectionAttribute("url"),
            config.getConnectionAttribute("user"),
            config.getConnectionAttribute("password")).
            open();
  }
  
  private MapMapper mapper = new MapMapper() {
    @Override
    public RowMapper<Map<String, Object>> specialize(java.sql.ResultSet rs, StatementContext ctx) throws SQLException {
      final RowMapper<Map<String, Object>> rootRowMapper = super.specialize(rs, ctx);
      return (r, c) -> {
        var result = new HashMap<String, Object>();
        rootRowMapper.map(rs, c).entrySet().stream().forEach(entry -> result.put(entry.getKey(), toOffsetDateTime(entry.getValue())));
        return result;
      };
    }
  };
  
  private static Object toOffsetDateTime(Object o) {
    if (o == null) return null;
    return switch (o) {
      case Date d -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(d.getTime()), ZoneId.systemDefault());
      case Timestamp ts -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneId.systemDefault());
      case Double d -> BigDecimal.valueOf(d);
      default -> o;
    };
  }
  
  public ResultIterable<Map<String, Object>> executeQuery(String query) {
    return handle.createQuery(query).map(mapper);
  }
  
  public Handle getConnection() {
    return handle;
  }
  
  private LocalDateTime getDate(String dateCol, Map<String, Object> row) {
    if (dateCol == null) return null;
    OffsetDateTime ts = (OffsetDateTime) row.get(dateCol);
    if (ts == null) return null;
    return ts.toLocalDateTime();
  }
  
  @Override
  public ResultSet execute(SingleSearch search) {
    ResultSet rs = new ResultSet();
    
    String preparedQuery = SQLAdapterSettings.get().createSinglePreparedQuery(search);
    var sqlQuery = SQLAdapterSettings.get().getSingleSqlQuery(preparedQuery, handle, search);
    log.info("Execute SQL query: {}", sqlQuery);
    var sqlRS = sqlQuery.map(mapper);
    Phenotype phe = search.getPhenotype();
    PhenotypeOutput out = search.getOutput();
    String sbjCol = out.getSubject();
    String pheCol = out.getValue(Phenotypes.getDataType(phe));
    String dateCol = out.getDateTime();
    String startDateCol = out.getStartDateTime();
    String endDateCol = out.getEndDateTime();
    
    for (Map<String, Object> row : sqlRS) {
      String sbj = row.get(sbjCol).toString();
      LocalDateTime date = getDate(dateCol, row);
      LocalDateTime startDate = getDate(startDateCol, row);
      LocalDateTime endDate = getDate(endDateCol, row);
      Value val;
      if (Phenotypes.hasBooleanType(phe)) {
        if (pheCol == null) {
          rs.addValue(
                  sbj, phe, search.getDateTimeRestriction(), Val.ofTrue(date, startDate, endDate));
          continue;
        } else val = Val.of((Boolean) row.get(pheCol), date, startDate, endDate);
      } else if (Phenotypes.hasDateTimeType(phe))
        val = Val.of(((Timestamp) row.get(pheCol)).toLocalDateTime(), date, startDate, endDate);
      else if (Phenotypes.hasNumberType(phe))
        val = Val.of((BigDecimal) row.get(pheCol), date, startDate, endDate);
      else val = Val.of((String) row.get(pheCol), date, startDate, endDate);
      if (val != null)
        rs.addValueWithRestriction(
                sbj,
                phe,
                search.getDateTimeRestriction(),
                val,
                search.getSourceUnit(),
                search.getModelUnit());
    }
    checkQuantifier(search, rs);
    
    return rs;
  }
  
  @Override
  public ResultSet execute(SubjectSearch search) {
    ResultSet rs = new ResultSet();
    
    String preparedQuery = SQLAdapterSettings.get().createSubjectPreparedQuery(search);
    
    var sqlQuery = SQLAdapterSettings.get().getSubjectSqlQuery(preparedQuery, handle, search);
    log.info("Execute SQL query: {}", sqlQuery);
    var sqlRS = sqlQuery.map(mapper);
    
    SubjectOutput out = search.getOutput();
    String sbjCol = out.getId();
    String bdCol = out.getBirthdate();
    String sexCol = out.getSex();
    Phenotype sex = search.getSex();
    Phenotype bd = search.getBirthdateDerived();
    Phenotype age = search.getAge();
    
    for (Map<String, Object> row : sqlRS) {
      String sbj = row.get(sbjCol).toString();
      if (bd == null && sex == null) {
        rs.addSubject(row.get(sbjCol).toString());
        continue;
      }
      if (bd != null) {
        OffsetDateTime bdSqlVal = (OffsetDateTime) row.get(bdCol);
        if (bdSqlVal != null) {
          Value val = Val.of(bdSqlVal.toLocalDate().atStartOfDay());
          if (search.getBirthdate() != null) rs.addValueWithRestriction(sbj, bd, val);
          else rs.addValue(sbj, bd, null, val);
          if (age != null) {
            Value ageVal = Val.of(DateUtil.birthdateToAge(bdSqlVal.toLocalDate().atStartOfDay()));
            rs.addValueWithRestriction(sbj, age, ageVal);
          }
        }
      }
      if (sex != null) {
        if (Phenotypes.hasBooleanType(sex)) {
          Boolean sexSqlVal = (Boolean) row.get(sexCol);
          if (sexSqlVal != null) rs.addValueWithRestriction(sbj, sex, Val.of(sexSqlVal));
        } else if (Phenotypes.hasNumberType(sex)) {
          BigDecimal sexSqlVal = (BigDecimal) row.get(sexCol);
          if (sexSqlVal != null) rs.addValueWithRestriction(sbj, sex, Val.of(sexSqlVal));
        } else {
          String sexSqlVal = (String) row.get(sexCol);
          if (sexSqlVal != null) rs.addValueWithRestriction(sbj, sex, Val.of(sexSqlVal));
        }
      }
    }
    
    return rs;
  }
  
  @Override
  public DataAdapterSettings getSettings() {
    return SQLAdapterSettings.get();
  }
  
  @Override
  public void close() {
    handle.close();
  }
  
  public static void printSize(java.sql.ResultSet rs) {
    if (rs == null) System.out.println("SIZE: NULL");
    else {
      try {
        rs.last();
        System.out.println("SIZE: " + rs.getRow());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
  
  public static void print(java.sql.ResultSet rs) {
    try {
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnsNumber = rsmd.getColumnCount();
      while (rs.next()) {
        for (int i = 1; i <= columnsNumber; i++) {
          if (i > 1) System.out.print(" | ");
          System.out.print(rs.getString(i));
        }
        System.out.println();
      }
      for (int i = 1; i <= columnsNumber; i++) {
        if (i > 1) System.out.print(" | ");
        System.out.print(rsmd.getColumnName(i));
      }
      System.out.println();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) throws URISyntaxException, SQLException {
    DataAdapterConfig conf = DataAdapterConfig.getInstance("test_files/Simple_SQL_Config.yaml");
    SQLAdapter sql = new SQLAdapter(conf);
    
    //    Phenotype phe = new Phenotype().dataType(DataType.NUMBER).itemType(ItemType.OBSERVATION);
    //    phe.setId("weight");
    //    phe.setEntityType(EntityType.SINGLE_PHENOTYPE);
    //    phe.addCodesItem(
    //        new Code().code("3141-9").codeSystem(new CodeSystem().uri(new
    // URI("http://loinc.org"))));
    //    SingleSearch search = new SingleSearch(null, new QueryCriterion().subject(phe), sql);
    //    System.out.println(sql.execute(search));
    
    Phenotype bd = new Phenotype().dataType(DataType.DATE_TIME);
    bd.setId("birthdate");
    bd.setEntityType(EntityType.SINGLE_PHENOTYPE);
    bd.addCodesItem(
            new Code().code("21112-8").codeSystem(new CodeSystem().uri(new URI("http://loinc.org"))));
    
    DateTimeRestriction bdR =
            new DateTimeRestriction().addValuesItem(LocalDateTime.of(2000, 1, 1, 0, 0));
    bdR.setType(DataType.DATE_TIME);
    bdR.setQuantifier(Quantifier.MIN);
    bdR.setCardinality(1);
    bdR.setMinOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO);
    
    Phenotype young = new Phenotype().dataType(DataType.DATE_TIME).restriction(bdR);
    young.setId("young");
    young.setEntityType(EntityType.SINGLE_RESTRICTION);
    young.setSuperPhenotype(bd);
    
    Phenotype age = new Phenotype().dataType(DataType.NUMBER);
    age.setId("age");
    age.setEntityType(EntityType.SINGLE_PHENOTYPE);
    age.addCodesItem(
            new Code().code("30525-0").codeSystem(new CodeSystem().uri(new URI("http://loinc.org"))));
    
    NumberRestriction ageR = new NumberRestriction().addValuesItem(BigDecimal.valueOf(20));
    ageR.setType(DataType.NUMBER);
    ageR.setQuantifier(Quantifier.MIN);
    ageR.setCardinality(1);
    ageR.setMaxOperator(RestrictionOperator.LESS_THAN_OR_EQUAL_TO);
    
    Phenotype youngAge = new Phenotype().dataType(DataType.NUMBER).restriction(ageR);
    youngAge.setId("youngAge");
    youngAge.setEntityType(EntityType.SINGLE_RESTRICTION);
    youngAge.setSuperPhenotype(age);
    
    Phenotype sex = new Phenotype().dataType(DataType.STRING);
    sex.setId("sex");
    sex.setEntityType(EntityType.SINGLE_PHENOTYPE);
    sex.addCodesItem(
            new Code().code("46098-0").codeSystem(new CodeSystem().uri(new URI("http://loinc.org"))));
    
    StringRestriction femaleR =
            new StringRestriction().addValuesItem("http://hl7.org/fhir/administrative-gender|female");
    femaleR.setType(DataType.STRING);
    femaleR.setQuantifier(Quantifier.MIN);
    femaleR.setCardinality(1);
    
    Phenotype female = new Phenotype().dataType(DataType.STRING).restriction(femaleR);
    female.setId("female");
    female.setEntityType(EntityType.SINGLE_RESTRICTION);
    female.setSuperPhenotype(sex);
    
    SubjectSearch sbjSearch = new SubjectSearch(null, female, null, youngAge, sql);
    ResultSet rs = sql.execute(sbjSearch);
    System.out.println(rs);
    System.out.println("SIZE: " + rs.size());
  }
}
