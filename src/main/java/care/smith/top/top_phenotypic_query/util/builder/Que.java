package care.smith.top.top_phenotypic_query.util.builder;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.ProjectionEntry;
import care.smith.top.model.ProjectionEntry.TypeEnum;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.util.Entities.NoCodesException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Que {

  private DataAdapter adapter;
  private DataAdapterConfig config;
  private PhenotypeQuery query = new PhenotypeQuery();
  private Entity[] entities;

  private Logger log = LoggerFactory.getLogger(Que.class);

  public Que(String configFilePath, Entity... entities) throws InstantiationException {
    this.adapter = DataAdapter.getInstanceFromResource(configFilePath);
    this.config = adapter.getConfig();
    this.entities = entities;
  }

  public DataAdapter getAdapter() {
    return adapter;
  }

  public DataAdapterConfig getConfig() {
    return config;
  }

  public PhenotypeQuery getQuery() {
    return query;
  }

  public Que inc(Phenotype p) {
    return cri(true, p, null, null);
  }

  public Que inc(Phenotype p, DateTimeRestriction dtr) {
    return cri(true, p, dtr, null);
  }

  public Que inc(Phenotype p, String defAggFunc) {
    return cri(true, p, null, defAggFunc);
  }

  public Que inc(Phenotype p, DateTimeRestriction dtr, String defAggFunc) {
    return cri(true, p, dtr, defAggFunc);
  }

  public Que exc(Phenotype p) {
    return cri(false, p, null, null);
  }

  public Que exc(Phenotype p, DateTimeRestriction dtr) {
    return cri(false, p, dtr, null);
  }

  public Que exc(Phenotype p, String defAggFunc) {
    return cri(false, p, null, defAggFunc);
  }

  public Que exc(Phenotype p, DateTimeRestriction dtr, String defAggFunc) {
    return cri(false, p, dtr, defAggFunc);
  }

  private Que cri(boolean inc, Phenotype p, DateTimeRestriction dtr, String defAggFunc) {
    query.addCriteriaItem(
        (QueryCriterion)
            new QueryCriterion()
                .inclusion(inc)
                .subjectId(p.getId())
                .dateTimeRestriction(dtr)
                .defaultAggregationFunctionId(defAggFunc)
                .type(TypeEnum.QUERYCRITERION));
    return this;
  }

  public Que pro(Phenotype p) {
    return pro(p, null, null);
  }

  public Que pro(Phenotype p, DateTimeRestriction dtr) {
    return pro(p, dtr, null);
  }

  public Que pro(Phenotype p, String defAggFunc) {
    return pro(p, null, defAggFunc);
  }

  private Que pro(Phenotype p, DateTimeRestriction dtr, String defAggFunc) {
    query.addProjectionItem(
        new ProjectionEntry()
            .subjectId(p.getId())
            .dateTimeRestriction(dtr)
            .defaultAggregationFunctionId(defAggFunc)
            .type(TypeEnum.PROJECTIONENTRY));
    return this;
  }

  public PhenotypeFinder getFinder() {
    return new PhenotypeFinder(query, entities, adapter);
  }

  public ResultSet execute() {
    PhenotypeFinder pf = getFinder();
    ResultSet rs = null;
    try {
      rs = pf.execute();
    } catch (SQLException | NoCodesException e) {
      e.printStackTrace();
    } finally {
      adapter.close();
    }
    return rs;
  }

  private String readResource(String path) {
    try {
      return Files.readString(Path.of("src/test/resources/" + path));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Que executeSqlFromResources(String... files) {
    return executeSql(Stream.of(files).map(f -> readResource(f)).toArray(String[]::new));
  }

  public Que cleanDB() {
    return executeSql("DROP ALL OBJECTS");
  }

  public Que executeSql(String... statements) {
    try {
      Connection con =
          DriverManager.getConnection(
              config.getConnectionAttribute("url"),
              config.getConnectionAttribute("user"),
              config.getConnectionAttribute("password"));
      for (String sql : statements) {
        log.debug("execute sql statement:{}{}", System.lineSeparator(), sql);
        Statement stmt = con.createStatement();
        stmt.execute(sql);
        stmt.close();
      }
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return this;
  }
}
