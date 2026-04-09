package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.top_phenotypic_query.analysis.Analysis;
import care.smith.top.top_phenotypic_query.analysis.AnalysisReport;
import java.io.File;
import java.util.List;
import java.util.Optional;
import picocli.CommandLine.Command;

@Command(
    name = "time-analysis",
    description = "Calculate the time intervals between specified phenotypes.",
    mixinStandardHelpOptions = true)
public class TimeAnalysis extends Analysis {

  @Override
  protected Optional<List<AnalysisReport>> analyse(File queryResultFile) {
    try {
      Optional<TimeAnalysisSpec> config = loadConfiguration(TimeAnalysisSpec.class);

      if (config.isPresent()) System.out.println(config.get());
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }

    return Optional.empty();
  }
}
