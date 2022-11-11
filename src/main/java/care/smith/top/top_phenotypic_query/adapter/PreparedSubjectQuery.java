package care.smith.top.top_phenotypic_query.adapter;

import java.util.Map;

import care.smith.top.model.Restriction;
import care.smith.top.top_phenotypic_query.adapter.config.SubjectQueryBuilder;
import care.smith.top.top_phenotypic_query.search.SubjectSearch;
import care.smith.top.top_phenotypic_query.util.Restrictions;

public abstract class PreparedSubjectQuery {

  private SubjectSearch search;
  private String preparedQuery;

  public PreparedSubjectQuery(SubjectSearch search) {
    this.search = search;
    prepareQuery();
  }

  private void prepareQuery() {
    SubjectQueryBuilder builder = search.getSubjectQuery().getQueryBuilder().baseQuery();

    if (search.hasSexRestriction()) {
      Restriction sexR = search.getSexRestriction();
      if (Restrictions.hasValues(sexR))
        builder.sexList(getSexList(search.getSexMapping().getSourceRestriction(sexR)));
    }

    if (search.hasBirthdateRestriction()) {
      Restriction birthdateR = search.getBirthdateRestriction();
      if (Restrictions.hasInterval(birthdateR)) {
        Map<String, String> interval =
            getBirthdateInterval(search.getBirthdateMapping().getSourceRestriction(birthdateR));
        for (String key : interval.keySet()) builder.birthdateIntervalLimit(key, interval.get(key));
      }
    }

    this.preparedQuery = builder.build();
  }

  public String getPreparedQuery() {
    return preparedQuery;
  }

  public abstract String getSexList(Restriction r);

  public abstract Map<String, String> getBirthdateInterval(Restriction r);
}
