package care.smith.top.top_phenotypic_query.util.builder;

import java.net.URI;

import care.smith.top.model.Category;
import care.smith.top.model.Code;
import care.smith.top.model.CodeSystem;
import care.smith.top.model.EntityType;
import care.smith.top.model.Expression;
import care.smith.top.model.LocalisableText;
import care.smith.top.top_phenotypic_query.util.Entities;

public class Cat {

  private Category c;

  public Cat(String id, String codeSystemUri, String... codes) {
    this(id);
    codes(codeSystemUri, codes);
  }

  public Cat(String id) {
    c = (Category) new Category().entityType(EntityType.CATEGORY).id(id);
  }

  public static Code getCode(String codeSystemUri, String code) {
    CodeSystem s = new CodeSystem().uri(URI.create(codeSystemUri));
    return new Code().code(code).codeSystem(s);
  }

  public Cat code(String codeSystemUri, String code) {
    c.addCodesItem(getCode(codeSystemUri, code));
    return this;
  }

  public Cat code(String codeUri) {
    String[] sysCode = codeUri.split("\\s*\\|\\s*");
    return code(sysCode[0], sysCode[1]);
  }

  public Cat codes(String codeSystemUri, String... codes) {
    for (String code : codes) code(codeSystemUri, code);
    return this;
  }

  public Cat codes(String... codeUris) {
    for (String codeUri : codeUris) code(codeUri);
    return this;
  }

  public Cat title(String txt) {
    c.addTitlesItem(new LocalisableText().text(txt));
    return this;
  }

  public Cat title(String txt, String lang) {
    c.addTitlesItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public Cat titleDe(String txt) {
    return title(txt, "de");
  }

  public Cat titleEn(String txt) {
    return title(txt, "en");
  }

  public Cat synonym(String txt) {
    c.addSynonymsItem(new LocalisableText().text(txt));
    return this;
  }

  public Cat synonym(String txt, String lang) {
    c.addSynonymsItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public Cat synonymDe(String txt) {
    return synonym(txt, "de");
  }

  public Cat synonymEn(String txt) {
    return synonym(txt, "en");
  }

  public Cat description(String txt) {
    c.addDescriptionsItem(new LocalisableText().text(txt));
    return this;
  }

  public Cat description(String txt, String lang) {
    c.addDescriptionsItem(new LocalisableText().text(txt).lang(lang));
    return this;
  }

  public Cat descriptionDe(String txt) {
    return description(txt, "de");
  }

  public Cat descriptionEn(String txt) {
    return description(txt, "en");
  }

  public Cat annotations(String txt) {
    Entities.addAnnotations(c, txt);
    return this;
  }

  public Cat expression(Expression exp) {
    c.setExpression(exp);
    return this;
  }

  public Cat single() {
    c.setEntityType(EntityType.SINGLE_CONCEPT);
    return this;
  }

  public Cat composite() {
    c.setEntityType(EntityType.COMPOSITE_CONCEPT);
    return this;
  }

  public Cat subCategories(Category... cats) {
    for (Category cat : cats) c.addSubCategoriesItem(cat);
    return this;
  }

  public Category get() {
    return c;
  }
}
