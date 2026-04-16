package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.analysis.Analysis;
import care.smith.top.top_phenotypic_query.analysis.AnalysisReport;
import com.google.common.collect.Multimap;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
      String algTitle = algPhe.getTitles().getFirst().getText();
      for (List<String> pheCombi : conf.getPhenotypes().get(algId)) {
        String pheCombiId = String.join("-", pheCombi);
        if (pheCombi.stream().map(p -> metadata.get(p)).anyMatch(Objects::isNull))
          log.trace(
              String.format(
                  "Algorithm %s does not contain phenotype combination %s", algId, pheCombiId));
        else
          new TimeAnalysisCalc(
                  pheCombi,
                  pheCombiId,
                  getPheCombiTitle(pheCombi, metadata),
                  algId,
                  algTitle,
                  data,
                  getTimeIntervals(conf),
                  report)
              .calculate();
      }
    }

    return Optional.of(report);
  }

  private String getPheCombiTitle(List<String> pheCombi, Map<String, Phenotype> metadata) {
    return pheCombi.stream()
        .map(p -> metadata.get(p).getTitles().getFirst().getText())
        .collect(Collectors.joining("-"));
  }

  private TreeMap<BigDecimal, Integer> getTimeIntervals(TimeAnalysisSpec conf) {
    TreeMap<BigDecimal, Integer> timeIntervals = new TreeMap<>();
    for (Integer time : conf.getTimeIntervals()) timeIntervals.put(new BigDecimal(time), 0);
    return timeIntervals;
  }
}
