package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import care.smith.top.model.Entity;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.ProjectionEntry;
import care.smith.top.model.QueryCriterion;
import care.smith.top.top_phenotypic_query.Cli;
import picocli.CommandLine;

class CliTest extends AbstractTest {

  private final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Test
  void query() throws IOException {
    Path queryConfig = createQueryConfig();
    Path model = createModel();
    Path output = Files.createTempFile("output", "zip");

    URL adapter_config =
        Thread.currentThread().getContextClassLoader().getResource("config/SQL_Adapter_Test.yml");
    assertNotNull(adapter_config);

    assertEquals(
        0,
        new CommandLine(new Cli())
            .execute(
                "query",
                "-f",
                queryConfig.toString(),
                model.toString(),
                adapter_config.getPath(),
                output.toString()));

    assertNotEquals(0, Files.size(output));

    try (ZipFile zip = new ZipFile(output.toFile())) {
      assertEquals(2, Collections.list(zip.entries()).size());
    } catch (IOException e) {
      fail(e);
    }
  }

  Path createQueryConfig() throws IOException {
    Path queryConfig = Files.createTempFile("query_config", "json");

    QueryCriterion criterion =
        (QueryCriterion)
            new QueryCriterion()
                .inclusion(true)
                .defaultAggregationFunctionId(defAgrFunc.getId())
                .subjectId(young.getId())
                .type(ProjectionEntry.TypeEnum.QUERYCRITERION);

    MAPPER.writeValue(queryConfig.toFile(), new PhenotypeQuery().addCriteriaItem(criterion));
    return queryConfig;
  }

  Path createModel() throws IOException {
    Path model = Files.createTempFile("model", "json");
    MAPPER.writeValue(model.toFile(), new Entity[] {age, young});
    return model;
  }
}
