package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.DataType;

public class WideCSVHead {

  private String id;
  private String title;
  private DataType dataType;

  public WideCSVHead(String id, String title, DataType dataType) {
    this.id = id;
    this.title = title;
    this.dataType = dataType;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public boolean isBoolean() {
    return dataType == DataType.BOOLEAN;
  }
}
