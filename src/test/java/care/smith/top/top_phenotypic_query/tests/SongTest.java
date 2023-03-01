package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Category;
import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.song.functions.And;
import care.smith.top.top_phenotypic_query.song.functions.Not;
import care.smith.top.top_phenotypic_query.song.functions.Or;
import care.smith.top.top_phenotypic_query.song.functions.SubTree;
import care.smith.top.top_phenotypic_query.song.functions.lucene.Lucene;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Cat;

public class SongTest {

  Category a =
      new Cat("a")
          .title("a-de", "de")
          .title("a-en", "en")
          .synonym("a1-de", "de")
          .synonym("a1-en", "en")
          .synonym("a2-de", "de")
          .synonym("a2-en", "en")
          .get();

  Category b =
      new Cat("b")
          .title("b-de", "de")
          .title("b-en", "en")
          .synonym("b1-de", "de")
          .synonym("b1-en", "en")
          .synonym("b2-de", "de")
          .synonym("b2-en", "en")
          .get();

  Category e =
      new Cat("e")
          .title("e-de", "de")
          .title("e-en", "en")
          .synonym("e1-de", "de")
          .synonym("e1-en", "en")
          .synonym("e2-de", "de")
          .synonym("e2-en", "en")
          .get();

  Category d =
      new Cat("d")
          .title("d-de", "de")
          .title("d-en", "en")
          .synonym("d1-de", "de")
          .synonym("d1-en", "en")
          .synonym("d2-de", "de")
          .synonym("d2-en", "en")
          .subCategories(e)
          .get();

  Category c =
      new Cat("c")
          .title("c-de", "de")
          .title("c-en", "en")
          .synonym("c1-de", "de")
          .synonym("c1-en", "en")
          .synonym("c2-de", "de")
          .synonym("c2-en", "en")
          .subCategories(d)
          .get();

  Entities concepts = Entities.of(a, b, c, d, e);

  @Test
  public void test1() {
    Expression exp = And.of(Or.of(a, b), Not.of(c));
    String query =
        Expressions.getStringValue(Lucene.get().concepts(concepts).lang("de").generate(exp));
    assertEquals(
        "(((a-de OR a1-de OR a2-de) OR (b-de OR b1-de OR b2-de)) AND NOT (c-de OR c1-de OR c2-de))",
        query);
  }

  @Test
  public void test2() {
    Expression exp = And.of(Or.of(a, b), Not.of(SubTree.of(c)));
    String query =
        Expressions.getStringValue(Lucene.get().concepts(concepts).lang("de").generate(exp));
    assertEquals(
        "(((a-de OR a1-de OR a2-de) OR (b-de OR b1-de OR b2-de)) AND NOT (c-de OR c1-de OR c2-de OR d-de OR d1-de OR d2-de OR e-de OR e1-de OR e2-de))",
        query);
  }
}
