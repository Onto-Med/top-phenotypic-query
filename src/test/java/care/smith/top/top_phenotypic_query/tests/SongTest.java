package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Category;
import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.song.functions.And;
import care.smith.top.top_phenotypic_query.song.functions.Dist;
import care.smith.top.top_phenotypic_query.song.functions.Not;
import care.smith.top.top_phenotypic_query.song.functions.Or;
import care.smith.top.top_phenotypic_query.song.functions.SubTree;
import care.smith.top.top_phenotypic_query.song.functions.XProd;
import care.smith.top.top_phenotypic_query.song.functions.lucene.Lucene;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Cat;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class SongTest {

  Category a =
      new Cat("a")
          .titleDe("a- de")
          .titleEn("a- en")
          .synonymDe("a1- de")
          .synonymEn("a1- en")
          .synonymDe("a2- de")
          .synonymEn("a2- en")
          .get();

  Category b =
      new Cat("b")
          .titleDe("b-de")
          .titleEn("b-en")
          .synonymDe("b1-de")
          .synonymEn("b1-en")
          .synonymDe("b2-de")
          .synonymEn("b2-en")
          .get();

  Category e =
      new Cat("e")
          .titleDe("e-de")
          .titleEn("e-en")
          .synonymDe("e1-de")
          .synonymEn("e1-en")
          .synonymDe("e2-de")
          .synonymEn("e2-en")
          .get();

  Category d =
      new Cat("d")
          .titleDe("d-de")
          .titleEn("d-en")
          .synonymDe("d1-de")
          .synonymEn("d1-en")
          .synonymDe("d2-de")
          .synonymEn("d2-en")
          .subCategories(e)
          .get();

  Category c =
      new Cat("c")
          .titleDe("c-de")
          .titleEn("c-en")
          .synonymDe("c1-de")
          .synonymEn("c1-en")
          .synonymDe("c2-de")
          .synonymEn("c2-en")
          .subCategories(d)
          .get();

  Category f =
      new Cat("f")
          .titleDe("f-de")
          .titleEn("f-en")
          .synonymDe("f1-de")
          .synonymEn("f1-en")
          .synonymDe("f2- de")
          .synonymEn("f2-en")
          .get();

  Entities concepts = Entities.of(a, b, c, d, e, f);

  @Test
  public void test1() {
    Expression exp = And.of(Or.of(a, b), Not.of(c, f));
    String query =
        Expressions.getStringValue(Lucene.get().concepts(concepts).lang("de").generate(exp));
    assertEquals(
        "(((\"a- de\" OR \"a1- de\" OR \"a2- de\") OR (b-de OR b1-de OR b2-de)) AND NOT c-de AND NOT c1-de AND NOT c2-de AND NOT f-de AND NOT f1-de AND NOT \"f2- de\")",
        query);
  }

  @Test
  public void test2() {
    Expression exp = And.of(Or.of(a, b), Not.of(SubTree.of(c), Exp.of(f)));
    String query =
        Expressions.getStringValue(Lucene.get().concepts(concepts).lang("de").generate(exp));
    assertEquals(
        "(((\"a- de\" OR \"a1- de\" OR \"a2- de\") OR (b-de OR b1-de OR b2-de)) AND NOT c-de AND NOT c1-de AND NOT c2-de AND NOT d-de AND NOT d1-de AND NOT d2-de AND NOT e-de AND NOT e1-de AND NOT e2-de AND NOT f-de AND NOT f1-de AND NOT \"f2- de\")",
        query);
  }

  @Test
  public void test3() {
    Expression exp = And.of(Or.of(Dist.of(a, 5), Exp.of(b)), Not.of(c));
    String query =
        Expressions.getStringValue(Lucene.get().concepts(concepts).lang("de").generate(exp));
    assertEquals(
        "(((\"a- de\"~5 OR \"a1- de\"~5 OR \"a2- de\"~5) OR (b-de OR b1-de OR b2-de)) AND NOT c-de AND NOT c1-de AND NOT c2-de)",
        query);
  }

  @Test
  public void test4() {
    Expression exp = And.of(XProd.of(a, b), Not.of(c));
    String query =
        Expressions.getStringValue(Lucene.get().concepts(concepts).lang("de").generate(exp));
    assertEquals(
        "((\"a- de b-de\" OR \"a- de b1-de\" OR \"a- de b2-de\" OR \"a1- de b-de\" OR \"a1- de b1-de\" OR \"a1- de b2-de\" OR \"a2- de b-de\" OR \"a2- de b1-de\" OR \"a2- de b2-de\") AND NOT c-de AND NOT c1-de AND NOT c2-de)",
        query);
  }

  @Test
  public void test5() {
    Expression exp = And.of(Dist.of(XProd.of(a, b), 2), Not.of(c));
    String query =
        Expressions.getStringValue(Lucene.get().concepts(concepts).lang("de").generate(exp));
    assertEquals(
        "((\"a- de b-de\"~2 OR \"a- de b1-de\"~2 OR \"a- de b2-de\"~2 OR \"a1- de b-de\"~2 OR \"a1- de b1-de\"~2 OR \"a1- de b2-de\"~2 OR \"a2- de b-de\"~2 OR \"a2- de b1-de\"~2 OR \"a2- de b2-de\"~2) AND NOT c-de AND NOT c1-de AND NOT c2-de)",
        query);
  }
}
