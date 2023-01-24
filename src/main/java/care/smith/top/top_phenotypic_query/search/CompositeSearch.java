package care.smith.top.top_phenotypic_query.search;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.c2reasoner.C2R;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;

public class CompositeSearch extends PhenotypeSearch {

  private QueryCriterion criterion;
  private ResultSet rs;
  private Entities phenotypes;

  private Logger log = LoggerFactory.getLogger(CompositeSearch.class);

  public CompositeSearch(Query query, QueryCriterion criterion, ResultSet rs, Entities phenotypes) {
    super(query);
    this.criterion = criterion;
    this.rs = rs;
    this.phenotypes = phenotypes;
  }

  @Override
  public ResultSet execute() {
    Phenotype phe = phenotypes.getPhenotype(criterion.getSubjectId());
    if (phe.getExpression() == null)
      throw new IllegalArgumentException(
          String.format(
              "A query criterion phenotype must have an expression! The phenotype '%s' has no expression.",
              phe.getId()));
    for (String sbjId : new HashSet<>(rs.getSubjectIds())) executeForSubject(phe, sbjId);
    return rs;
  }

  private void executeForSubject(Phenotype phe, String sbjId) {
    log.debug("start composite search: {} :: {} ...", sbjId, phe.getId());

    C2R c2r =
        new C2R()
            .phenotypes(phenotypes)
            .values(rs.getPhenotypes(sbjId))
            .dateTimeRestriction(criterion.getDateTimeRestriction())
            .defaultAggregateFunction(criterion.getDefaultAggregationFunctionId());

    Expression resExp = c2r.calculate(phe);
    boolean res = (resExp == null) ? false : Expressions.getBooleanValue(resExp);

    log.debug("end composite search: {} :: {} :: {}", sbjId, phe.getId(), res);

    if ((!criterion.isInclusion() && res) || (criterion.isInclusion() && !res))
      rs.removeSubject(sbjId);
  }
}
