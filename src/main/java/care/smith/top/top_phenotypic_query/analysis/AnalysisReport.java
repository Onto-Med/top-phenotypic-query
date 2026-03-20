package care.smith.top.top_phenotypic_query.analysis;

import com.opencsv.bean.CsvBindByName;

public class AnalysisReport {

  @CsvBindByName private String model;

  @CsvBindByName private String algorithm;

  @CsvBindByName private Object result;

  public String getModel() {
    return model;
  }

  public AnalysisReport model(String model) {
    this.model = model;
    return this;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public AnalysisReport algorithm(String algorithm) {
    this.algorithm = algorithm;
    return this;
  }

  public Object getResult() {
    return result;
  }

  public AnalysisReport result(Object result) {
    this.result = result;
    return this;
  }
}
