package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.analysis.Analysis;
import care.smith.top.top_phenotypic_query.analysis.AnalysisReport;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import com.google.common.collect.Multimap;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    TimeAnalysisSpec conf = config.get();

    Map<String, Phenotype> metadata = loadMetadataToMap(queryResultFile);

    Map<String, Multimap<String, Value>> data = loadPhenotypeDataToMap(queryResultFile);

    for (String algId : conf.getPhenotypes().keySet()) {
      Phenotype algPhe = metadata.get(algId);
      if (algPhe == null) continue;
      System.out.println();
      System.out.println("Algorithm: " + algId + " :: " + algPhe.getTitles().getFirst().getText());
      List<List<String>> pheCombis = conf.getPhenotypes().get(algId);
      for (List<String> pheCombi : pheCombis) {
        System.out.println();
        System.out.println("Parameters: " + String.join("|", pheCombi));
        System.out.println("Parameters: " + getPhenotypeCombinationTitle(pheCombi, metadata));
        checkPhenotypeCombination(pheCombi, data);
      }
    }

    return Optional.empty();
  }

  private void checkPhenotypeCombination(
      List<String> pheCombi, Map<String, Multimap<String, Value>> data) {
    for (String sbj : data.keySet()) {
      System.out.println();
      System.out.println("Subject: " + sbj);
      checkPhenotypeCombination(pheCombi, data.get(sbj));
    }
  }

  private void checkPhenotypeCombination(List<String> pheCombi, Multimap<String, Value> data) {

    List<List<Value>> records = new ArrayList<>();
    List<Integer> counts = new ArrayList<>();
    for (String pheId : pheCombi) {
      List<Value> pheValues = (List<Value>) data.get(pheId);
      records.add(pheValues);
      counts.add(pheValues.size() - 1);
    }

    List<int[]> combinations = new ArrayList<>();
    getCombinations(records.size() - 1, counts.stream().mapToInt(i -> i).toArray(), combinations);

    BigDecimal min = null;
    for (int[] c : combinations) {
      System.out.println(Arrays.toString(c));
      PhenotypeCombination combi = new PhenotypeCombination();
      for (int i = 0; i < c.length; i++) {
        System.out.println(DateUtil.format(records.get(i).get(c[i]).getStartDateTime()));
        combi.add(records.get(i).get(c[i]));
      }
      BigDecimal time = combi.getTimeInterval();
      if (min == null || time.compareTo(min) < 0) min = time;
      System.out.println(time);
      System.out.println();
    }

    System.out.println("Min: " + min);
  }

  private String getPhenotypeCombinationTitle(
      List<String> pheCombi, Map<String, Phenotype> metadata) {
    return pheCombi.stream()
        .map(p -> metadata.get(p).getTitles().getFirst().getText())
        .collect(Collectors.joining("|"));
  }

  private void getCombinations(int loop, int[] counts, List<int[]> res) {
    if (loop == 0) {
      for (int i = counts[0]; i >= 0; i--) {
        int[] combi = Arrays.copyOf(counts, counts.length);
        combi[0] = i;
        res.add(combi);
      }
      return;
    }

    for (int i = counts[loop]; i >= 0; i--) {
      int[] newCounts = Arrays.copyOf(counts, counts.length);
      newCounts[loop] = i;
      getCombinations(loop - 1, newCounts, res);
    }
  }
}
