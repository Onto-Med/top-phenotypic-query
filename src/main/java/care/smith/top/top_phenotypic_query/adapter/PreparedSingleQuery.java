package care.smith.top.top_phenotypic_query.adapter;

import care.smith.top.model.DateTimeRestriction;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Quantifier;
import care.smith.top.model.Restriction;
import care.smith.top.top_phenotypic_query.adapter.config.CodeMapping;
import care.smith.top.top_phenotypic_query.adapter.config.PhenotypeQueryBuilder;
import care.smith.top.top_phenotypic_query.search.SingleSearch;
import care.smith.top.top_phenotypic_query.util.Restrictions;

public abstract class PreparedSingleQuery {

  private SingleSearch search;
  private String preparedQuery;

  public PreparedSingleQuery(SingleSearch search) {
    this.search = search;
    prepareQuery();
  }

  private void prepareQuery() {
    PhenotypeQueryBuilder builder =
        search.getPhenotypeQuery().getQueryBuilder(search.getPhenotypeMappings()).baseQuery();
    CodeMapping codeMap = search.getCodeMapping();
    DateTimeRestriction dtr = search.getDateTimeRestriction();

    if (search.hasRestriction()) {
      Phenotype superPhe = search.getSuperPhenotype();
      Restriction r = search.getRestriction();
      if (r.getQuantifier() != Quantifier.ALL) {
        Restriction sourceR = codeMap.getSourceRestriction(r, superPhe);
        if (Restrictions.hasInterval(r)) addValueIntervalLimit(sourceR, builder);
        else if (Restrictions.hasValues(r)) addValueList(sourceR, builder);
      }
    }

    if (dtr != null) {
      if (Restrictions.hasInterval(dtr)) addDateIntervalLimit(dtr, builder);
    }

    this.preparedQuery = builder.build();
  }

  public String getPreparedQuery() {
    return preparedQuery;
  }

  public abstract void addValueIntervalLimit(Restriction r, PhenotypeQueryBuilder builder);

  public abstract void addValueList(Restriction r, PhenotypeQueryBuilder builder);

  public abstract void addDateIntervalLimit(Restriction r, PhenotypeQueryBuilder builder);
}
