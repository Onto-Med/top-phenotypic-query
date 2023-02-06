package care.smith.top.top_phenotypic_query.util;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;

public class Queries {

  public static QueryType getType(Query query, Entities phenotypes) {
    boolean cicWithoutNegation = false;

    for (QueryCriterion cri : query.getCriteria()) {
      Phenotype phe = phenotypes.getPhenotype(cri.getSubjectId());
      if (cri.isInclusion()) {
        if (Phenotypes.isSingle(phe)) return QueryType.TYPE_1;
        if ((Phenotypes.isCompositePhenotype(phe)
                && !Expressions.containsNegation(phe.getExpression(), phenotypes))
            || (Phenotypes.isCompositeRestriction(phe)
                && !Expressions.containsNegation(
                    phe.getSuperPhenotype().getExpression(), phenotypes)))
          cicWithoutNegation = true;
      }
    }

    return (cicWithoutNegation) ? QueryType.TYPE_2 : QueryType.TYPE_3;
  }

  public enum QueryType {
    TYPE_1, // contains single inclusion criteria
    TYPE_2, // no single inclusion criteria but composite inclusion criteria without negation
    TYPE_3 // no inclusion criteria or only composite inclusion criteria with negation
  }
}
