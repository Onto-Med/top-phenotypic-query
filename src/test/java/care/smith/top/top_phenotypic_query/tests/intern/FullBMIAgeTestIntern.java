package care.smith.top.top_phenotypic_query.tests.intern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.simple_onto_api.calculator.Calculator;
import care.smith.top.simple_onto_api.calculator.expressions.ConstantExpression;
import care.smith.top.simple_onto_api.calculator.expressions.FunctionExpression;
import care.smith.top.simple_onto_api.calculator.expressions.MathExpression;
import care.smith.top.simple_onto_api.calculator.expressions.VariableExpression;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.adapter.sql.SQLAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import care.smith.top.top_phenotypic_query.tests.AbstractTest;
import care.smith.top.top_phenotypic_query.ucum.UCUM;

public class FullBMIAgeTestIntern extends AbstractTest {

  public static void main(String[] args) throws SQLException {
    //    print();
    //    System.exit(0);

    QueryCriterion cri1 =
        new QueryCriterion()
            .inclusion(true)
            .defaultAggregationFunctionId(defAgrFunc.getId())
            .subjectId(overWeight.getId())
            .dateTimeRestriction(getDTR(2000));
    QueryCriterion cri2 = new QueryCriterion().inclusion(true).subjectId(female.getId());
    QueryCriterion cri3 = new QueryCriterion().inclusion(true).subjectId(old.getId());
    Query query = new Query().addCriteriaItem(cri1).addCriteriaItem(cri2).addCriteriaItem(cri3);

    DataAdapterConfig config =
        DataAdapterConfig.getInstance("test_files/SQL_Adapter_Test_intern.yml");
    SQLAdapter adapter = new SQLAdapter(config);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();
    adapter.close();

    List<String> actualSbjIds = new ArrayList<>(rs.getSubjectIds());
    Collections.sort(
        actualSbjIds,
        new Comparator<String>() {
          @Override
          public int compare(String o1, String o2) {
            return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
          }
        });

    System.out.println("ACTUAL:");
    System.out.println(actualSbjIds);

    List<String> expectedSbjIds = getExpectedSubjectIds();
    System.out.println("EXPECTED:");
    System.out.println(expectedSbjIds);

    List<String> onlyActualSbjIds = new ArrayList<>(actualSbjIds);
    onlyActualSbjIds.removeAll(expectedSbjIds);
    System.out.println("ONLY ACTUAL:");
    System.out.println(onlyActualSbjIds);

    List<String> onlyExpectedSbjIds = new ArrayList<>(expectedSbjIds);
    onlyExpectedSbjIds.removeAll(actualSbjIds);
    System.out.println("ONLY EXPECTED:");
    System.out.println(onlyExpectedSbjIds);

    assertEquals(expectedSbjIds, actualSbjIds);
  }

  private static List<String> getExpectedSubjectIds() throws SQLException {
    DataAdapterConfig sqlConfig =
        DataAdapterConfig.getInstance("test_files/SQL_Adapter_Test_intern.yml");
    SQLAdapter sqlAdapter = new SQLAdapter(sqlConfig);
    java.sql.ResultSet rs =
        sqlAdapter.executeQuery(
            "SELECT s.subject_id, birth_date, sex, assessment_id, created_at, height, weight, DATE_PART('year', AGE(CURRENT_DATE, birth_date)) years, (weight/((height/100)^2)) bmi FROM subject s, (select distinct on (subject_id) subject_id, assessment_id, created_at, height, weight from assessment1 where height is not null and weight is not null and created_at >= '2000-01-01'::date and created_at < '2001-01-01'::date order by subject_id, created_at desc) a WHERE s.subject_id = a.subject_id AND sex = 'female' AND DATE_PART('year', AGE(CURRENT_DATE, birth_date)) > 18 AND (weight/((height/100)^2)) >= 27 AND (weight/((height/100)^2)) < 30 ORDER BY s.subject_id");
    //    SQLAdapter.print(rs);

    List<String> ids = new ArrayList<>();
    while (rs.next()) ids.add(rs.getString("subject_id"));

    return ids;
  }

  private static void print() throws SQLException {
    DataAdapterConfig sqlConfig =
        DataAdapterConfig.getInstance("test_files/SQL_Adapter_Test_intern.yml");
    SQLAdapter sqlAdapter = new SQLAdapter(sqlConfig);
    java.sql.ResultSet rs =
        sqlAdapter.executeQuery(
            "SELECT s.subject_id, birth_date, sex, assessment_id, created_at, height, weight FROM subject s, assessment1 a WHERE s.subject_id = a.subject_id AND s.subject_id = 3269 ORDER BY created_at");
    SQLAdapter.print(rs);
  }

  public static void testBMI() {
    Calculator c = new Calculator();
    MathExpression e =
        new FunctionExpression("divide")
            .arg(new VariableExpression("m"))
            .arg(
                new FunctionExpression("power")
                    .arg(new VariableExpression("l"))
                    .arg(new ConstantExpression(new DecimalValue(2))));

    c.setVariable("m", 76.6057689700064);
    c.setVariable("l", UCUM.convert(new BigDecimal("167.617921540792"), "cm", "m"));

    System.out.println(c.calculate(e).getValueDecimal());
  }
}
