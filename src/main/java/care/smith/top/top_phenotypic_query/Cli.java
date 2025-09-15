package care.smith.top.top_phenotypic_query;

import care.smith.top.model.*;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.converter.csv.CSV;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
      @Option(
              names = {"-o", "--output"},
              paramLabel = "<output ZIP>",
              description =
                  "Location where resulting ZIP file will be stored. If missing, output will be printed to stdout.")
          File outputFile,
      @Option(
              names = {"-p", "--phenotype"},
              paramLabel = "<phenotype-id>",
              description =
                  "A phenotype ID to be used as inclusion criterion to query the data source. "
                      + "To provide multiple phenotype IDs, add this option multiple times. The IDs must be present in the provided model. "
                      + "Alternatively, you can provide a query configuration file (see other positional parameter).")
          List<String> phenotypeIds,
      @Parameters(
              index = "0",
              paramLabel = "<phenotype model JSON>",
              description = "JSON file containing model of phenotype class definitions")
          File modelFile,
      @Parameters(
              index = "1",
              paramLabel = "<adapter config YAML>",
              description = "Adapter configuration that specifies the data source.")
          File adapterConfigFile,
      @Parameters(
              index = "2",
              paramLabel = "<query config JSON>",
              description =
                  "Configuration of the query to be performed on the data source. "
                      + "Alternatively, you can provide a phenotype ID (see `--phenotype`)."
                      + "This configuration file takes precedence over the `--phenotype` option.",
              arity = "0..1")
          File queryConfigFile) {
    try {
      PhenotypeQuery query;
      if (queryConfigFile != null) {
        query = MAPPER.readValue(queryConfigFile, PhenotypeQuery.class);
      } else if (phenotypeIds != null && !phenotypeIds.isEmpty()) {
        query =
            new PhenotypeQuery(UUID.randomUUID(), QueryType.PHENOTYPE, "placeholder")
                .criteria(phenotypeIds.stream().map(this::idToCriterion).toList());
      } else {
        throw new IllegalArgumentException(
            "Either a query configuration file or a phenotype ID must be provided!");
      }

      Entity[] entities = MAPPER.readValue(modelFile, Entity[].class);

      DataAdapterConfig config =
          DataAdapterConfig.getInstanceFromStream(new FileInputStream(adapterConfigFile));
      if (config == null) throw new FileNotFoundException(adapterConfigFile.getAbsolutePath());

      DataAdapter adapter = DataAdapter.getInstance(config);
      PhenotypeFinder finder = new PhenotypeFinder(query, entities, adapter);
      ResultSet rs = finder.execute();
      adapter.close();

      if (outputFile != null) {
        writeResultSetToZip(rs, entities, query, outputFile, force);
      } else {
        System.out.println(rs.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }

  QueryCriterion idToCriterion(String id) {
    return new QueryCriterion(true, id, ProjectionEntry.TypeEnum.QUERY_CRITERION);
  }

  void writeResultSetToZip(
      ResultSet resultSet, Entity[] entities, PhenotypeQuery query, File zipFile, boolean force)
      throws IOException {
    if (zipFile.exists() && !force) throw new FileAlreadyExistsException(zipFile.getAbsolutePath());

    ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
    CSV csvConverter = new CSV();

    zipStream.putNextEntry(new ZipEntry("data_phenotypes.csv"));
    csvConverter.writePhenotypes(resultSet, entities, zipStream);

    zipStream.putNextEntry(new ZipEntry("data_subjects.csv"));
    csvConverter.writeSubjects(resultSet, entities, query, zipStream);

    zipStream.putNextEntry(new ZipEntry("metadata.csv"));
    csvConverter.writeMetadata(entities, zipStream);

    zipStream.close();
  }
}
