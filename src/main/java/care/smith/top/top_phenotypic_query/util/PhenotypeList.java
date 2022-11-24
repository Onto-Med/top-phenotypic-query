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

import care.smith.top.model.Entity;
import care.smith.top.model.EntityType;
import care.smith.top.model.LocalisableText;
import care.smith.top.model.Phenotype;

public class PhenotypeList {

  private Map<String, Phenotype> phenotypes;

  private PhenotypeList(Phenotype... phens) {
    phenotypes = Stream.of(phens).collect(Collectors.toMap(Phenotype::getId, Function.identity()));
    for (Phenotype p : phenotypes.values()) {
      Phenotype supP = p.getSuperPhenotype();
      if (Phenotypes.isRestriction(p)) {
        p.setExpression(Expressions.restrictionToExpression(p));
        if (Phenotypes.isSingle(p) && p.getRestriction() == null)
          p.setRestriction(Restrictions.newRestrictionFromCodes(p));
      }
      if (supP != null) p.setSuperPhenotype(phenotypes.get(supP.getId()));
    }
  }

  public static PhenotypeList of(Phenotype... phenotypes) {
    return new PhenotypeList(phenotypes);
  }

  public static PhenotypeList of(PhenotypeList phenotypeList, Phenotype... phenotypes) {
    Set<Phenotype> phens = new HashSet<>(phenotypeList.getPhenotypes());
    for (Phenotype p : phenotypes) phens.add(p);
    return new PhenotypeList(phens.stream().toArray(Phenotype[]::new));
  }

  public static PhenotypeList read(String url, String user, String password) throws IOException {
    URLConnection uc = new URL(url).openConnection();
    String userpass = user + ":" + password;
    String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
    uc.setRequestProperty("Authorization", basicAuth);
    String text = new String(uc.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    Entity[] entities = mapper.readValue(text, Entity[].class);
    Phenotype[] phens =
        Stream.of(entities).filter(e -> !Phenotypes.isCategory(e)).toArray(Phenotype[]::new);
    return of(phens);
  }

  public Phenotype getPhenotype(String id) {
    return phenotypes.get(id);
  }

  public Phenotype getPhenotypeWithTitle(String title) {
    for (Phenotype p : getPhenotypes())
      for (LocalisableText t : p.getTitles()) if (title.equalsIgnoreCase(t.getText())) return p;
    return null;
  }

  public Collection<Phenotype> getPhenotypes() {
    return phenotypes.values();
  }

  public Collection<Phenotype> getPhenotypes(EntityType type) {
    return getPhenotypes().stream()
        .filter(p -> p.getEntityType() == type)
        .collect(Collectors.toSet());
  }

  public Set<String> getIds() {
    return phenotypes.keySet();
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
    return phenotypes.size();
  }

  @Override
  public String toString() {
    return "PhenotypeList [phenotypes=" + phenotypes.values() + "]";
  }
}
