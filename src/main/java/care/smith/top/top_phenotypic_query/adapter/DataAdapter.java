package care.smith.top.top_phenotypic_query.adapter;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Restriction;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQueryBuilder;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;

public abstract class DataAdapter {

  protected DataAdapterConfig config;

  protected DataAdapter(DataAdapterConfig config) {
    this.config = config;
  }

  protected DataAdapter(String configFile) {
    this(DataAdapterConfig.getInstance(configFile));
  }

  public static DataAdapter getInstance(DataAdapterConfig config) throws InstantiationException {
    DataAdapter dataAdapter;
    try {
      Class<?> adapterClass = Class.forName(config.getAdapter());
      dataAdapter =
          (DataAdapter) adapterClass.getConstructor(DataAdapterConfig.class).newInstance(config);
    } catch (ClassNotFoundException
        | InvocationTargetException
        | IllegalAccessException
        | NoSuchMethodException
        | ClassCastException e) {
      e.printStackTrace();
      throw new InstantiationException("Could not instantiate adapter for provided configuration.");
    }
    return dataAdapter;
  }

  public static DataAdapter getInstance(String configFile) throws InstantiationException {
    return getInstance(DataAdapterConfig.getInstance(configFile));
  }

  public DataAdapterConfig getConfig() {
    return config;
  }

  // call terminology server
  // fetch id attributes, column/table names
  // build atomic queries using DataAdapterConfig
  // execute queries and normalize/return ResultSet
  public abstract ResultSet execute(SubjectSearch search);

  public abstract ResultSet execute(SingleSearch search);

  public abstract ResultSet executeAllSubjectsQuery();

  public abstract DataAdapterFormat getFormat();

  public abstract void close();

  public void addValueIntervalLimit(
      String operator, String value, PhenotypeQueryBuilder builder, Restriction restriction) {
    builder.valueIntervalLimit(operator, value);
  }

  public void addValueList(
      String valuesAsString, PhenotypeQueryBuilder builder, Restriction restriction) {
    builder.valueList(valuesAsString);
  }

  public void addDateIntervalLimit(String operator, String value, PhenotypeQueryBuilder builder) {
    builder.dateIntervalLimit(operator, value);
  }

  public Map<String, String> getPhenotypeMappings(
      Phenotype phenotype, DataAdapterConfig config, Map<String, Phenotype> phenotypes) {
    CodeMapping codeMap = config.getCodeMapping(phenotype, phenotypes);
    if (codeMap == null) return null;
    return codeMap.getPhenotypeMappings();
  }
}
