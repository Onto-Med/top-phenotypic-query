package care.smith.top.top_phenotypic_query.util.builder;

import care.smith.top.model.Category;
import care.smith.top.model.EntityType;
import care.smith.top.model.LocalisableText;
import care.smith.top.top_phenotypic_query.util.Entities;

public class Cat {

  private Category c;

  public Cat(String id) {
    c = (Category) new Category().entityType(EntityType.CATEGORY).id(id);
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

  public Cat superCategory(Category superCat) {
    c.addSuperCategoriesItem(superCat);
    return this;
  }

  public Category get() {
    return c;
  }
}
