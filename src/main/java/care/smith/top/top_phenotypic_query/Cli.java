package care.smith.top.top_phenotypic_query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import care.smith.top.model.Entity;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.converter.csv.CSV;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;

@Command(
    name = "top-phenotypic-query",
    mixinStandardHelpOptions = true,
    description = "Perform phenotypic queries on data sources.",
    synopsisSubcommandLabel = "COMMAND")
public class Cli implements Callable<Integer> {

  private final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  public static void main(String... args) {
    int exitCode = new CommandLine(new Cli()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    throw new ParameterException(new CommandLine(new Cli()), "Missing required subcommand.");
  }

  @Command(mixinStandardHelpOptions = true)
  int query(
      @Option(
              names = {"-f", "--force"},
              description = "Overwrite existing output file.")
          boolean force,
      @Parameters(
              index = "0",
              paramLabel = "<query config JSON>",
              description = "Configuration of the query to be performed on the data source.")
          File queryConfigFile,
      @Parameters(
              index = "1",
              paramLabel = "<phenotype model JSON>",
              description = "JSON file containing model of phenotype class definitions")
          File modelFile,
      @Parameters(
              index = "2",
              paramLabel = "<adapter config YAML>",
              description = "Adapter configuration that specifies the data source.")
          File adapterConfigFile,
      @Parameters(
              index = "3",
              paramLabel = "<output ZIP>",
              description = "Location where resulting ZIP file will be stored.")
          File outputFile) {
    try {
      PhenotypeQuery query = MAPPER.readValue(queryConfigFile, PhenotypeQuery.class);
      Entity[] entities = MAPPER.readValue(modelFile, Entity[].class);

      DataAdapterConfig config =
          DataAdapterConfig.getInstanceFromStream(new FileInputStream(adapterConfigFile));
      if (config == null) throw new FileNotFoundException(adapterConfigFile.getAbsolutePath());

      DataAdapter adapter = DataAdapter.getInstance(config);
      PhenotypeFinder finder = new PhenotypeFinder(query, entities, adapter);
      ResultSet rs = finder.execute();
      adapter.close();

      writeResultSetToZip(rs, entities, outputFile, force);
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }

  void writeResultSetToZip(ResultSet resultSet, Entity[] entities, File zipFile, boolean force)
      throws IOException {
    if (zipFile.exists() && !force) throw new FileAlreadyExistsException(zipFile.getAbsolutePath());

    ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
    CSV csvConverter = new CSV();

    zipStream.putNextEntry(new ZipEntry("data.csv"));
    csvConverter.write(resultSet, zipStream);

    zipStream.putNextEntry(new ZipEntry("metadata.csv"));
    csvConverter.write(entities, zipStream);

    zipStream.close();
  }
}
