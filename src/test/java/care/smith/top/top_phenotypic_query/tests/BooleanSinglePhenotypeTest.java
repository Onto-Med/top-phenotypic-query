package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import care.smith.top.model.DataType;
import care.smith.top.model.EntityType;
import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;

public class BooleanSinglePhenotypeTest extends AbstractTest {

  private DataAdapter adapter;
  private Phenotype w =
      getPhenotype("Weight", "http://loinc.org", "3141-9").dataType(DataType.BOOLEAN);
  private Phenotype h =
      getPhenotype("Height", "http://loinc.org", "3137-7", "m").dataType(DataType.BOOLEAN);
  private Map<String, Phenotype> phenotypes = getPhenotypeMap(w, h);

  @BeforeEach
  void start() throws InstantiationException {
    URL configFile =
        BooleanSinglePhenotypeTest.class
            .getClassLoader()
            .getResource("config/SQL_Adapter_Test5.yml");
    assertNotNull(configFile);
    adapter = DataAdapter.getInstance(configFile.getPath());
  }

  @AfterEach
  void end() {
    adapter.close();
  }

  @Test
  public void test1() {
    QueryCriterion cri1 = new QueryCriterion().inclusion(false).subjectId(h.getId());
    QueryCriterion cri2 = new QueryCriterion().inclusion(true).subjectId(w.getId());
    Query query = new Query().addCriteriaItem(cri1).addCriteriaItem(cri2);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();

    assertEquals(Set.of("2", "4"), rs.getSubjectIds());
  }

  @Test
  public void test2() {
    QueryCriterion cri1 = new QueryCriterion().inclusion(true).subjectId(h.getId());
    QueryCriterion cri2 = new QueryCriterion().inclusion(false).subjectId(w.getId());
    Query query = new Query().addCriteriaItem(cri1).addCriteriaItem(cri2);

    PhenotypeFinder pf = new PhenotypeFinder(query, phenotypes, adapter);
    ResultSet rs = pf.execute();

    assertEquals(Set.of("1"), rs.getSubjectIds());
  }

  @Test
  public void test3() {
    Expression exp =
        new Expression()
            .functionId("and")
            .addArgumentsItem(new Expression().entityId("Weight"))
            .addArgumentsItem(
                new Expression()
                    .functionId("not")
                    .addArgumentsItem(new Expression().entityId("Height")));
    Phenotype composite =
        (Phenotype)
            new Phenotype().expression(exp).id("c").entityType(EntityType.COMPOSITE_PHENOTYPE);

    Map<String, Phenotype> phes = new HashMap<>(phenotypes);
    phes.put(composite.getId(), composite);

    QueryCriterion cri = new QueryCriterion().inclusion(true).subjectId(composite.getId());
    Query query = new Query().addCriteriaItem(cri);

    PhenotypeFinder pf = new PhenotypeFinder(query, phes, adapter);
    ResultSet rs = pf.execute();

    assertEquals(Set.of("2", "4"), rs.getSubjectIds());
  }

  @Test
  public void test4() {
    Expression exp =
        new Expression()
            .functionId("and")
            .addArgumentsItem(new Expression().entityId("Height"))
            .addArgumentsItem(
                new Expression()
                    .functionId("not")
                    .addArgumentsItem(new Expression().entityId("Weight")));
    Phenotype composite =
        (Phenotype)
            new Phenotype().expression(exp).id("c").entityType(EntityType.COMPOSITE_PHENOTYPE);

    Map<String, Phenotype> phes = new HashMap<>(phenotypes);
    phes.put(composite.getId(), composite);

    QueryCriterion cri = new QueryCriterion().inclusion(true).subjectId(composite.getId());
    Query query = new Query().addCriteriaItem(cri);

    PhenotypeFinder pf = new PhenotypeFinder(query, phes, adapter);
    ResultSet rs = pf.execute();

    assertEquals(Set.of("1"), rs.getSubjectIds());
  }

  @Test
  public void test5() {
    Expression exp =
        new Expression()
            .functionId("and")
            .addArgumentsItem(new Expression().entityId("Weight"))
            .addArgumentsItem(
                new Expression()
                    .functionId("not")
                    .addArgumentsItem(new Expression().entityId("Height")));
    Phenotype composite =
        (Phenotype)
            new Phenotype().expression(exp).id("c").entityType(EntityType.COMPOSITE_PHENOTYPE);

    Map<String, Phenotype> phes = new HashMap<>(phenotypes);
    phes.put(composite.getId(), composite);

    QueryCriterion cri = new QueryCriterion().inclusion(false).subjectId(composite.getId());
    Query query = new Query().addCriteriaItem(cri);

    PhenotypeFinder pf = new PhenotypeFinder(query, phes, adapter);
    ResultSet rs = pf.execute();

    assertEquals(Set.of("1", "3", "5"), rs.getSubjectIds());
  }

  @Test
  public void test6() {
    Expression exp =
        new Expression()
            .functionId("and")
            .addArgumentsItem(new Expression().entityId("Height"))
            .addArgumentsItem(
                new Expression()
                    .functionId("not")
                    .addArgumentsItem(new Expression().entityId("Weight")));
    Phenotype composite =
        (Phenotype)
            new Phenotype().expression(exp).id("c").entityType(EntityType.COMPOSITE_PHENOTYPE);

    Map<String, Phenotype> phes = new HashMap<>(phenotypes);
    phes.put(composite.getId(), composite);

    QueryCriterion cri = new QueryCriterion().inclusion(false).subjectId(composite.getId());
    Query query = new Query().addCriteriaItem(cri);

    PhenotypeFinder pf = new PhenotypeFinder(query, phes, adapter);
    ResultSet rs = pf.execute();

    assertEquals(Set.of("2", "3", "4", "5"), rs.getSubjectIds());
  }
}
