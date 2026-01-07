package care.smith.top.top_phenotypic_query.tests.intern;

import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.tests.AbstractTest;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Entities.NoCodesException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Disabled
public class POLARTestIntern extends AbstractTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(POLARTestIntern.class);

  public static void main(String[] args)
      throws SQLException,
          IOException,
          InstantiationException,
          InterruptedException,
          NoCodesException,
          URISyntaxException {
    Entities entities =
        Entities.of(
            "http://top-prod.imise.uni-leipzig.de/api/polar/delir/entity",
            System.getenv("POLAR_USER"),
            System.getenv("POLAR_PASSWORD"));

    DataAdapter adapter = DataAdapter.getInstance("test_files/POLAR_SQL_Adapter_Test_intern.yml");

    QueryCriterion cri1 =
        (QueryCriterion)
            new QueryCriterion()
                .inclusion(true)
                .defaultAggregationFunctionId(defAgrFunc.getId())
                .subjectId(entities.getPhenotypeWithTitle("Extended algorithm").getId());
    PhenotypeQuery query = new PhenotypeQuery().addCriteriaItem(cri1);

    PhenotypeFinder pf = new PhenotypeFinder(query, entities, adapter);
    ResultSet rs = pf.execute();
    LOGGER.trace(rs.toString(entities));
    LOGGER.trace(String.valueOf(rs.size()));
    adapter.close();
  }
}
