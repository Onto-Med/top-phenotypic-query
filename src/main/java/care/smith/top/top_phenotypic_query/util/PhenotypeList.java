package care.smith.top.top_phenotypic_query.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import care.smith.top.model.Category;
import care.smith.top.model.Entity;
import care.smith.top.model.EntityType;
import care.smith.top.model.LocalisableText;
import care.smith.top.model.Phenotype;

public class PhenotypeList {

  private Map<String, Entity> entities;

  private PhenotypeList(Entity... entities) {
    this.entities =
        Stream.of(entities).collect(Collectors.toMap(Entity::getId, Function.identity()));
    for (Phenotype p : getPhenotypes()) {
      Phenotype supP = p.getSuperPhenotype();
      if (Phenotypes.isRestriction(p)) {
        p.setExpression(Expressions.restrictionToExpression(p));
        if (Phenotypes.isSingle(p) && p.getRestriction() == null)
          p.setRestriction(Restrictions.newRestrictionFromCodes(p));
      }
      if (supP != null) p.setSuperPhenotype(getPhenotype(supP.getId()));
    }
  }

  public static PhenotypeList of(Entity... entities) {
    return new PhenotypeList(entities);
  }

  public static PhenotypeList of(PhenotypeList phenotypeList, Entity... entities) {
    Set<Entity> newEntities = new HashSet<>(phenotypeList.getPhenotypes());
    for (Entity p : entities) newEntities.add(p);
    return new PhenotypeList(newEntities.toArray(Entity[]::new));
  }

  public static PhenotypeList read(String url, String user, String password) throws IOException {
    URLConnection uc = new URL(url).openConnection();
    String userpass = user + ":" + password;
    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
    uc.setRequestProperty("Authorization", basicAuth);
    String text = new String(uc.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    return of(mapper.readValue(text, Entity[].class));
  }

  public Entity getEntity(String id) {
    return entities.get(id);
  }

  public Phenotype getPhenotype(String id) {
    return (Phenotype) getEntity(id);
  }

  public Phenotype getPhenotypeWithTitle(String title) {
    for (Phenotype p : getPhenotypes())
      for (LocalisableText t : p.getTitles()) if (title.equalsIgnoreCase(t.getText())) return p;
    return null;
  }

  public Collection<Entity> getEntities() {
    return entities.values();
  }

  public Collection<Category> getCategories() {
    return getEntities().stream()
        .filter(e -> e.getEntityType() == EntityType.CATEGORY)
        .map(Category.class::cast)
        .collect(Collectors.toSet());
  }

  public Collection<Phenotype> getPhenotypes() {
    return getEntities().stream()
        .filter(e -> e.getEntityType() != EntityType.CATEGORY)
        .map(Phenotype.class::cast)
        .collect(Collectors.toSet());
  }

  public Collection<Phenotype> getPhenotypes(EntityType type) {
    return getEntities().stream()
        .filter(e -> e.getEntityType() == type)
        .map(Phenotype.class::cast)
        .collect(Collectors.toSet());
  }

  public Set<String> getIds() {
    return entities.keySet();
  }

  public Multimap<String, String> getIdsAndTitles() {
    Multimap<String, String> mm = HashMultimap.create();
    for (Phenotype p : getPhenotypes())
      for (LocalisableText t : p.getTitles()) mm.put(p.getId(), t.getText());
    return mm;
  }

  public Map<String, String> getIdsAndSingleTitles() {
    Map<String, String> mm = new HashMap<>();
    for (Phenotype p : getPhenotypes()) mm.put(p.getId(), p.getTitles().get(0).getText());
    return mm;
  }

  public int size() {
    return entities.size();
  }

  @Override
  public String toString() {
    return "PhenotypeList [entities=" + entities.values() + "]";
  }
}
