package care.smith.top.top_phenotypic_query.util;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;

public class Queries {

  //  public static boolean isAllSubjectsQueryRequired(Query query, Entities phenotypes) {
  //    for (QueryCriterion cri : query.getCriteria()) {
  //      Phenotype phe = phenotypes.getPhenotype(cri.getSubjectId());
  //      if (cri.isInclusion()) {
  //        if (Phenotypes.isSingle(phe)) return false;
  //        if (Phenotypes.isCompositePhenotype(phe)
  //            && !Expressions.containsNegation(phe.getExpression(), phenotypes)) return false;
  //        if (Phenotypes.isCompositeRestriction(phe)
  //            && !Expressions.containsNegation(phe.getExpression(), phenotypes)
  //            && !Expressions.containsNegation(phe.getSuperPhenotype().getExpression(),
  // phenotypes))
  //          return false;
  //      }
  //    }
  //    return true;
  //  }

  public static boolean containsSingleInclusionCriteria(Query query, Entities phenotypes) {
    for (QueryCriterion cri : query.getCriteria()) {
      Phenotype phe = phenotypes.getPhenotype(cri.getSubjectId());
      if (cri.isInclusion() && Phenotypes.isSingle(phe)) return true;
    }
    return false;
  }
}
