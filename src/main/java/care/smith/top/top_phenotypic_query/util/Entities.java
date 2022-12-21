package care.smith.top.top_phenotypic_query.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import care.smith.top.model.Category;
import care.smith.top.model.Entity;
import care.smith.top.model.EntityType;
import care.smith.top.model.LocalisableText;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Repository;
import care.smith.top.top_phenotypic_query.util.builder.ExpressionBuilder;
import care.smith.top.top_phenotypic_query.util.builder.RestrictionBuilder;

public class Entities {

  private Map<String, Entity> entities = new LinkedHashMap<>();
  private Repository repo;

  private Entities(Entity... entities) {
    this.entities =
        Stream.of(entities).collect(Collectors.toMap(Entity::getId, Function.identity()));
  }

  public Entities deriveAdditionalProperties() {
    for (Phenotype p : getPhenotypes()) {
      Phenotype supP = p.getSuperPhenotype();
      if (Phenotypes.isRestriction(p)) {
        p.setExpression(ExpressionBuilder.ofRestriction(p));
        if (Phenotypes.isSingle(p) && p.getRestriction() == null)
          p.setRestriction(RestrictionBuilder.ofCodes(p));
      }
      if (supP != null) p.setSuperPhenotype(getPhenotype(supP.getId()));
    }
    return this;
  }

  public static Entities of(Entity... entities) {
    return new Entities(entities);
  }

  public static Entities of(Repository repo, Entity... entities) {
    return new Entities(entities).repository(repo);
  }

  public static Entities read(String repoUrl, String user, String password) throws IOException {
    return of(readRepository(repoUrl, user, password), readEntities(repoUrl, user, password));
  }

  public static Entity[] readEntities(String repoUrl, String user, String password)
      throws IOException {
    return read(repoUrl + "/entity", user, password, Entity[].class);
  }

  public static Repository readRepository(String repoUrl, String user, String password)
      throws IOException {
    return read(repoUrl, user, password, Repository.class);
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

  public static String getFirstTitle(Entity e) {
    return e.getTitles().get(0).getText();
  }

  private static final String ANN_SEP = "|";
  private static final String PROP_VAL_SEP = "::";

  public static String getAllTextProperties(Entity e) {
    String txt = toString("title", e.getTitles());
    if (e.getSynonyms() != null && !e.getSynonyms().isEmpty())
      txt += ANN_SEP + toString("synonym", e.getSynonyms());
    if (e.getDescriptions() != null && !e.getDescriptions().isEmpty())
      txt += ANN_SEP + toString("description", e.getDescriptions());
    return txt;
  }

  public static void addAllTextProperties(Entity e, String props) {
    if (props == null || props.isBlank()) return;
    String[] anns = props.split("\\s*\\" + ANN_SEP + "\\s*");
    for (String ann : anns) add(e, ann.split("\\s*" + PROP_VAL_SEP + "\\s*"));
  }

  private static String toString(String prop, List<LocalisableText> txts) {
    return txts.stream().map(t -> toString(prop, t)).collect(Collectors.joining("|"));
  }

  private static String toString(String prop, LocalisableText txt) {
    return (txt.getLang() == null)
        ? prop + PROP_VAL_SEP + txt.getText()
        : prop + PROP_VAL_SEP + txt.getText() + PROP_VAL_SEP + txt.getLang();
  }

  private static void add(Entity e, String[] vals) {
    LocalisableText txt = new LocalisableText();

    if (vals.length == 1) {
      e.addTitlesItem(txt.text(vals[0]));
      return;
    }

    txt.text(vals[1]);
    if (vals.length > 2) txt.lang(vals[2]);

    if ("title".equalsIgnoreCase(vals[0])) e.addTitlesItem(txt);
    else if ("synonym".equalsIgnoreCase(vals[0])) e.addSynonymsItem(txt);
    else if ("description".equalsIgnoreCase(vals[0])) e.addDescriptionsItem(txt);
  }

  public int size() {
    return entities.size();
  }

  @Override
  public String toString() {
    return "Entities [entities=" + entities.values() + "]";
  }
}
