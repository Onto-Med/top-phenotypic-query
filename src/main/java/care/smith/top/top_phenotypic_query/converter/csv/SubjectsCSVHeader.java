package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.Phenotype;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.ProjectionEntry;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

public class SubjectsCSVHeader extends LinkedHashMap<SubjectsCSVHead, TreeSet<SubjectsCSVHead>> {

  private static final long serialVersionUID = 1L;
  private Entities phenotypes;
  private PhenotypeQuery query;
  private List<SubjectsCSVHead> header = new ArrayList<>();
  private List<String> titles = new ArrayList<>();

  protected SubjectsCSVHeader(Entities phenotypes, PhenotypeQuery query) {
    this.phenotypes = phenotypes;
    this.query = query;
    this.titles.add("Id");
    run();
  }

  protected List<SubjectsCSVHead> getHeader() {
    return header;
  }

  protected List<String> getTitles() {
    return titles;
  }

  private void run() {
    if (query.getProjection() != null)
      for (ProjectionEntry pro : query.getProjection()) putHead(pro.getSubjectId());

    if (query.getCriteria() != null)
      for (QueryCriterion cri : query.getCriteria()) putHead(cri.getSubjectId());

    if (query.getProjection() != null)
      for (ProjectionEntry pro : query.getProjection()) putHeadVars(pro.getSubjectId());

    if (query.getCriteria() != null)
      for (QueryCriterion cri : query.getCriteria()) putHeadVars(cri.getSubjectId());

    for (SubjectsCSVHead superHead : keySet()) {
      header.add(superHead);
      titles.add(superHead.getTitle());
      for (SubjectsCSVHead subHead : get(superHead)) {
        header.add(subHead);
        titles.add(subHead.getTitle());
      }
    }
  }

  private SubjectsCSVHead getHead(Phenotype p) {
    return new SubjectsCSVHead(
        p.getId(), Entities.getDefaultTitleFull(p), p.getDataType());
  }

  private void putHead(Phenotype p) {
    SubjectsCSVHead h = getHead(p);
    if (Phenotypes.isPhenotype(p)) {
      if (!containsKey(h)) put(h, new TreeSet<>());
    } else if (Phenotypes.isRestriction(p)) {
      SubjectsCSVHead superHead = getHead(p.getSuperPhenotype());
      TreeSet<SubjectsCSVHead> subHeader = get(superHead);
      if (subHeader == null) {
        subHeader = new TreeSet<>();
        put(superHead, subHeader);
      }
      subHeader.add(h);
    }
  }

  private void putHead(String pheId) {
    putHead(phenotypes.getPhenotype(pheId));
  }

  private void putHeadVars(String pheId) {
    Phenotype phe = phenotypes.getPhenotype(pheId);
    if (phe.getExpression() != null)
      for (String var : Expressions.getVariables(phe.getExpression(), phenotypes)) putHead(var);
  }
}
