package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.analysis.Analysis;
import care.smith.top.top_phenotypic_query.analysis.AnalysisReport;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.ArrayList;
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
      e.printStackTrace();
      System.exit(0);
    }

    if (config.isEmpty()) {
      log.trace("No configuration was provided or the configuration is empty.");
      System.exit(0);
    }

    List<AnalysisReport> report = new ArrayList<>();
    TimeAnalysisSpec conf = config.get();
    Map<String, Phenotype> metadata = loadMetadataToMap(queryResultFile);
    Map<String, Multimap<String, Value>> data = loadPhenotypeDataToMap(queryResultFile);

    for (String algId : conf.getPhenotypes().keySet()) {
      Phenotype algPhe = metadata.get(algId);
      if (algPhe == null) continue;
      for (List<String> pheCombi : conf.getPhenotypes().get(algId))
        new TimeAnalysisCalc(pheCombi, algPhe, metadata, data, conf.getTimeIntervals(), report)
            .calculate();
    }

    return Optional.of(report);
  }
}
