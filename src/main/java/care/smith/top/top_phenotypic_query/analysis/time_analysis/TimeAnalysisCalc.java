package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.analysis.AnalysisReport;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Values;
import com.google.common.collect.Multimap;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TimeAnalysisCalc {

  private List<String> pheCombi;

  private String algId;
  private String algTitle;
  private String pheId;
  private String pheTitle;

  private Map<String, Multimap<String, Value>> data;
  private TreeMap<BigDecimal, Integer> timeIntervals = new TreeMap<>();
  private List<AnalysisReport> report;

  private int countOver = 0;
  private int countNull = 0;

  public TimeAnalysisCalc(
      List<String> phenotypeCombi,
      Phenotype algorithm,
      Map<String, Phenotype> metadata,
      Map<String, Multimap<String, Value>> data,
      List<Integer> timeIntervals,
      List<AnalysisReport> report) {
    this.pheCombi = phenotypeCombi;
    this.algId = algorithm.getId();
    this.algTitle = algorithm.getTitles().getFirst().getText();
    this.pheId = String.join("|", pheCombi);
    this.pheTitle =
        pheCombi.stream()
            .map(p -> metadata.get(p).getTitles().getFirst().getText())
            .collect(Collectors.joining("|"));
    this.data = data;
    for (Integer time : timeIntervals) this.timeIntervals.put(new BigDecimal(time), 0);
    this.report = report;
  }

  protected void calculate() {
    System.out.println();
    System.out.println("Algorithm: " + algId);
    System.out.println();
    System.out.println("Parameters: " + pheId);

    for (String sbj : data.keySet()) {
      System.out.println();
      System.out.println("Subject: " + sbj);
      calculate(data.get(sbj));
    }
    report();
  }

  private void calculate(Multimap<String, Value> data) {
    List<List<Value>> records = new ArrayList<>();
    List<Integer> counts = new ArrayList<>();
    for (String pheId : pheCombi) {
      List<Value> pheValues = (List<Value>) data.get(pheId);
      records.add(pheValues);
      counts.add(pheValues.size() - 1);
    }

    List<int[]> combiIndices = new ArrayList<>();
    getCombinationIndices(
        records.size() - 1, counts.stream().mapToInt(i -> i).toArray(), combiIndices);

    BigDecimal minTime = null;
    for (int[] c : combiIndices) {
      System.out.println(Arrays.toString(c));
      PhenotypeCombination combi = new PhenotypeCombination();
      for (int i = 0; i < c.length; i++) {
        if (Values.hasNoDateTime(records.get(i).get(c[i]))) System.out.println("null");
        else System.out.println(DateUtil.format(records.get(i).get(c[i]).getStartDateTime()));
        combi.add(records.get(i).get(c[i]));
      }
      BigDecimal time = combi.getTimeInterval();
      if (minTime == null || time.compareTo(minTime) < 0) minTime = time;
      System.out.println(time);
      System.out.println();
    }

    checkTimeInterval(minTime);

    System.out.println("MinTime: " + minTime);
  }

  private void getCombinationIndices(int loop, int[] counts, List<int[]> res) {
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
      getCombinationIndices(loop - 1, newCounts, res);
    }
  }

  private void checkTimeInterval(BigDecimal time) {
    if (time == null) {
      countNull++;
      return;
    }

    for (BigDecimal i : timeIntervals.keySet()) {
      if (time.compareTo(i) < 0) {
        timeIntervals.put(i, timeIntervals.get(i) + 1);
        return;
      }
    }

    countOver++;
  }

  private void report() {
    for (BigDecimal time : timeIntervals.keySet())
      getReport(time.toString(), timeIntervals.get(time));
    getReport("greater equal", countOver);
    getReport("no timestamp", countNull);
  }

  private void getReport(String name, Object value) {
    AnalysisReport r =
        new AnalysisReport()
            .algorithmId(algId)
            .algorithmTitle(algTitle)
            .phenotypeId(pheId)
            .phenotypeTitle(pheTitle)
            .resultName(name)
            .resultValue(value);
    report.add(r);
  }
}
