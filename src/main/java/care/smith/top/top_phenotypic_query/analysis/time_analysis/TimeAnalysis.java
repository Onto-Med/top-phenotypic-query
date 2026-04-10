package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.analysis.Analysis;
import care.smith.top.top_phenotypic_query.analysis.AnalysisReport;
import care.smith.top.top_phenotypic_query.analysis.PhenotypeRecord;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import picocli.CommandLine.Command;

@Command(
    name = "time-analysis",
    description = "Calculate the time intervals between specified phenotypes.",
    mixinStandardHelpOptions = true)
public class TimeAnalysis extends Analysis {

  @Override
  protected Optional<List<AnalysisReport>> analyse(File queryResultFile) {
    Optional<TimeAnalysisSpec> config = null;
    try {
      config = loadConfiguration(TimeAnalysisSpec.class);
    } catch (Exception e) {
      log.trace("No configuration was provided.");
      e.printStackTrace();
      System.exit(0);
    }

    Map<String, Phenotype> metadata = loadMetadataToMap(queryResultFile);
    System.out.println(metadata.keySet());

    Multimap<String, PhenotypeRecord> data = loadPhenotypeDataOfSubjects(queryResultFile);
    System.out.println(data);

    return Optional.empty();
  }
}
