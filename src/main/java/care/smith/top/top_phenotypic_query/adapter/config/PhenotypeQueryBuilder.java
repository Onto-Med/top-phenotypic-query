package care.smith.top.top_phenotypic_query.adapter.config;

import java.util.Map;

public class PhenotypeQueryBuilder extends QueryBuilder {

  private PhenotypeQuery query;
  private Map<String, String> mappings;

  public PhenotypeQueryBuilder(PhenotypeQuery query, Map<String, String> mappings) {
    this.query = query;
    this.mappings = mappings;
  }

  public PhenotypeQueryBuilder baseQuery() {
    add(query.getBaseQuery(), mappings);
    return this;
  }

  public PhenotypeQueryBuilder valueRangeLimit(String operator, String value) {
    add(
        query.getValueRangePart().replace("{operator}", operator).replace("{value}", value),
        mappings);
    return this;
  }

  public PhenotypeQueryBuilder dateRangeLimit(String operator, String value) {
    add(
        query.getDateRangePart().replace("{operator}", operator).replace("{value}", value),
        mappings);
    return this;
  }

  public PhenotypeQueryBuilder subjects(String subjectIds) {
    add(query.getSubjectsPart().replace("{subject_ids}", subjectIds));
    return this;
  }
}
