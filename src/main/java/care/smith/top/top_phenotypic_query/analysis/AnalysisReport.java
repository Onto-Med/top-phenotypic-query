package care.smith.top.top_phenotypic_query.analysis;

import com.opencsv.bean.CsvBindByName;

public class AnalysisReport {

  @CsvBindByName private String model;
  @CsvBindByName private String algorithmId;
  @CsvBindByName private String algorithmTitle;
  @CsvBindByName private String phenotypeId;
  @CsvBindByName private String phenotypeTitle;
  @CsvBindByName private String resultName;
  @CsvBindByName private Object resultValue;

  public String getModel() {
    return model;
  }

  public AnalysisReport model(String model) {
    this.model = model;
    return this;
  }

  public String getAlgorithmId() {
    return algorithmId;
  }

  public AnalysisReport algorithmId(String algorithmId) {
    this.algorithmId = algorithmId;
    return this;
  }

  public String getAlgorithmTitle() {
    return algorithmTitle;
  }

  public AnalysisReport algorithmTitle(String algorithmTitle) {
    this.algorithmTitle = algorithmTitle;
    return this;
  }

  public String getPhenotypeId() {
    return phenotypeId;
  }

  public AnalysisReport phenotypeId(String phenotypeId) {
    this.phenotypeId = phenotypeId;
    return this;
  }

  public String getPhenotypeTitle() {
    return phenotypeTitle;
  }

  public AnalysisReport phenotypeTitle(String phenotypeTitle) {
    this.phenotypeTitle = phenotypeTitle;
    return this;
  }

  public String getResultName() {
    return resultName;
  }

  public AnalysisReport resultName(String resultName) {
    this.resultName = resultName;
    return this;
  }

  public Object getResultValue() {
    return resultValue;
  }

  public AnalysisReport resultValue(Object resultValue) {
    this.resultValue = resultValue;
    return this;
  }

  @Override
  public String toString() {
    return "AnalysisReport [model="
        + model
        + ", algorithmId="
        + algorithmId
        + ", algorithmTitle="
        + algorithmTitle
        + ", phenotypeId="
        + phenotypeId
        + ", phenotypeTitle="
        + phenotypeTitle
        + ", resultName="
        + resultName
        + ", resultValue="
        + resultValue
        + "]";
  }
}
