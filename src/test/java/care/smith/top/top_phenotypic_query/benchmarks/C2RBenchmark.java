package care.smith.top.top_phenotypic_query.benchmarks;

import module java.base;

import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.tests.AbstractTest;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import org.openjdk.jmh.annotations.*;

/** Measures C2R evaluation of the BMI expression. */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class C2RBenchmark extends AbstractTest {

  private C2R c2r;
  private SubjectPhenotypes values;

  @Setup
  public void setup() {
    values = new SubjectPhenotypes("1");
    for (int i = 0; i < 100; i++) {
      LocalDateTime date = LocalDateTime.of(2020, 1, 1, 0, 0).plusDays(i);
      values.addValue("Weight", null, Val.of(80 + i % 10, date));
      values.addValue("Height", null, Val.of(1.8, date));
    }
    c2r = new C2R().phenotypes(Entities.of(phenotypes)).values(values);
  }

  @Benchmark
  public Expression bmi() {
    values.remove("BMI"); // C2R caches the result in the values map
    return c2r.calculate(bmi);
  }
}
