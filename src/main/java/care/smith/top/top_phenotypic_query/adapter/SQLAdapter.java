package care.smith.top.top_phenotypic_query.adapter;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import care.smith.top.backend.model.Code;
import care.smith.top.backend.model.CodeSystem;
import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.ItemType;
import care.smith.top.backend.model.NumberRestriction;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Quantifier;
import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.backend.model.StringRestriction;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.PhenotypeUtil;

public class SQLAdapter extends DataAdapter {

  private Connection con;

  public SQLAdapter(DataAdapterConfig config) {
    super(config);
    initConnection();
  }

  public SQLAdapter(String configFilePath) {
    super(configFilePath);
    initConnection();
  }

  private void initConnection() {
    try {
      this.con =
          DriverManager.getConnection(
              config.getConnectionAttribute("url"),
              config.getConnectionAttribute("user"),
              config.getConnectionAttribute("password"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    ResultSet rs = new ResultSet();
    try {
      java.sql.ResultSet sqlRS = con.createStatement().executeQuery(search.getQueryString());
      PhenotypeOutput out = search.getOutput();
      String sbjCol = out.getSubject();
      String pheCol = out.getPhenotype();
      String dateCol = out.getDate();
      Phenotype phe = search.getPhenotype();
      DataType datatype = phe.getDataType();

      while (sqlRS.next()) {
        String sbj = sqlRS.getString(sbjCol);
        LocalDateTime date = sqlRS.getTimestamp(dateCol).toLocalDateTime();
        Value val = null;
        if (datatype == DataType.BOOLEAN) val = new BooleanValue(sqlRS.getBoolean(pheCol), date);
        else if (datatype == DataType.DATE_TIME)
          val = new DateTimeValue(sqlRS.getTimestamp(pheCol).toLocalDateTime(), date);
        else if (datatype == DataType.NUMBER)
          val = new DecimalValue(sqlRS.getBigDecimal(pheCol), date);
        else val = new StringValue(sqlRS.getString(pheCol), date);
        if (val != null) addValue(rs, sbj, phe, search.getDateTimeRestriction(), val);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  @Override
  public ResultSet execute(SubjectSearch search) {
    ResultSet rs = new ResultSet();
    try {
      java.sql.ResultSet sqlRS = con.createStatement().executeQuery(search.getQueryString());
      SubjectOutput out = search.getOutput();
      String sbjCol = out.getId();
      String bdCol = out.getBirthdate();
      String sexCol = out.getSex();
      Phenotype sex = search.getSex();
      Phenotype bd = search.getBirthdate();
      Phenotype age = search.getAge();

      while (sqlRS.next()) {
        String sbj = sqlRS.getString(sbjCol);
        if (bd != null) {
          Timestamp bdSqlVal = sqlRS.getTimestamp(bdCol);
          if (bdSqlVal != null) {
            addValue(rs, sbj, bd, null, new DateTimeValue(bdSqlVal.toLocalDateTime()));
            if (age != null) {
              Value ageVal =
                  new DecimalValue(SubjectSearch.birthdateToAge(bdSqlVal.toLocalDateTime()));
              addValue(rs, sbj, age, null, ageVal);
            }
          }
        }
        if (sex != null) {
          if (sex.getDataType() == DataType.BOOLEAN) {
            Boolean sexSqlVal = sqlRS.getBoolean(sexCol);
            if (sexSqlVal != null) addValue(rs, sbj, sex, null, new BooleanValue(sexSqlVal));
          } else if (sex.getDataType() == DataType.NUMBER) {
            BigDecimal sexSqlVal = sqlRS.getBigDecimal(sexCol);
            if (sexSqlVal != null) addValue(rs, sbj, sex, null, new DecimalValue(sexSqlVal));
          } else {
            String sexSqlVal = sqlRS.getString(sexCol);
            if (sexSqlVal != null) addValue(rs, sbj, sex, null, new StringValue(sexSqlVal));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  private void addValue(
      ResultSet rs, String sbj, Phenotype phe, DateTimeRestriction dtr, Value val) {
    rs.addValue(sbj, PhenotypeUtil.getPhenotypeId(phe), dtr, val);
    if (PhenotypeUtil.hasExistentialQuantifier(phe))
      rs.addValue(sbj, phe.getId(), dtr, new BooleanValue(true));
  }

  @Override
  public ResultSet executeAllSubjectsQuery() {
    ResultSet rs = new ResultSet();
    try {
      java.sql.ResultSet sqlRS =
          con.createStatement().executeQuery(SubjectSearch.getBaseQuery(config));
      String sbjCol = SubjectSearch.getIdColumn(config);
      while (sqlRS.next()) rs.addSubject(sqlRS.getString(sbjCol));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  @Override
  public DataAdapterFormat getFormat() {
    return SQLAdapterFormat.get();
  }

  @Override
  public void close() {
    try {
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
        System.out.println("");
      }
      for (int i = 1; i <= columnsNumber; i++) {
        if (i > 1) System.out.print(" | ");
        System.out.print(rsmd.getColumnName(i));
      }
      System.out.println("");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws URISyntaxException {
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

    Phenotype bd = new Phenotype().dataType(DataType.DATE_TIME).itemType(ItemType.OBSERVATION);
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

    Phenotype age = new Phenotype().dataType(DataType.NUMBER).itemType(ItemType.OBSERVATION);
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

    Phenotype sex = new Phenotype().dataType(DataType.STRING).itemType(ItemType.OBSERVATION);
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
