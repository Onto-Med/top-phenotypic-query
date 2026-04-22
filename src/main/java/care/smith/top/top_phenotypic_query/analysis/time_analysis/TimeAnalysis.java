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
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;

/**
 * Calculates periods within which all values of the specified phenotype combinations fall and
 * aggregates/counts them according to the specified intervals.
 *
 * <p>The configuration file (.yml) must look like this:
 *
 * <p>periods: [ 12, 24, 36, 48, 72, 120 ]
 *
 * <p>phenotypes:
 *
 * <p>&nbsp;&nbsp;A1:
 *
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;- [ P1, P2 ]
 *
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;- [ P3, P4, P5 ]
 *
 * <p>&nbsp;&nbsp;A2:
 *
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;- [ P6, P7, P8 ]
 *
 * <p>...
 *
 * <p>A1, ..., An: algorithms (derived/composite phenotype classes)
 *
 * <p>P1, ..., Pn: their parameters (basic/single phenotype classes), whose temporal relationships
 * are to be investigated.
 *
 * <p>periods: time intervals for aggregating/counting the results (n+1 periods: one for results
 * less than the corresponding defined period value and one for results greater than or equal to the
 * last period value)
 *
 * <p>For each phenotype combination and each subject, the minimum period within which all values of
 * the combination fall is calculated and assigned to one of the defined intervals. The calculated
 * periods are then counted across all subjects and exported in a report (.csv).
 */
@Command(
    name = "time-analysis",
    description = "Calculate the time intervals between specified phenotypes.",
    mixinStandardHelpOptions = true)
public class TimeAnalysis extends Analysis {

  private Logger log = LoggerFactory.getLogger(TimeAnalysis.class);

  @Override
  protected Optional<List<AnalysisReport>> analyse(File queryResultFile) {
    Optional<TimeAnalysisSpec> config = null;
    try {
      config = loadConfiguration(TimeAnalysisSpec.class);
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }

    if (config.isEmpty()) {
      log.warn("No configuration was provided or the configuration is empty.");
      return Optional.empty();
    }

    List<AnalysisReport> report = new ArrayList<>();
    TimeAnalysisSpec conf = config.get();
    Map<String, Phenotype> metadata = loadMetadataToMap(queryResultFile);
    Map<String, Multimap<String, Value>> data = loadPhenotypeDataToMap(queryResultFile);
    int size = data.keySet().size();

    for (String algId : conf.getPhenotypes().keySet()) {
      Phenotype algPhe = metadata.get(algId);
      if (algPhe == null) continue;
      String algTitle = algPhe.getTitles().getFirst().getText();
      for (List<String> pheCombi : conf.getPhenotypes().get(algId)) {
        String pheCombiId = String.join("-", pheCombi);
        if (containsCombi(pheCombi, algId, metadata))
          new TimeAnalysisCalc(
                  pheCombi,
                  pheCombiId,
                  getPheCombiTitle(pheCombi, metadata),
                  algId,
                  algTitle,
                  data,
                  getPeriods(conf),
                  report,
                  size)
              .calculate();
      }
    }

    return Optional.of(report);
  }

  private boolean containsCombi(
      List<String> pheCombi, String algId, Map<String, Phenotype> metadata) {
    boolean contains = true;
    for (String p : pheCombi) {
      if (!metadata.containsKey(p)) {
        log.warn(String.format("Algorithm %s does not contain phenotype %s.", algId, p));
        contains = false;
      }
    }
    return contains;
  }

  private String getPheCombiTitle(List<String> pheCombi, Map<String, Phenotype> metadata) {
    return pheCombi.stream()
        .map(p -> metadata.get(p).getTitles().getFirst().getText())
        .collect(Collectors.joining("-"));
  }

  private TreeMap<BigDecimal, Integer> getPeriods(TimeAnalysisSpec conf) {
    TreeMap<BigDecimal, Integer> periods = new TreeMap<>();
    for (Integer time : conf.getPeriods()) periods.put(new BigDecimal(time), 0);
    return periods;
  }
}
