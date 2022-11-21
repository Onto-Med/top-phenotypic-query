package care.smith.top.top_phenotypic_query.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import care.smith.top.model.Code;
import care.smith.top.model.DataType;
import care.smith.top.model.Entity;
import care.smith.top.model.EntityType;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;

public class Phenotypes {

  public static boolean isSinglePhenotype(Phenotype p) {
    return p.getEntityType() == EntityType.SINGLE_PHENOTYPE;
  }

  public static boolean isSingleRestriction(Phenotype p) {
    return p.getEntityType() == EntityType.SINGLE_RESTRICTION;
  }

  public static boolean isCompositePhenotype(Phenotype p) {
    return p.getEntityType() == EntityType.COMPOSITE_PHENOTYPE;
  }

  public static boolean isCompositeRestriction(Phenotype p) {
    return p.getEntityType() == EntityType.COMPOSITE_RESTRICTION;
  }

  public static boolean isSingle(Phenotype p) {
    return isSinglePhenotype(p) || isSingleRestriction(p);
  }

  public static boolean isComposite(Phenotype p) {
    return isCompositePhenotype(p) || isCompositeRestriction(p);
  }

  public static boolean isPhenotype(Phenotype p) {
    return isSinglePhenotype(p) || isCompositePhenotype(p);
  }

  public static boolean isRestriction(Phenotype p) {
    return isSingleRestriction(p) || isCompositeRestriction(p);
  }

  public static boolean isCategory(Entity e) {
    return e.getEntityType() == EntityType.CATEGORY;
  }

  public static String getPhenotypeId(Phenotype p) {
    return (isRestriction(p)) ? p.getSuperPhenotype().getId() : p.getId();
  }

  public static String getCodeUri(Code code) {
    return code.getCodeSystem().getUri().toString() + "|" + code.getCode();
  }

  public static List<Code> getCodes(Phenotype p) {
    return (isRestriction(p)) ? p.getSuperPhenotype().getCodes() : p.getCodes();
  }

  public static Stream<String> getCodeUris(Phenotype p) {
    return getCodes(p).stream().map(c -> getCodeUri(c));
  }

  public static List<String> getOwnCodeUris(Phenotype p) {
    return p.getCodes().stream().map(c -> getCodeUri(c)).collect(Collectors.toList());
  }

  public static boolean hasExistentialQuantifier(Phenotype p) {
    Restriction r = p.getRestriction();
    if (r == null) return false;
    return r.getQuantifier() == Quantifier.MIN && r.getCardinality().intValue() == 1;
  }

  public static DataType getDataType(Phenotype p) {
    return (isRestriction(p)) ? p.getSuperPhenotype().getDataType() : p.getDataType();
  }

  public static boolean hasStringType(Phenotype p) {
    return getDataType(p) == DataType.STRING;
  }

  public static boolean hasNumberType(Phenotype p) {
    return getDataType(p) == DataType.NUMBER;
  }

  public static boolean hasBooleanType(Phenotype p) {
    return getDataType(p) == DataType.BOOLEAN;
  }

  public static boolean hasDateTimeType(Phenotype p) {
    return getDataType(p) == DataType.DATE_TIME;
  }
}
