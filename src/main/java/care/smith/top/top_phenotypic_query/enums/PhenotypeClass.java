package care.smith.top.top_phenotypic_query.enums;

import care.smith.top.simple_onto_api.model.ClassDef;

public enum PhenotypeClass {
  USiP("Unrestricted Single Phenotype"),
  RSiP("Restricted Single Phenotype"),
  UCoP("Unrestricted Combined Phenotype"),
  RCoP("Restricted Combined Phenotype"),
  UDeP("Unrestricted Derived Phenotype"),
  RDeP("Restricted Derived Phenotype");

  private String type;

  private PhenotypeClass(String type) {
    this.type = type;
  }

  private String getType() {
    return type;
  }

  public static boolean isUSiP(ClassDef cls) {
    return USiP.getType().equals(cls.getType());
  }

  public static boolean isRSiP(ClassDef cls) {
    return RSiP.getType().equals(cls.getType());
  }

  public static boolean isUCoP(ClassDef cls) {
    return UCoP.getType().equals(cls.getType());
  }

  public static boolean isRCoP(ClassDef cls) {
    return RCoP.getType().equals(cls.getType());
  }

  public static boolean isUDeP(ClassDef cls) {
    return UDeP.getType().equals(cls.getType());
  }

  public static boolean isRDeP(ClassDef cls) {
    return RDeP.getType().equals(cls.getType());
  }
}
