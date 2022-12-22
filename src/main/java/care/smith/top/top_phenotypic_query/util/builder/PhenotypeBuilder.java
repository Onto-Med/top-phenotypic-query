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

public class PhenotypeBuilder {

  private Phenotype p;

  public PhenotypeBuilder(String id, String codeSystemUri, String... codes) {
    p = (Phenotype) new Phenotype().id(id);
    codes(codeSystemUri, codes);
  }

  public PhenotypeBuilder(String id, String... codeUris) {
    p = (Phenotype) new Phenotype().id(id);
    codes(codeUris);
  }

  public PhenotypeBuilder(String id) {
    p = (Phenotype) new Phenotype().id(id);
  }

  public PhenotypeBuilder code(String codeSystemUri, String code) {
    CodeSystem s = new CodeSystem().uri(URI.create(codeSystemUri));
    Code c = new Code().code(code).codeSystem(s);
    p.addCodesItem(c);
    return this;
  }

  public PhenotypeBuilder code(String codeUri) {
    String[] sysCode = codeUri.split("\\s*\\|\\s*");
    return code(sysCode[0], sysCode[1]);
  }

  public PhenotypeBuilder codes(String codeSystemUri, String... codes) {
    for (String code : codes) code(codeSystemUri, code);
    return this;
  }

  public PhenotypeBuilder codes(String... codeUris) {
    for (String codeUri : codeUris) code(codeUri);
    return this;
  }

  public PhenotypeBuilder title(String txt) {
    p.addTitlesItem(new LocalisableText().text(txt));
    return this;
  }

  public PhenotypeBuilder title(String txt, String lang) {
    p.addTitlesItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public PhenotypeBuilder synonym(String txt) {
    p.addSynonymsItem(new LocalisableText().text(txt));
    return this;
  }

  public PhenotypeBuilder synonym(String txt, String lang) {
    p.addSynonymsItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public PhenotypeBuilder description(String txt) {
    p.addDescriptionsItem(new LocalisableText().text(txt));
    return this;
  }

  public PhenotypeBuilder description(String txt, String lang) {
    p.addDescriptionsItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public PhenotypeBuilder annotations(String txt) {
    Entities.addAnnotations(p, txt);
    return this;
  }

  public PhenotypeBuilder dataType(String dt) {
    return dataType(DataType.fromValue(dt));
  }

  public PhenotypeBuilder dataType(DataType dt) {
    p.dataType(dt).entityType(EntityType.SINGLE_PHENOTYPE);
    return this;
  }

  public PhenotypeBuilder string() {
    return dataType(DataType.STRING);
  }

  public PhenotypeBuilder number() {
    return dataType(DataType.NUMBER);
  }

  public PhenotypeBuilder number(String unit) {
    p.unit(unit);
    return number();
  }

  public PhenotypeBuilder dateTime() {
    return dataType(DataType.DATE_TIME);
  }

  public PhenotypeBuilder bool() {
    return dataType(DataType.BOOLEAN);
  }

  public PhenotypeBuilder restriction(Phenotype superPhenotype, Restriction r) {
    p.superPhenotype(superPhenotype).restriction(r).dataType(DataType.BOOLEAN);
    if (Phenotypes.isSinglePhenotype(superPhenotype)) p.entityType(EntityType.SINGLE_RESTRICTION);
    else p.entityType(EntityType.COMPOSITE_RESTRICTION);
    return this;
  }

  public PhenotypeBuilder expression(Expression e) {
    p.expression(e).entityType(EntityType.COMPOSITE_PHENOTYPE);
    return this;
  }

  public Phenotype get() {
    return p;
  }
}
