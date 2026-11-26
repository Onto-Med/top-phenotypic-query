package care.smith.top.top_phenotypic_query.benchmarks;

import module java.base;

import care.smith.top.model.DataType;
import care.smith.top.model.EntityType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.GraphLayout;

/**
 * Prints the retained heap size of a result set. Override the size with {@code -Dsubjects=...} and
 * {@code -Dvalues=...}; run with {@code mvn -Dtest=ResultSetMemory test}.
 */
class ResultSetMemory {

  @Test
  void retainedSize() {
    System.setProperty("jol.magicFieldOffset", "true"); // JOL 0.17 predates records on JDK 27
    int subjects = Integer.getInteger("subjects", 10_000);
    int values = Integer.getInteger("values", 10);

    Phenotype height = phenotype("Height");
    Phenotype weight = phenotype("Weight");
    ResultSet rs = new ResultSet();
    LocalDateTime date = LocalDateTime.of(2022, 1, 1, 0, 0);

    for (int i = 0; i < subjects; i++)
      for (int j = 0; j < values; j++) {
        rs.addValue(String.valueOf(i), height, null, Val.of(1.8, date.plusDays(j)));
        rs.addValue(String.valueOf(i), weight, null, Val.of(80, date.plusDays(j)));
      }

    System.out.printf(
        "%d subjects x %d values: %,d bytes%n",
        subjects, values, GraphLayout.parseInstance(rs).totalSize());
  }

  private static Phenotype phenotype(String id) {
    return new Phenotype().id(id).dataType(DataType.NUMBER).entityType(EntityType.SINGLE_PHENOTYPE);
  }
}
