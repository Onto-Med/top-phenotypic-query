package care.smith.top.top_phenotypic_query.util.builder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public PhenotypeBuilder(String id) {
    p = (Phenotype) new Phenotype().id(id);
  }

  public PhenotypeBuilder(String id, String codeSystem, String... codes) {
    CodeSystem sys = new CodeSystem().uri(URI.create(codeSystem));
    List<Code> codeList =
        Stream.of(codes).map(c -> new Code().code(c).codeSystem(sys)).collect(Collectors.toList());
    p = (Phenotype) new Phenotype().id(id).codes(codeList);
  }

  public PhenotypeBuilder(String id, String... codeUris) {
    p = (Phenotype) new Phenotype().id(id);
    for (String cu : codeUris) {
      String[] sysCode = cu.split("\\s*\\|\\s*");
      CodeSystem sys = new CodeSystem().uri(URI.create(sysCode[0]));
      Code code = new Code().code(sysCode[1]).codeSystem(sys);
      p.addCodesItem(code);
    }
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

  public PhenotypeBuilder string() {
    p.dataType(DataType.STRING).entityType(EntityType.SINGLE_PHENOTYPE);
    return this;
  }

  public PhenotypeBuilder number() {
    p.dataType(DataType.NUMBER).entityType(EntityType.SINGLE_PHENOTYPE);
    return this;
  }

  public PhenotypeBuilder number(String unit) {
    p.unit(unit);
    return number();
  }

  public PhenotypeBuilder dateTime() {
    p.dataType(DataType.DATE_TIME).entityType(EntityType.SINGLE_PHENOTYPE);
    return this;
  }

  public PhenotypeBuilder bool() {
    p.dataType(DataType.BOOLEAN).entityType(EntityType.SINGLE_PHENOTYPE);
    return this;
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
