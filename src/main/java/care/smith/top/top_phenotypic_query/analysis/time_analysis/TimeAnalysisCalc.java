package care.smith.top.top_phenotypic_query.analysis.time_analysis;

import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.analysis.AnalysisReport;
import care.smith.top.top_phenotypic_query.util.Values;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Calculates periods of a phenotype combination
public class TimeAnalysisCalc {

  private Logger log = LoggerFactory.getLogger(TimeAnalysisCalc.class);

  private List<String> pheCombi;
  private String pheId;
  private String pheTitle;
  private String algId;
  private String algTitle;
  private Map<String, Multimap<String, Value>> data;
  private TreeMap<BigDecimal, Integer> periods = new TreeMap<>();
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
      TreeMap<BigDecimal, Integer> periods,
      List<AnalysisReport> report) {
    this.pheCombi = pheCombi;
    this.pheId = pheCombiId;
    this.pheTitle = pheCombiTitle;
    this.algId = algId;
    this.algTitle = algTitle;
    this.data = data;
    this.periods = periods;
    this.report = report;
  }

  // Calculates periods of a phenotype combination
  protected void calculate() {
    log.debug(String.format("Analyse phenotype combi %s of algorithm %s", pheId, algId));
    for (String sbj : data.keySet()) {
      log.debug(String.format("Analyse data of subject %s", sbj));
      calculate(data.get(sbj));
    }
    report();
  }

  // Calculates the minimum period of a phenotype combination of a subject
  private void calculate(Multimap<String, Value> data) {
    List<List<Value>> sbjRecord = new ArrayList<>();
    for (String pheId : pheCombi) {
      if (pheId == null) continue;
      List<Value> pheValues = (List<Value>) data.get(pheId);
      if (pheValues == null || pheValues.isEmpty()) {
        log.debug(String.format("No values of phenotype %s", pheId));
        countNoValue++;
        return;
      }
      sbjRecord.add(pheValues);
    }

    BigDecimal pheCombiPeriod = null;
    for (List<Value> valCombi : Lists.cartesianProduct(sbjRecord)) {
      log.debug(String.format("Value combi times: %s", Values.toStringPeriod(valCombi)));
      BigDecimal valCombiPeriod = getPeriod(valCombi);
      if (valCombiPeriod != null) {
        log.debug(String.format("Value combi period (max): %s", valCombiPeriod));
        if (pheCombiPeriod == null || pheCombiPeriod.compareTo(valCombiPeriod) > 0)
          pheCombiPeriod = valCombiPeriod;
      }
    }
    log.debug(String.format("Phenotype combi period (min): %s", pheCombiPeriod));

    checkPeriod(pheCombiPeriod);
  }

  // Calculates the maximum period between values of a value combination
  private BigDecimal getPeriod(List<Value> valCombi) {
    if (!Values.hasDateTimeAllValues(valCombi)) return null;
    BigDecimal period = null;
    int size = valCombi.size();
    for (int i = 0; i < size - 1; i++) {
      for (int j = i + 1; j < size; j++) {
        BigDecimal newPeriod = Values.getPeriodInHours(valCombi.get(i), valCombi.get(j));
        if (period == null || period.compareTo(newPeriod) < 0) period = newPeriod;
      }
    }
    return period;
  }

  // Counts resulting periods
  private void checkPeriod(BigDecimal period) {
    if (period == null) {
      countNoTimestamp++;
      return;
    }

    for (BigDecimal i : periods.keySet()) {
      if (period.compareTo(i) < 0) {
        periods.put(i, periods.get(i) + 1);
        return;
      }
    }

    countGreaterEqual++;
  }

  private void report() {
    for (BigDecimal p : periods.keySet()) report(p.toString(), periods.get(p));
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
