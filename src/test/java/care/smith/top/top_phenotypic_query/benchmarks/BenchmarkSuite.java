package care.smith.top.top_phenotypic_query.benchmarks;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatFactory;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

/** Runs all JMH benchmarks with {@code mvn -Dtest=BenchmarkSuite test}. */
class BenchmarkSuite {

  @Test
  void run() throws RunnerException {
    // Run silently and print only the result table; JMH's normal output includes a long preamble.
    Collection<RunResult> results =
        new Runner(
                new OptionsBuilder().include(".*Benchmark").verbosity(VerboseMode.SILENT).build())
            .run();
    ResultFormatFactory.getInstance(ResultFormatType.TEXT, System.out).writeOut(results);
  }
}
