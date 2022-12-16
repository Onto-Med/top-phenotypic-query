package care.smith.top.top_phenotypic_query.tests.intern;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.tests.AbstractTest;
import care.smith.top.top_phenotypic_query.util.Entities;

public class POLARTestIntern extends AbstractTest {

  public static void main(String[] args)
      throws SQLException, MalformedURLException, IOException, InstantiationException {
    Entities phens =
        Entities.read(
            "http://top-prod.imise.uni-leipzig.de/api/polar/delir/entity",
            System.getenv("POLAR_USER"),
            System.getenv("POLAR_PASSWORD"));

    DataAdapter adapter = DataAdapter.getInstance("test_files/POLAR_SQL_Adapter_Test_intern.yml");

    QueryCriterion cri1 =
        new QueryCriterion()
            .inclusion(true)
            .defaultAggregationFunctionId(defAgrFunc.getId())
            .subjectId(phens.getPhenotypeWithTitle("Extended algorithm").getId());
    Query query = new Query().addCriteriaItem(cri1);

    PhenotypeFinder pf = new PhenotypeFinder(query, phens, adapter);
    ResultSet rs = pf.execute();
    System.out.println(rs.toString(phens));
    System.out.println(rs.size());
    adapter.close();

    //    System.out.println(phens.getIdsAndTitles());
    //    System.out.println(phens.size());
  }
}
