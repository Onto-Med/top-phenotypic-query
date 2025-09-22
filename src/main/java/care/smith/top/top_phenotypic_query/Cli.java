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
import java.io.*;
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

  private static boolean isValidOutputSpec(File outputFile, boolean slim) {
    String filename = outputFile.getName().toUpperCase();
    return slim && filename.endsWith(".CSV") || !slim && filename.endsWith(".ZIP");
  }

  @Override
  public Integer call() throws Exception {
    throw new ParameterException(new CommandLine(this), "Missing required subcommand.");
  }

  @Command(mixinStandardHelpOptions = true)
  int query(
      @Option(
              names = {"-f", "--force"},
              description = "Overwrite existing output file.")
          boolean force,
      @Option(
              names = {"-l", "--list"},
              description = "Print a list of patient IDs that match the query criteria to STDOUT.")
          boolean list,
      @Option(
              names = {"-n", "--count"},
              description = "Print number of patients that match the query criteria to STDOUT.")
          boolean count,
      @Option(
              names = {"-o", "--output"},
              paramLabel = "<output ZIP>",
              description =
                  "Location where resulting out file will be stored. "
                      + "The file must have a .csv (for `--slim` option) or .zip extension.")
          File outputFile,
      @Option(
              names = {"-p", "--phenotype"},
              paramLabel = "<phenotype-id>",
              description =
                  "A phenotype ID to be used as inclusion criterion to query the data source. "
                      + "To provide multiple phenotype IDs, add this option multiple times. The IDs must be present in the provided model. "
                      + "Alternatively, you can provide a query configuration file (see other positional parameter).")
          List<String> phenotypeIds,
      @Option(
              names = {"-s", "--slim"},
              description =
                  "The output will contnsist of a single CSV file. "
                      + "Consequently, the outputfile specified with `--output` must have a .csv extension.")
          boolean slim,
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
      if (outputFile != null && !isValidOutputSpec(outputFile, slim)) {
        throw new ParameterException(
            new CommandLine(this),
            "The output file must have one of the following extensions: .csv (for slim output) or .zip");
      }

      PhenotypeQuery query;
      if (queryConfigFile != null) {
        query = MAPPER.readValue(queryConfigFile, PhenotypeQuery.class);
      } else if (phenotypeIds != null && !phenotypeIds.isEmpty()) {
        query =
            new PhenotypeQuery(UUID.randomUUID(), QueryType.PHENOTYPE, "placeholder")
                .criteria(phenotypeIds.stream().map(this::idToCriterion).toList());
      } else {
        throw new ParameterException(
            new CommandLine(this),
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
        exportResultset(rs, entities, query, outputFile, slim, force);
      }

      if (list) System.out.println(rs.getSubjectIds());
      if (count) System.out.println(rs.getSubjectIds().size());
      if (!list && !count) System.out.println(rs.toString());
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }

  QueryCriterion idToCriterion(String id) {
    return new QueryCriterion(true, id, ProjectionEntry.TypeEnum.QUERY_CRITERION);
  }

  void exportResultset(
      ResultSet resultSet,
      Entity[] entities,
      PhenotypeQuery query,
      File outputFile,
      boolean slim,
      boolean force)
      throws IOException {
    if (outputFile.exists() && !force)
      throw new FileAlreadyExistsException(outputFile.getAbsolutePath());

    CSV csvConverter = new CSV();

    if (slim) {
      OutputStream outStream = new FileOutputStream(outputFile);
      csvConverter.writeSubjects(resultSet, entities, query, outStream);
    } else {
      ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(outputFile));

      zipStream.putNextEntry(new ZipEntry("data_phenotypes.csv"));
      csvConverter.writePhenotypes(resultSet, entities, zipStream);

      zipStream.putNextEntry(new ZipEntry("data_subjects.csv"));
      csvConverter.writeSubjects(resultSet, entities, query, zipStream);

      zipStream.putNextEntry(new ZipEntry("metadata.csv"));
      csvConverter.writeMetadata(entities, zipStream);

      zipStream.close();
    }
  }
}
