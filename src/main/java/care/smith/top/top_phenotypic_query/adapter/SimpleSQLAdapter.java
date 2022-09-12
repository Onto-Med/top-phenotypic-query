package care.smith.top.top_phenotypic_query.adapter;

import java.util.List;
import java.util.Map;

import care.smith.top.backend.model.Code;
import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Quantifier;
import care.smith.top.backend.model.Restriction;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQuery;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQueryBuilder;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;

public class SimpleSQLAdapter extends DataAdapter {

  private SQLConnection con;

  public SimpleSQLAdapter(DataAdapterConfig conf) {
    super(conf);
    this.con =
        new SQLConnection(
            conf.getConnectionAttribute("url"),
            conf.getConnectionAttribute("user"),
            conf.getConnectionAttribute("password"));
  }

  public SimpleSQLAdapter(String confFile, String mapFile) {
    super(confFile, mapFile);
    this.con =
        new SQLConnection(
            conf.getConnectionAttribute("url"),
            conf.getConnectionAttribute("user"),
            conf.getConnectionAttribute("password"));
  }

  @Override
  public ResultSet executeAllSubjectsQuery() {
    //    java.sql.ResultSet rs =
    //        con.execute(conf.getSubjectQuery().getQueryBuilder().baseQuery().build());
    //    SQLConnection.print(rs);
    return null;
  }

  @Override
  public ResultSet execute(SubjectSearch search) {

    return null;
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    Phenotype phe = search.getPhenotype();
    CodeMapping codeMap = conf.getCodeMapping(getCodes(phe));
    Map<String, String> pheMap = codeMap.getPhenotypeMappings();
    DateTimeRestriction dtr = search.getCriterion().getDateTimeRestriction();
    PhenotypeQuery query = conf.getPhenotypeQuery(codeMap.getType());
    PhenotypeQueryBuilder builder = query.getQueryBuilder(pheMap).baseQuery();

    if (phe.getEntityType() == EntityType.SINGLE_RESTRICTION) {
      Restriction r = phe.getRestriction();
      if (r.getQuantifier() != Quantifier.ALL) {
        if (RestrictionUtil.hasInterval(r)) {
          Map<String, String> interval =
              RestrictionUtil.getInterval(codeMap.getSourceRestriction(r), SQLAdapterFormat.get());
          for (String key : interval.keySet()) builder.valueIntervalLimit(key, interval.get(key));
        } else if (RestrictionUtil.hasValues(r))
          builder.valueList(
              RestrictionUtil.getValuesAsString(
                  codeMap.getSourceRestriction(r), SQLAdapterFormat.get()));
      }
    }

    if (dtr != null) {
      if (RestrictionUtil.hasInterval(dtr)) {
        Map<String, String> interval = RestrictionUtil.getInterval(dtr, SQLAdapterFormat.get());
        for (String key : interval.keySet()) builder.dateIntervalLimit(key, interval.get(key));
      }
    }

    return con.execute(builder.build(), phe, query.getOutput().mapping(pheMap), dtr);
  }

  private List<Code> getCodes(Phenotype p) {
    if (p.getEntityType() == EntityType.SINGLE_PHENOTYPE) return p.getCodes();
    return p.getSuperPhenotype().getCodes();
  }

  public static void main(String[] args) {
    SimpleSQLAdapter a =
        new SimpleSQLAdapter(
            "test_files/Simple_SQL_Config.yaml", "test_files/Simple_SQL_Mapping.yaml");
    a.executeAllSubjectsQuery();
  }
}
