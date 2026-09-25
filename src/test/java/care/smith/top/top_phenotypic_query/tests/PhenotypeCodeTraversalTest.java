package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.*;

import module java.base;

import care.smith.top.model.Code;
import care.smith.top.model.CodeSystem;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import org.junit.jupiter.api.Test;

class PhenotypeCodeTraversalTest {
  private Code code(String id) {
    return new Code().code(id).uri(URI.create("urn:test:" + id));
  }

  private List<Code> flatten(Code... roots) {
    return Phenotypes.getUnrestrictedPhenotypeCodes(new Phenotype().codes(List.of(roots)));
  }

  @Test
  void retainsDepthFirstOrderAndFirstEqualObject() {
    Code child = code("child");
    Code root = code("root").children(List.of(child));
    List<Code> actual = flatten(root, code("child"), code("other"));
    assertEquals(List.of(root, child, code("other")), actual);
    assertSame(child, actual.get(1));
  }

  @Test
  void doesNotMergeDifferentMetadataOrSubtrees() {
    Code first = code("same").name("first").children(List.of(code("a")));
    Code second = code("same").name("second").children(List.of(code("b")));
    assertEquals(List.of(first, code("a"), second, code("b")), flatten(first, second));
  }

  @Test
  void doesNotMergeDifferentSystemsWithSameUriAndCode() {
    Code first = code("same").codeSystem(new CodeSystem().uri(URI.create("urn:system:one")));
    Code second = code("same").codeSystem(new CodeSystem().uri(URI.create("urn:system:two")));
    assertEquals(List.of(first, second), flatten(first, second));
  }

  @Test
  void supportsMissingUriAndCode() {
    Code first = new Code().code("one");
    Code second = new Code().code("two");
    Code third = new Code().name("no identifiers");
    assertEquals(
        List.of(first, second, third), flatten(first, second, third, new Code().code("one")));
  }

  @Test
  void deduplicatesEqualTreesWithoutDroppingDescendants() {
    Code first =
        code("root").children(List.of(code("child").children(List.of(code("grandchild")))));
    Code second =
        code("root").children(List.of(code("child").children(List.of(code("grandchild")))));
    assertEquals(3, flatten(first, second).size());
  }

  @Test
  void doesNotCacheAcrossMutationsOrCalls() {
    Code root = code("root").children(new ArrayList<>());
    assertEquals(1, flatten(root).size());
    root.getChildren().add(code("child"));
    assertEquals(2, flatten(root).size());
    assertTrue(Phenotypes.getUnrestrictedPhenotypeCodes(new Phenotype()).isEmpty());
  }
}
