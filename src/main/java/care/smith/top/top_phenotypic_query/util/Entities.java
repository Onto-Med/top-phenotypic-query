package care.smith.top.top_phenotypic_query.util;

import care.smith.top.model.Category;
import care.smith.top.model.Entity;
import care.smith.top.model.EntityType;
import care.smith.top.model.ItemType;
import care.smith.top.model.LocalisableText;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Repository;
import care.smith.top.model.Restriction;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Entities {

  private Map<String, Entity> entities = new LinkedHashMap<>();
  private Repository repo;

  private Entities(Entity... entities) {
    add(entities);
  }

  private Entities(List<Entity> entities) {
    for (Entity e : entities) add(e);
  }

  public void add(Entity e) {
    entities.put(e.getId(), e);
  }

  public void add(Entity... entities) {
    for (Entity e : entities) add(e);
  }

  public Entities deriveAdditionalProperties(DataAdapterConfig config) {
    for (Phenotype p : getPhenotypes()) {
      Phenotype supP = p.getSuperPhenotype();
      if (supP != null) {
        supP = getPhenotype(supP.getId());
        p.setSuperPhenotype(supP);
      }
      if (Phenotypes.isRestriction(p)) {
        if (Phenotypes.isSingle(p) && p.getRestriction() == null) p.setRestriction(Res.ofCodes(p));
        if (supP != null) p.setExpression(Exp.inRestriction(supP, getRestriction(config, supP, p)));
      }
    }
    return this;
  }

  public Entities deriveAdditionalProperties() {
    return deriveAdditionalProperties(null);
  }

  private Restriction getRestriction(DataAdapterConfig config, Phenotype supP, Phenotype p) {
    if (Phenotypes.isSinglePhenotype(supP) && config != null) {
      CodeMapping codeMap = config.getCodeMappingIncludingSubjectParameters(supP);
      if (codeMap != null) return codeMap.getRestriction(p.getRestriction());
    }
    return p.getRestriction();
  }

  public static Entities of(Entity... entities) {
    return new Entities(entities);
  }

  public static Entities of(List<Entity> entities) {
    return new Entities(entities);
  }

  public static Entities of(Repository repo, Entity... entities) {
    return new Entities(entities).repository(repo);
  }

  public static Entities of(Repository repo, List<Entity> entities) {
    return new Entities(entities).repository(repo);
  }

  public static Entities of(String repoUrl, String user, String password)
      throws IOException, InterruptedException, URISyntaxException {
    String token = HTTP.getToken(user, password);
    return of(HTTP.readRepository(repoUrl, token), HTTP.readEntities(repoUrl, token));
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

  public Entity[] getEntitiesArray() {
    return getEntities().toArray(new Entity[0]);
  }

  public Collection<Category> getCategories() {
    return getEntities().stream()
        .filter(e -> e.getEntityType() == EntityType.CATEGORY)
        .map(Category.class::cast)
        .collect(Collectors.toSet());
  }

  public Set<String> getPhenotypeIds() {
    return getPhenotypes().stream().map(e -> e.getId()).collect(Collectors.toSet());
  }

  public Collection<Phenotype> getPhenotypes() {
    return getEntities().stream()
        .filter(e -> e.getEntityType() != EntityType.CATEGORY)
        .map(Phenotype.class::cast)
        .collect(Collectors.toList());
  }

  public Collection<Phenotype> getPhenotypes(EntityType type) {
    return getEntities().stream()
        .filter(e -> e.getEntityType() == type)
        .map(Phenotype.class::cast)
        .collect(Collectors.toList());
  }

  public Collection<Phenotype> getSinglePhenotypes() {
    return getPhenotypes(EntityType.SINGLE_PHENOTYPE);
  }

  public Collection<Phenotype> getSingleRestrictions() {
    return getPhenotypes(EntityType.SINGLE_RESTRICTION);
  }

  public Collection<Phenotype> getCompositePhenotypes() {
    return getPhenotypes(EntityType.COMPOSITE_PHENOTYPE);
  }

  public Set<String> getIds() {
    return entities.keySet();
  }

  public static String getFirstTitle(Entity e) {
    return (e.getTitles() == null || e.getTitles().isEmpty())
        ? e.getId()
        : e.getTitles().get(0).getText();
  }

  public static List<String> getTitles(Entity e) {
    return getAnnotations(e.getTitles());
  }

  public static List<String> getSynonyms(Entity e) {
    return getAnnotations(e.getSynonyms());
  }

  public static List<String> getDescriptions(Entity e) {
    return getAnnotations(e.getDescriptions());
  }

  private static List<String> getAnnotations(List<LocalisableText> texts) {
    if (texts == null) return new ArrayList<>();
    return texts.stream().map(t -> toString(t)).collect(Collectors.toList());
  }

  private static String toString(LocalisableText txt) {
    return (txt.getLang() == null) ? txt.getText() : txt.getText() + PROP_VAL_SEP + txt.getLang();
  }

  public static String getDefaultTitle(Entity e) {
    String title = getTitle(e, "en");
    return (title == null) ? getFirstTitle(e) : title;
  }

  public static String getDefaultTitleFull(Phenotype p) {
    String t = getDefaultTitle(p);
    if (p.getSuperPhenotype() == null) {
      if (Phenotypes.isPhenotype(p) && p.getUnit() != null) return t + "[" + p.getUnit() + "]";
      else return t;
    } else return getDefaultTitle(p.getSuperPhenotype()) + ANN_SEP + t;
  }

  public static String getTitle(Entity e, String lang) {
    return getText(e.getTitles(), lang);
  }

  public static String getSynonym(Entity e, String lang) {
    return getText(e.getSynonyms(), lang);
  }

  public static String getDescription(Entity e, String lang) {
    return getText(e.getDescriptions(), lang);
  }

  private static String getText(List<LocalisableText> texts, String lang) {
    return texts.stream()
        .filter(t -> Objects.equals(t.getLang(), lang))
        .map(t -> t.getText())
        .findFirst()
        .orElse(null);
  }

  private static final String ANN_SEP = "::";
  private static final String PROP_VAL_SEP = "|";

  public static String getAnnotations(Entity e) {
    String txt = toString("title", e.getTitles());
    if (e.getSynonyms() != null && !e.getSynonyms().isEmpty())
      txt += ANN_SEP + toString("synonym", e.getSynonyms());
    if (e.getDescriptions() != null && !e.getDescriptions().isEmpty())
      txt += ANN_SEP + toString("description", e.getDescriptions());
    return txt;
  }

  private static String toString(String prop, List<LocalisableText> txts) {
    return txts.stream()
        .map(t -> prop + PROP_VAL_SEP + toString(t))
        .collect(Collectors.joining(ANN_SEP));
  }

  public static void addAnnotations(Entity e, String props) {
    if (props == null || props.isBlank()) return;
    String[] anns = props.split("\\s*" + ANN_SEP + "\\s*");
    for (String ann : anns) add(e, ann.split("\\s*\\" + PROP_VAL_SEP + "\\s*"));
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

  public NoCodesException checkNoCodes() {
    String phenotypes =
        getEntities().stream()
            .filter(e -> hasNoCodes(e))
            .map(e -> e.getId() + " (" + getFirstTitle(e) + ")")
            .collect(Collectors.joining(", "));
    if (phenotypes.isEmpty()) return null;
    return new NoCodesException(phenotypes);
  }

  private boolean hasNoCodes(Entity e) {
    if (e.getEntityType() != EntityType.SINGLE_PHENOTYPE) return false;
    Phenotype p = (Phenotype) e;
    if (p.getItemType() == ItemType.SUBJECT_AGE
        || p.getItemType() == ItemType.SUBJECT_BIRTH_DATE
        || p.getItemType() == ItemType.SUBJECT_SEX
        || p.getItemType() == ItemType.ENCOUNTER) return false;
    return e.getCodes() == null || e.getCodes().isEmpty();
  }

  public class NoCodesException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoCodesException(String phenotypes) {
      super("The following single phenotypes have no codes: " + phenotypes + "!");
    }
  }

  public static void writeJSON(String file, Entity... entities) {
    try {
      new ObjectMapper().writeValue(new File(file), entities);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void writePrettyJSON(String file, Entity... entities) {
    try {
      new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(file), entities);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
