package care.smith.top.top_phenotypic_query.util.builder;

import java.net.URI;

import care.smith.top.model.Code;
import care.smith.top.model.CodeSystem;
import care.smith.top.model.DataType;
import care.smith.top.model.EntityType;
import care.smith.top.model.Expression;
import care.smith.top.model.LocalisableText;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Restriction;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Phenotypes;

public class PheBuild {

  private Phenotype p;

  public PheBuild(String id, String codeSystemUri, String... codes) {
    p = (Phenotype) new Phenotype().id(id);
    codes(codeSystemUri, codes);
  }

  public PheBuild(String id) {
    p = (Phenotype) new Phenotype().id(id);
  }

  public PheBuild code(String codeSystemUri, String code) {
    CodeSystem s = new CodeSystem().uri(URI.create(codeSystemUri));
    Code c = new Code().code(code).codeSystem(s);
    p.addCodesItem(c);
    return this;
  }

  public PheBuild code(String codeUri) {
    String[] sysCode = codeUri.split("\\s*\\|\\s*");
    return code(sysCode[0], sysCode[1]);
  }

  public PheBuild codes(String codeSystemUri, String... codes) {
    for (String code : codes) code(codeSystemUri, code);
    return this;
  }

  public PheBuild codes(String... codeUris) {
    for (String codeUri : codeUris) code(codeUri);
    return this;
  }

  public PheBuild title(String txt) {
    p.addTitlesItem(new LocalisableText().text(txt));
    return this;
  }

  public PheBuild title(String txt, String lang) {
    p.addTitlesItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public PheBuild titleDe(String txt) {
    return title(txt, "de");
  }

  public PheBuild titleEn(String txt) {
    return title(txt, "en");
  }

  public PheBuild synonym(String txt) {
    p.addSynonymsItem(new LocalisableText().text(txt));
    return this;
  }

  public PheBuild synonym(String txt, String lang) {
    p.addSynonymsItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public PheBuild synonymDe(String txt) {
    return synonym(txt, "de");
  }

  public PheBuild synonymEn(String txt) {
    return synonym(txt, "en");
  }

  public PheBuild description(String txt) {
    p.addDescriptionsItem(new LocalisableText().text(txt));
    return this;
  }

  public PheBuild description(String txt, String lang) {
    p.addDescriptionsItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public PheBuild descriptionDe(String txt) {
    return description(txt, "de");
  }

  public PheBuild descriptionEn(String txt) {
    return description(txt, "en");
  }

  public PheBuild annotations(String txt) {
    Entities.addAnnotations(p, txt);
    return this;
  }

  public PheBuild dataType(String dt) {
    return dataType(DataType.fromValue(dt));
  }

  public PheBuild dataType(DataType dt) {
    p.dataType(dt).entityType(EntityType.SINGLE_PHENOTYPE);
    return this;
  }

  public PheBuild string() {
    return dataType(DataType.STRING);
  }

  public PheBuild number() {
    return dataType(DataType.NUMBER);
  }

  public PheBuild number(String unit) {
    p.unit(unit);
    return number();
  }

  public PheBuild dateTime() {
    return dataType(DataType.DATE_TIME);
  }

  public PheBuild bool() {
    return dataType(DataType.BOOLEAN);
  }

  public PheBuild restriction(Phenotype superPhenotype, Restriction r) {
    p.superPhenotype(superPhenotype).restriction(r).dataType(DataType.BOOLEAN);
    if (Phenotypes.isSinglePhenotype(superPhenotype)) p.entityType(EntityType.SINGLE_RESTRICTION);
    else p.entityType(EntityType.COMPOSITE_RESTRICTION);
    return this;
  }

  public PheBuild expression(Expression e) {
    p.expression(e).entityType(EntityType.COMPOSITE_PHENOTYPE);
    return this;
  }

  public Phenotype get() {
    return p;
  }
}
