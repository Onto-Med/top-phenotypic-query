package care.smith.top.top_phenotypic_query;

public class Operators {

  private String eq;
  private String gt;
  private String ge;
  private String lt;
  private String le;

  public String getEq() {
    return eq;
  }

  public void setEq(String eq) {
    this.eq = eq;
  }

  public String getGt() {
    return gt;
  }

  public void setGt(String gt) {
    this.gt = gt;
  }

  public String getGe() {
    return ge;
  }

  public void setGe(String ge) {
    this.ge = ge;
  }

  public String getLt() {
    return lt;
  }

  public void setLt(String lt) {
    this.lt = lt;
  }

  public String getLe() {
    return le;
  }

  public void setLe(String le) {
    this.le = le;
  }

  @Override
  public String toString() {
    return "Operators [eq=" + eq + ", gt=" + gt + ", ge=" + ge + ", lt=" + lt + ", le=" + le + "]";
  }
}
