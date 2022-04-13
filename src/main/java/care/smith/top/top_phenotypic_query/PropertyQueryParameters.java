package care.smith.top.top_phenotypic_query;

public class PropertyQueryParameters {

  private String code;
  private String valueQuantity;
  private String valueConcept;
  private String date;
  private String sortInc;
  private String sortDec;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getValueQuantity() {
    return valueQuantity;
  }

  public void setValueQuantity(String valueQuantity) {
    this.valueQuantity = valueQuantity;
  }

  public String getValueConcept() {
    return valueConcept;
  }

  public void setValueConcept(String valueConcept) {
    this.valueConcept = valueConcept;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getSortInc() {
    return sortInc;
  }

  public void setSortInc(String sortInc) {
    this.sortInc = sortInc;
  }

  public String getSortDec() {
    return sortDec;
  }

  public void setSortDec(String sortDec) {
    this.sortDec = sortDec;
  }

  @Override
  public String toString() {
    return "PropertyQueryParameters [code="
        + code
        + ", valueQuantity="
        + valueQuantity
        + ", valueConcept="
        + valueConcept
        + ", date="
        + date
        + ", sortInc="
        + sortInc
        + ", sortDec="
        + sortDec
        + "]";
  }
}
