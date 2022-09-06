package care.smith.top.top_phenotypic_query.adapter.simple_sql_adapter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import care.smith.top.backend.model.Code;
import care.smith.top.backend.model.EntityType;
import care.smith.top.backend.model.Phenotype;
import care.smith.top.backend.model.Quantifier;
import care.smith.top.backend.model.Restriction;
import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQueryBuilder;
import care.smith.top.top_phenotypic_query.adapter.mapping.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.mapping.DataAdapterMapping;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;

public class SimpleSQLAdapter extends DataAdapter {

  private Connection con;

  public SimpleSQLAdapter(DataAdapterConfig conf, DataAdapterMapping map) {
    super(conf, map);
    con = SQLUtil.connect(conf);
  }

  public SimpleSQLAdapter(String confFile, String mapFile) {
    super(confFile, mapFile);
    con = SQLUtil.connect(conf);
  }

  @Override
  public ResultSet execute(SubjectSearch search) {

    return null;
  }

  @Override
  public ResultSet execute(SingleSearch search) {
    Phenotype phe = search.getPhenotype();
    CodeMapping codeMap = map.getCodeMapping(getCodes(phe));
    Map<String, String> pheMap = codeMap.getPhenotypeMappings();

    PhenotypeQueryBuilder base =
        conf.getPhenotypeQuery(codeMap.getType()).getQueryBuilder(pheMap).baseQuery();

    if (phe.getEntityType() == EntityType.SINGLE_RESTRICTION) {
      Restriction r = phe.getRestriction();
      if (r.getQuantifier() != Quantifier.ALL) {
        if (RestrictionUtil.hasInterval(r)) {
          Map<RestrictionOperator, String> interval = RestrictionUtil.getInterval(r);
          //          for (RestrictionOperator op : interval.keySet())
          //        	  base.
        }
      }
    }

    return null;
  }

  private List<Code> getCodes(Phenotype p) {
    if (p.getEntityType() == EntityType.SINGLE_PHENOTYPE) return p.getCodes();
    return p.getSuperPhenotype().getCodes();
  }

  public java.sql.ResultSet execute(String query) {
    return SQLUtil.execute(con, query);
  }

  public void close() {
    SQLUtil.close(con);
  }

  public static void main(String[] args) {
    SimpleSQLAdapter a =
        new SimpleSQLAdapter(
            "test_files/Simple_SQL_Config.yaml", "test_files/Simple_SQL_Mapping.yaml");
    SQLUtil.print(a.execute("SELECT * FROM subject"));
  }

  @Override
  public ResultSet executeAllSubjectsQuery() { // TODO Auto-generated method stub
    return null;
  }
}
