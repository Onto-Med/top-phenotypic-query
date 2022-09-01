package care.smith.top.top_phenotypic_query.simple_config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DataAdapterConfig {

  private String id;
  private Map<String, String> connection;
  private SubjectQuery subjectQuery;
  private Map<String, PhenotypeQuery> phenotypeQueries = new HashMap<>();

  public static DataAdapterConfig getInstance(String yamlFilePath) {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    DataAdapterConfig config = null;
    try {
      config = mapper.readValue(new File(yamlFilePath), DataAdapterConfig.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return config;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, String> getConnection() {
    return connection;
  }

  public String getConnectionAttribute(String name) {
    return connection.get(name);
  }

  public void setConnection(Map<String, String> connection) {
    this.connection = connection;
  }

  public SubjectQuery getSubjectQuery() {
    return subjectQuery;
  }

  public void setSubjectQuery(SubjectQuery subjectQuery) {
    this.subjectQuery = subjectQuery;
  }

  public Map<String, PhenotypeQuery> getPhenotypeQueries() {
    return phenotypeQueries;
  }

  public PhenotypeQuery getPhenotypeQuery(String type) {
    return phenotypeQueries.get(type);
  }

  public void setPhenotypeQueries(List<PhenotypeQuery> phenotypeQueries) {
    for (PhenotypeQuery phenotypeQuery : phenotypeQueries)
      this.phenotypeQueries.put(phenotypeQuery.getType(), phenotypeQuery);
  }

  @Override
  public String toString() {
    return "DataAdapterConfig [id="
        + id
        + ", connection="
        + connection
        + ", subjectQuery="
        + subjectQuery
        + ", phenotypeQueries="
        + phenotypeQueries
        + "]";
  }
}
