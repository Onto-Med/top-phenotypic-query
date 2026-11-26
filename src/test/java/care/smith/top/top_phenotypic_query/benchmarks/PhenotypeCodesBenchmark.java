package care.smith.top.top_phenotypic_query.benchmarks;

import module java.base;

import care.smith.top.model.Code;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import org.openjdk.jmh.annotations.*;

/** Measures flattening and deduplicating a phenotype code tree. */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class PhenotypeCodesBenchmark {

  @Param({"10", "100"})
  int width;

  private Phenotype phenotype;

  @Setup
  public void setup() {
    List<Code> roots = new ArrayList<>();
    for (int i = 0; i < width; i++)
      roots.add(new Code().code("root-" + i).children(List.of(new Code().code("child-" + i))));
    phenotype = new Phenotype().codes(roots);
  }

  @Benchmark
  public List<Code> flatten() {
    return Phenotypes.getUnrestrictedPhenotypeCodes(phenotype);
  }
}
