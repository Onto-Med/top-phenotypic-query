package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.top_phenotypic_query.util.ExpressionUtil;

public class ExpressionTest extends AbstractTest {

  @Test
  public void test() throws URISyntaxException {
    Phenotype age = getPhenotype("Age", null, null);
    Phenotype young = getRestriction("Young", age, 18, 34, Quantifier.EXACT, 5);
    assertEquals(young.getExpression(), ExpressionUtil.restrictionToExpression(young));
  }
}
