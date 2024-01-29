package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.DataType;
import care.smith.top.model.EntityType;
import care.smith.top.model.ItemType;
import java.util.Objects;

public class CSVSubjectsHead implements Comparable<CSVSubjectsHead> {

  private String id;
  private String title;
  private EntityType entityType;
  private ItemType itemType;
  private DataType dataType;

  protected CSVSubjectsHead(
      String id, String title, EntityType entityType, ItemType itemType, DataType dataType) {
    this.id = id;
    this.title = title;
    this.entityType = entityType;
    this.itemType = itemType;
    this.dataType = dataType;
  }

  protected String getId() {
    return id;
  }

  protected String getTitle() {
    return title;
  }

  protected boolean isBoolean() {
    return dataType == DataType.BOOLEAN;
  }

  protected boolean hasDateColumn() {
    return entityType == EntityType.SINGLE_PHENOTYPE
        && itemType != ItemType.SUBJECT_AGE
        && itemType != ItemType.SUBJECT_BIRTH_DATE
        && itemType != ItemType.SUBJECT_SEX;
  }

  @Override
  public int compareTo(CSVSubjectsHead o) {
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
    CSVSubjectsHead other = (CSVSubjectsHead) obj;
    return Objects.equals(id, other.id);
  }

  @Override
  public String toString() {
    return "SubjectsCSVHead [id="
        + id
        + ", title="
        + title
        + ", entityType="
        + entityType
        + ", dataType="
        + dataType
        + "]";
  }
}
