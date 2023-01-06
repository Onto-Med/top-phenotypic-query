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
    add(query.getValueIntervalPart(), operator, value, mappings);
    return this;
  }

  public PhenotypeQueryBuilder numberValueIntervalLimit(String operator, String value) {
    add(query.getNumberValueIntervalPart(), operator, value, mappings);
    return this;
  }

  public PhenotypeQueryBuilder dateValueIntervalLimit(String operator, String value) {
    add(query.getDateTimeValueIntervalPart(), operator, value, mappings);
    return this;
  }

  public PhenotypeQueryBuilder valueList(String values) {
    add(query.getValueListPart(), values, mappings);
    return this;
  }

  public PhenotypeQueryBuilder stringValueList(String values) {
    add(query.getTextValueListPart(), values, mappings);
    return this;
  }

  public PhenotypeQueryBuilder numberValueList(String values) {
    add(query.getNumberValueListPart(), values, mappings);
    return this;
  }

  public PhenotypeQueryBuilder conceptValueList(String values) {
    add(query.getConceptValueListPart(), values, mappings);
    return this;
  }

  public PhenotypeQueryBuilder dateIntervalLimit(String operator, String value) {
    add(query.getDateTimeIntervalPart(), operator, value, mappings);
    return this;
  }

  public PhenotypeQueryBuilder subjects(String subjectIds) {
    add(query.getSubjectsPart(), subjectIds);
    return this;
  }
}
