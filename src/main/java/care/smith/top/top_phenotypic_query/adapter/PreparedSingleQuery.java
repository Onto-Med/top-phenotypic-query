package care.smith.top.top_phenotypic_query.adapter;

import java.util.Map;

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
  }

  public String getPreparedQuery() {
    PhenotypeQueryBuilder builder =
        search.getPhenotypeQuery().getQueryBuilder(search.getPhenotypeMappings()).baseQuery();
    CodeMapping codeMap = search.getCodeMapping();
    DateTimeRestriction dtr = search.getDateTimeRestriction();

    if (search.hasRestriction()) {
      Phenotype superPhe = search.getSuperPhenotype();
      Restriction r = search.getRestriction();
      if (r.getQuantifier() != Quantifier.ALL) {
        if (Restrictions.hasInterval(r)) {
          Map<String, String> interval = getInterval(codeMap.getSourceRestriction(r, superPhe));
          for (String key : interval.keySet())
            adapter.addValueIntervalLimit(key, interval.get(key), builder, r);
        } else if (Restrictions.hasValues(r)) {
          String values =
              Restrictions.getValuesAsString(
                  codeMap.getSourceRestriction(r, superPhe), adapter.getFormat());
          adapter.addValueList(values, builder, r);
        }
      }
    }

    if (dtr != null) {
      if (Restrictions.hasInterval(dtr)) {
        Map<String, String> interval =
            Restrictions.getIntervalAsStringMap(dtr, adapter.getFormat());
        for (String key : interval.keySet())
          adapter.addDateIntervalLimit(key, interval.get(key), builder);
      }
    }

    preparedQuery = builder.build();
    return preparedQuery;
  }

  protected abstract String getList(Restriction r);

  protected abstract Map<String, String> getInterval(Restriction r);
}
