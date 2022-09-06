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

  public PhenotypeQueryBuilder valueIntervalLimit(String operator, String value) {
    add(
        query.getValueIntervalPart().replace("{operator}", operator).replace("{value}", value),
        mappings);
    return this;
  }

  public PhenotypeQueryBuilder valueList(String values) {
    add(query.getValueListPart().replace("{values}", values), mappings);
    return this;
  }

  public PhenotypeQueryBuilder dateIntervalLimit(String operator, String value) {
    add(
        query.getDateIntervalPart().replace("{operator}", operator).replace("{value}", value),
        mappings);
    return this;
  }

  public PhenotypeQueryBuilder subjects(String subjectIds) {
    add(query.getSubjectsPart().replace("{subject_ids}", subjectIds));
    return this;
  }
}
