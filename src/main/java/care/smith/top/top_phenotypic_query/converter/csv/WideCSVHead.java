package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.DataType;
import java.util.Objects;

public class WideCSVHead implements Comparable<WideCSVHead> {

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

  @Override
  public int compareTo(WideCSVHead o) {
    return getTitle().compareTo(o.getTitle());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    WideCSVHead other = (WideCSVHead) obj;
    return Objects.equals(id, other.id);
  }

  @Override
  public String toString() {
    return "WideCSVHead [id=" + id + ", title=" + title + ", dataType=" + dataType + "]";
  }
}
