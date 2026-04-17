package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.analysis.AnalysisReport;
import care.smith.top.top_phenotypic_query.analysis.CombiIndices;
import com.google.common.collect.Multimap;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeAnalysisCalc {

  private Logger log = LoggerFactory.getLogger(TimeAnalysisCalc.class);

  private List<String> pheCombi;
  private String pheId;
  private String pheTitle;
  private String algId;
  private String algTitle;
  private Map<String, Multimap<String, Value>> data;
  private TreeMap<BigDecimal, Integer> timeIntervals = new TreeMap<>();
  private List<AnalysisReport> report;

  private int countGreaterEqual = 0;
  private int countNoValue = 0;
  private int countNoTimestamp = 0;

  public TimeAnalysisCalc(
      List<String> pheCombi,
      String pheCombiId,
      String pheCombiTitle,
      String algId,
      String algTitle,
      Map<String, Multimap<String, Value>> data,
      TreeMap<BigDecimal, Integer> timeIntervals,
      List<AnalysisReport> report) {
    this.pheCombi = pheCombi;
    this.pheId = pheCombiId;
    this.pheTitle = pheCombiTitle;
    this.algId = algId;
    this.algTitle = algTitle;
    this.data = data;
    this.timeIntervals = timeIntervals;
    this.report = report;
  }

  protected void calculate() {
    log.debug(String.format("Analyse phenotype combination %s of algorithm %s", pheId, algId));
    for (String sbj : data.keySet()) {
      log.debug(String.format("Analyse data of subject %s", sbj));
      calculate(data.get(sbj));
    }
    report();
  }

  private void calculate(Multimap<String, Value> data) {
    List<List<Value>> sbjRecord = new ArrayList<>();
    List<Integer> counts = new ArrayList<>();
    for (String pheId : pheCombi) {
      List<Value> pheValues = (List<Value>) data.get(pheId);
      if (pheValues == null || pheValues.isEmpty()) {
        countNoValue++;
        return;
      }
      sbjRecord.add(pheValues);
      counts.add(pheValues.size() - 1);
    }

    BigDecimal minCombiTimeInt = null;
    for (int[] combiIndices : CombiIndices.get(sbjRecord.size() - 1, counts)) {
      log.debug(String.format("Phenotype combination indices: %s", Arrays.toString(combiIndices)));
      PhenoCombiTime combiTime = new PhenoCombiTime(combiIndices, sbjRecord);
      BigDecimal combiTimeInt = combiTime.getTimeInterval();
      if (combiTimeInt != null) {
        log.debug(String.format("Phenotype combination time: %s : %s", combiTime, combiTimeInt));
        if (minCombiTimeInt == null || combiTimeInt.compareTo(minCombiTimeInt) < 0)
          minCombiTimeInt = combiTimeInt;
      }
    }
    log.debug(String.format("Minimal phenotype combination time: %s", minCombiTimeInt));

    checkTimeInterval(minCombiTimeInt);
  }

  private void checkTimeInterval(BigDecimal time) {
    if (time == null) {
      countNoTimestamp++;
      return;
    }

    for (BigDecimal i : timeIntervals.keySet()) {
      if (time.compareTo(i) < 0) {
        timeIntervals.put(i, timeIntervals.get(i) + 1);
        return;
      }
    }

    countGreaterEqual++;
  }

  private void report() {
    for (BigDecimal time : timeIntervals.keySet())
      report(time.toString(), timeIntervals.get(time));
    report("greater equal", countGreaterEqual);
    report("no value", countNoValue);
    report("no timestamp", countNoTimestamp);
  }

  private void report(String name, Object value) {
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
