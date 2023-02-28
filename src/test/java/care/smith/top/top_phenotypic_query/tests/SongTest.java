package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.song.functions.And;
import care.smith.top.top_phenotypic_query.song.functions.Not;
import care.smith.top.top_phenotypic_query.song.functions.Or;
import care.smith.top.top_phenotypic_query.song.functions.lucene.Lucene;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Phe;

public class SongTest {

  Phenotype a =
      new Phe("a")
          .title("a-de", "de")
          .title("a-en", "en")
          .synonym("a1-de", "de")
          .synonym("a1-en", "en")
          .synonym("a2-de", "de")
          .synonym("a2-en", "en")
          .string()
          .get();

  Phenotype b =
      new Phe("b")
          .title("b-de", "de")
          .title("b-en", "en")
          .synonym("b1-de", "de")
          .synonym("b1-en", "en")
          .synonym("b2-de", "de")
          .synonym("b2-en", "en")
          .string()
          .get();

  Phenotype c =
      new Phe("c")
          .title("c-de", "de")
          .title("c-en", "en")
          .synonym("c1-de", "de")
          .synonym("c1-en", "en")
          .synonym("c2-de", "de")
          .synonym("c2-en", "en")
          .string()
          .get();

  Entities concepts = Entities.of(a, b, c);

  @Test
  public void test1() {
    Expression exp = And.of(Or.of(a, b), Not.of(c));
    String query =
        Expressions.getStringValue(Lucene.get().concepts(concepts).lang("de").generate(exp));
    assertEquals(
        "(((a-de OR a1-de OR a2-de) OR (b-de OR b1-de OR b2-de)) AND NOT (c-de OR c1-de OR c2-de))",
        query);
  }
}
