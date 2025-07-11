package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.DataType;
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

public class CSVSubjectsHeader extends LinkedHashMap<CSVSubjectsHead, TreeSet<CSVSubjectsHead>> {

  private static final long serialVersionUID = 1L;
  private Entities phenotypes;
  private PhenotypeQuery query;
  private List<CSVSubjectsHead> header = new ArrayList<>();
  private List<String> titles = new ArrayList<>();

  protected CSVSubjectsHeader(Entities phenotypes, PhenotypeQuery query) {
    this.phenotypes = phenotypes;
    this.query = query;
    this.titles.add("Id");
    run();
  }

  protected List<CSVSubjectsHead> getHeader() {
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

    for (CSVSubjectsHead superHead : keySet()) {
      addHead(superHead);
      for (CSVSubjectsHead subHead : get(superHead)) addHead(subHead);
    }
  }

  private void addHead(CSVSubjectsHead h) {
    header.add(h);
    titles.add(h.getTitle());
    if (h.hasDateColumn()) titles.add(h.getTitle() + "(DATE)");
  }

  private CSVSubjectsHead getHead(Phenotype p) {
    return getHead(p.getId(), Entities.getDefaultTitleFull(p), p.getDataType(), p);
  }

  private CSVSubjectsHead getRestrictedValuesHead(Phenotype singleRestriction) {
    return getHead(
        Phenotypes.getRestrictedValuesKey(singleRestriction),
        Entities.getDefaultTitleFull(singleRestriction) + "(VALUES)",
        singleRestriction.getSuperPhenotype().getDataType(),
        singleRestriction);
  }

  private CSVSubjectsHead getHead(String id, String title, DataType dt, Phenotype p) {
    return new CSVSubjectsHead(id, title, p.getEntityType(), p.getItemType(), dt);
  }

  private void putHead(Phenotype p) {
    CSVSubjectsHead h = getHead(p);
    if (Phenotypes.isPhenotype(p)) {
      if (!containsKey(h)) put(h, new TreeSet<>());
    } else if (Phenotypes.isRestriction(p)) {
      CSVSubjectsHead superHead = getHead(p.getSuperPhenotype());
      TreeSet<CSVSubjectsHead> subHeader = get(superHead);
      if (subHeader == null) {
        subHeader = new TreeSet<>();
        put(superHead, subHeader);
      }
      subHeader.add(h);
      if (Phenotypes.isSingleRestriction(p)) subHeader.add(getRestrictedValuesHead(p));
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
