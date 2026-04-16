package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.ProjectionEntry;
import care.smith.top.model.QueryCriterion;
import care.smith.top.model.QueryType;
import care.smith.top.top_phenotypic_query.Cli;
import care.smith.top.top_phenotypic_query.converter.csv.CSV;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.builder.Val;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class CliTest extends AbstractTest {

  private final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

  @Test
  void timeAnalysisTest() throws IOException {
    Path config = getConfig();
    Path data = getData();

    Path report = Files.createTempFile("time_analysis_report", ".csv");

    new CommandLine(new Cli())
        .execute(
            "analysis",
            "time-analysis",
            "-c",
            config.toString(),
            "-o",
            report.toString(),
            data.toString());

    Files.deleteIfExists(config);
    Files.deleteIfExists(data);
    Files.deleteIfExists(report);
  }

  Path getConfig() throws IOException {
    Path config = Files.createTempFile("time_analysis_config", ".yml");
    String configText =
        "timeIntervals: [ 12, 24, 36, 48, 72, 120 ]\r\n"
            + "\r\n"
            + "phenotypes:\r\n"
            + "\r\n"
            + "  BMI:\r\n"
            + "    - [ Weight, Height ]\r\n"
            + "    - [ Dabi, Infect, Op ]\r\n"
            + "\r\n"
            + "  Combi:\r\n"
            + "    - [ Weight, Height ]\r\n"
            + "    - [ Dabi, Infect, Op ]\r\n";
    Files.writeString(config, configText, StandardOpenOption.CREATE);
    return config;
  }

  Path getData() throws IOException {
    Path data = Files.createTempFile("result_set", ".zip");
    ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(data.toFile()));
    CSV csvConverter = new CSV();

    zipStream.putNextEntry(new ZipEntry("data_phenotypes.csv"));
    csvConverter.writePhenotypes(getResultSet(), phenotypes, zipStream);

    zipStream.putNextEntry(new ZipEntry("metadata.csv"));
    csvConverter.writeMetadata(phenotypes2, zipStream);

    zipStream.close();
    return data;
  }

  private ResultSet rs;

  ResultSet getResultSet() {
    rs = new ResultSet();

    addNum("1", weight, "2008-01-01", null, null);
    addNum("1", weight, "2009-01-01", null, null);
    addNum("1", weight, "2010-01-01", null, null);
    addNum("1", height, "2008-01-03", null, null);
    addNum("1", height, "2010-01-05", null, null);
    addBool("1", dabi, "2010-01-05", null, null);
    addBool("1", dabi, "2010-01-06", null, null);
    addBool("1", dabi, "2010-01-07", null, null);
    addBool("1", infect, "2010-02-07", null, null);
    addBool("1", infect, "2010-03-07", null, null);
    addBool("1", op, "2010-03-08", null, null);
    addBool("1", op, "2010-03-09", null, null);
    addBool("1", op, "2010-03-10", null, null);

    return rs;
  }

  void addNum(String patId, Phenotype phe, String date, String start, String end) {
    rs.addValue(
        patId, phe, null, Val.of(1, getDateTime(date), getDateTime(start), getDateTime(end)));
  }

  void addBool(String patId, Phenotype phe, String date, String start, String end) {
    rs.addValue(
        patId, phe, null, Val.of(true, getDateTime(date), getDateTime(start), getDateTime(end)));
  }

  LocalDateTime getDateTime(String date) {
    return (date == null) ? null : DateUtil.parse(date);
  }

  @Test
  void query() throws IOException {
    Path queryConfig = createQueryConfig();
    Path model = createModel();
    Path output = Files.createTempFile("output", ".zip");

    URL adapter_config =
        Thread.currentThread()
            .getContextClassLoader()
            .getResource("config/SQL_Adapter_Cli_Test.yml");
    assertNotNull(adapter_config);

    assertEquals(
        0,
        new CommandLine(new Cli())
            .execute(
                "query",
                "-f",
                "-o",
                output.toString(),
                model.toString(),
                adapter_config.getPath(),
                queryConfig.toString()));

    assertNotEquals(0, Files.size(output));

    try (ZipFile zip = new ZipFile(output.toFile())) {
      assertEquals(3, Collections.list(zip.entries()).size());
    } catch (IOException e) {
      fail(e);
    }

    assertEquals(
        0,
        new CommandLine(new Cli())
            .execute(
                "query", "-f", model.toString(), adapter_config.getPath(), queryConfig.toString()));
  }

  Path createQueryConfig() throws IOException {
    Path queryConfig = Files.createTempFile("query_config", ".json");

    QueryCriterion criterion =
        new QueryCriterion()
            .inclusion(true)
            .defaultAggregationFunctionId(defAgrFunc.getId())
            .subjectId(young.getId())
            .type(ProjectionEntry.TypeEnum.QUERY_CRITERION);

    MAPPER.writeValue(
        queryConfig.toFile(),
        new PhenotypeQuery().dataSource("").addCriteriaItem(criterion).type(QueryType.PHENOTYPE));
    return queryConfig;
  }

  Path createModel() throws IOException {
    Path model = Files.createTempFile("model", ".json");
    MAPPER.writeValue(model.toFile(), new Entity[] {age, young});
    return model;
  }
}
