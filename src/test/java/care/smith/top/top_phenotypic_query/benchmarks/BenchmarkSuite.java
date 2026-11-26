package care.smith.top.top_phenotypic_query.benchmarks;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/** Runs all JMH benchmarks with {@code mvn -Dtest=BenchmarkSuite test}. */
class BenchmarkSuite {

  @Test
  void run() throws RunnerException {
    new Runner(new OptionsBuilder().include(".*Benchmark").build()).run();
  }
}
