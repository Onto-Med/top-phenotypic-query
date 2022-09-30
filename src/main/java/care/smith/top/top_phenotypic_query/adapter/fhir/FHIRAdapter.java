package care.smith.top.top_phenotypic_query.adapter.fhir;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.hl7.fhir.r4.model.Resource;

import care.smith.top.backend.model.DataType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.simple_onto_api.model.property.data.value.BooleanValue;
import care.smith.top.simple_onto_api.model.property.data.value.DateTimeValue;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.simple_onto_api.model.property.data.value.StringValue;
import care.smith.top.simple_onto_api.model.property.data.value.Value;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterFormat;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeOutput;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectOutput;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public class FHIRAdapter extends DataAdapter {

  private FHIRClient client;

  public FHIRAdapter(DataAdapterConfig config) {
    super(config);
    this.client = new FHIRClient(config);
  }

  public FHIRAdapter(String configFilePath) {
    super(configFilePath);
    this.client = new FHIRClient(config);
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.executeQuery(search.getQueryString()); 
   PhenotypeOutput out = search.getOutput();
      String sbjCol = out.getSubject();
      String pheCol = out.getPhenotype();
      String dateCol = out.getDate();
      Phenotype phe = search.getPhenotype();
      DataType datatype = phe.getDataType();

      for (Resource res : resources) {
        String sbj = sqlRS.getString(sbjCol);
        LocalDateTime date = sqlRS.getTimestamp(dateCol).toLocalDateTime();
        Value val = null;
        if (datatype == DataType.BOOLEAN) val = new BooleanValue(sqlRS.getBoolean(pheCol), date);
        else if (datatype == DataType.DATE_TIME)
          val = new DateTimeValue(sqlRS.getTimestamp(pheCol).toLocalDateTime(), date);
        else if (datatype == DataType.NUMBER)
          val = new DecimalValue(sqlRS.getBigDecimal(pheCol), date);
        else val = new StringValue(sqlRS.getString(pheCol), date);
        if (val != null)
          rs.addValueWithRestriction(
              sbj,
              phe,
              search.getDateTimeRestriction(),
              val,
              search.getSourceUnit(),
              search.getModelUnit());
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
      Phenotype bd = search.getBirthdateDerived();
      Phenotype age = search.getAge();

      while (sqlRS.next()) {
        String sbj = sqlRS.getString(sbjCol);
        if (bd != null) {
          Timestamp bdSqlVal = sqlRS.getTimestamp(bdCol);
          if (bdSqlVal != null) {
            DateTimeValue val = new DateTimeValue(bdSqlVal.toLocalDateTime());
            if (search.getBirthdate() != null) rs.addValueWithRestriction(sbj, bd, null, val);
            else rs.addValue(sbj, bd, null, val);
            if (age != null) {
              Value ageVal =
                  new DecimalValue(SubjectSearch.birthdateToAge(bdSqlVal.toLocalDateTime()));
              rs.addValueWithRestriction(sbj, age, null, ageVal);
            }
          }
        }
        if (sex != null) {
          if (sex.getDataType() == DataType.BOOLEAN) {
            Boolean sexSqlVal = sqlRS.getBoolean(sexCol);
            if (sexSqlVal != null)
              rs.addValueWithRestriction(sbj, sex, null, new BooleanValue(sexSqlVal));
          } else if (sex.getDataType() == DataType.NUMBER) {
            BigDecimal sexSqlVal = sqlRS.getBigDecimal(sexCol);
            if (sexSqlVal != null)
              rs.addValueWithRestriction(sbj, sex, null, new DecimalValue(sexSqlVal));
          } else {
            String sexSqlVal = sqlRS.getString(sexCol);
            if (sexSqlVal != null)
              rs.addValueWithRestriction(sbj, sex, null, new StringValue(sexSqlVal));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return rs;
  }

  @Override
  public ResultSet executeAllSubjectsQuery() {
    ResultSet rs = new ResultSet();
    List<Resource> resources = client.executeQuery(SubjectSearch.getBaseQuery(config));
    for (Resource res : resources) rs.addSubject(FHIRUtil.getId(res));
    return rs;
  }

  @Override
  public DataAdapterFormat getFormat() {
    return FHIRAdapterFormat.get();
  }

  @Override
  public void close() {}
}
