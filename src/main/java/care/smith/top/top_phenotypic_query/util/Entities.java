package care.smith.top.top_phenotypic_query.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
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
import care.smith.top.model.Repository;

public class Entities {

  private Map<String, Entity> entities = new HashMap<>();
  private Repository repo;

  private Entities(Entity... entities) {
    Stream.of(entities).collect(Collectors.toMap(Entity::getId, Function.identity()));
    init();
  }

  private Entities(Collection<Entity> entities) {
    entities.stream().collect(Collectors.toMap(Entity::getId, Function.identity()));
    init();
  }

  private Entities(Entities entities) {
    this(entities.getEntities());
    this.repo = entities.getRepository();
  }

  public static Entities of(Entity... entities) {
    return new Entities(entities);
  }

  public static Entities of(Repository repo, Entity... entities) {
    return new Entities(entities).repository(repo);
  }

  public static Entities of(Entities entities1, Entity... entities2) {

    Entities res = new Entities(entities1);
    res.add(entities2);
    return res;
  }

  private void init() {
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

  public Entities repository(Repository repo) {
    this.repo = repo;
    return this;
  }

  public Repository getRepository() {
    return repo;
  }

  public String getRepoName() {
    return repo.getName();
  }

  public String getRepoDescription() {
    return repo.getDescription();
  }

  public static Entities read(String url, String user, String password) throws IOException {
    if (url.endsWith("entity")) return of(read(url, user, password, Entity[].class));
    Repository repo = read(url, user, password, Repository.class);
    Entity[] entities = read(url + "/entity", user, password, Entity[].class);
    return of(repo, entities);
  }

  private static <T> T read(String url, String user, String password, Class<T> cls)
      throws IOException {
    URLConnection uc = new URL(url).openConnection();
    String userpass = user + ":" + password;
    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
    uc.setRequestProperty("Authorization", basicAuth);
    String text = new String(uc.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    return mapper.readValue(text, cls);
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
