package care.smith.top.top_phenotypic_query.util;

import care.smith.top.model.Phenotype;
import care.smith.top.model.Query;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.search.SingleQueryMan;
import care.smith.top.top_phenotypic_query.search.SubjectQueryMan;

public class Queries {

  public static QueryType getType(
      Query query, Entities phenotypes, SubjectQueryMan sbjMan, SingleQueryMan sinMan) {
    boolean sic = false;
    boolean cicWithoutNegation = false;

    for (QueryCriterion cri : query.getCriteria()) {
      Phenotype phe = phenotypes.getPhenotype(cri.getSubjectId());
      if (cri.isInclusion()) {
        if (Phenotypes.isSingle(phe)) sic = true;
        else if ((Phenotypes.isCompositePhenotype(phe)
                && !Expressions.containsNegation(phe.getExpression(), phenotypes))
            || (Phenotypes.isCompositeRestriction(phe)
                && !Expressions.containsNegation(
                    phe.getSuperPhenotype().getExpression(), phenotypes)))
          cicWithoutNegation = true;
      }
    }

    if ((!sic && !cicWithoutNegation) || sbjMan.hasComplexParameters()) return QueryType.TYPE_1;
    if (sbjMan.hasInclusion()) return QueryType.TYPE_2;
    if (sinMan.hasInclusion()) return QueryType.TYPE_3;
    return QueryType.TYPE_4;
  }

  public enum QueryType {
    TYPE_1, // all subjects query (ASQ) required, if:
    // a) no single IC and no composite IC without negation or
    // b) no subject single IC but an unrestricted subject variable or
    // c) no subject single IC but more than one subject restriction of the same unrestricted
    // phenotype (age and birth date count together)
    TYPE_2, // no ASQ, but subject single IC
    TYPE_3, // no ASQ, no subject single IC, but other single IC
    TYPE_4 // no ASQ, no single IC (i.e., composite IC without negation exist)
  }

  //  public static QueryType getType(Query query, Entities phenotypes) {
  //	    boolean cicWithoutNegation = false;
  //
  //	    for (QueryCriterion cri : query.getCriteria()) {
  //	      Phenotype phe = phenotypes.getPhenotype(cri.getSubjectId());
  //	      if (cri.isInclusion()) {
  //	        if (Phenotypes.isSingle(phe)) return QueryType.TYPE_1;
  //	        if ((Phenotypes.isCompositePhenotype(phe)
  //	                && !Expressions.containsNegation(phe.getExpression(), phenotypes))
  //	            || (Phenotypes.isCompositeRestriction(phe)
  //	                && !Expressions.containsNegation(
  //	                    phe.getSuperPhenotype().getExpression(), phenotypes)))
  //	          cicWithoutNegation = true;
  //	      }
  //	    }
  //
  //	    return (cicWithoutNegation) ? QueryType.TYPE_2 : QueryType.TYPE_3;
  //	  }
  //
  //  public enum QueryType {
  //	  TYPE_1, // contains single inclusion criteria
  //	  TYPE_2, // no single inclusion criteria but composite inclusion criteria without negation
  //	  TYPE_3 // no inclusion criteria or only composite inclusion criteria with negation
  //  }
}
