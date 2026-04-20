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

    String expectedReport =
        String.format(
            "\"ALGORITHMID\",\"ALGORITHMTITLE\",\"MODEL\",\"PHENOTYPEID\",\"PHENOTYPETITLE\",\"RESULTNAME\",\"RESULTVALUE\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"12\",\"2\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"24\",\"1\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"36\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"48\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"72\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"120\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"greater equal\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"no value\",\"1\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Weight-Height\",\"Weight-Height\",\"no timestamp\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"12\",\"1\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"24\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"36\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"48\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"72\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"120\",\"1\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"greater equal\",\"1\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"no value\",\"0\"\n"
                + "\"BMI\",\"BMI\",\"%1$s\",\"Dabi-Infect-Op\",\"Dabi|en,Dabi|de-Infection|en,Infektion|de-Operation|en,Operation|de\",\"no timestamp\",\"1\"\n",
            data.getFileName());

    assertEquals(expectedReport, Files.readString(report));

    Files.deleteIfExists(config);
    Files.deleteIfExists(data);
    Files.deleteIfExists(report);
  }

  Path getConfig() throws IOException {
    Path config = Files.createTempFile("time_analysis_config", ".yml");
    String configText =
        "periods: [ 12, 24, 36, 48, 72, 120 ]\r\n"
            + "\r\n"
            + "phenotypes:\r\n"
            + "\r\n"
            + "  BMI:\r\n"
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

    addNum("1", weight, "2010-01-03", null, null);
    addNum("1", weight, "2010-01-01T18:00", null, null);
    addNum("1", weight, "2010-01-02", null, null);
    addNum("1", height, "2010-01-04", null, null);
    addNum("1", height, "2010-01-01T07:00", null, null);
    addBool("1", dabi, "2010-01-05", null, null);
    addBool("1", dabi, "2010-01-06", null, null);
    addBool("1", dabi, "2010-01-07", null, null);
    addBool("1", infect, "2010-01-05", null, null);
    addBool("1", infect, "2010-01-06", null, null);
    addBool("1", op, "2010-01-05", null, null);
    addBool("1", op, "2010-01-06", null, null);
    addBool("1", op, "2010-01-07", null, null);

    addNum("2", weight, null, "2010-01-03", null);
    addNum("2", weight, null, null, "2010-01-01T18:00");
    addNum("2", weight, null, "2010-01-02", "2010-01-03");
    addNum("2", height, "2010-01-01T21:00", null, null);
    addNum("2", height, null, "2010-01-01T20:00", "2010-01-01T21:00");
    addBool("2", dabi, null, null, "2010-01-07");
    addBool("2", dabi, null, null, "2010-01-07");
    addBool("2", dabi, null, null, "2010-01-07");
    addBool("2", infect, null, "2010-01-08", "2010-01-09");
    addBool("2", infect, null, "2010-01-08", "2010-01-09");
    addBool("2", op, null, "2010-01-11", null);
    addBool("2", op, null, "2010-01-11", null);
    addBool("2", op, null, "2010-01-11", null);

    addNum("3", weight, "2010-01-03", null, null);
    addNum("3", weight, "2010-01-01T18:00", null, null);
    addNum("3", weight, "2010-01-02", null, null);
    addBool("3", dabi, "2010-01-05", null, null);
    addBool("3", dabi, "2010-01-06", null, null);
    addBool("3", dabi, "2010-01-07", null, null);
    addBool("3", infect, "2010-02-05", null, null);
    addBool("3", infect, "2010-02-06", null, null);
    addBool("3", op, "2010-03-05", null, null);
    addBool("3", op, "2010-03-06", null, null);
    addBool("3", op, "2010-03-07", null, null);

    addNum("4", weight, "2010-01-03", null, null);
    addNum("4", weight, "2010-01-01T18:00", null, null);
    addNum("4", weight, "2010-01-02", null, null);
    addNum("4", height, "2010-01-04", null, null);
    addNum("4", height, "2010-01-01T06:00", null, null);
    addBool("4", dabi, "2010-01-05", null, null);
    addBool("4", dabi, "2010-01-06", null, null);
    addBool("4", dabi, "2010-01-07", null, null);
    addBool("4", infect, null, null, null);
    addBool("4", infect, null, null, null);
    addBool("4", op, "2010-01-05", null, null);
    addBool("4", op, "2010-01-06", null, null);
    addBool("4", op, "2010-01-07", null, null);

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
