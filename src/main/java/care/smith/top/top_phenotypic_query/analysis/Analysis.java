package care.smith.top.top_phenotypic_query.analysis;

import care.smith.top.model.DataType;
import care.smith.top.model.EntityType;
import care.smith.top.model.Phenotype;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public abstract class Analysis implements Runnable {

  protected Logger log = LoggerFactory.getLogger(Analysis.class);

  @Option(
      names = {"-c", "--config"},
      paramLabel = "<config YAML>",
      description = "Specify analysis parameters via a YAML configuration file.")
  protected Optional<File> configFile;

  @Option(
      names = {"-o", "--output"},
      paramLabel = "<output ZIP>",
      description =
          "Location where resulting out file will be stored. "
              + "The file should have a .csv extension.")
  protected Optional<File> outputFile;

  @Parameters(
      index = "0",
      arity = "1..*",
      paramLabel = "<input ZIP>",
      description = "TOP query results as ZIP files to be analysed.")
  protected List<File> inputFiles;

  /**
   * Iterates over all provided query results and calls {@link #analyse(File)} for each of them.
   *
   * <p>The resulting reports are written to the provided {@link #outputFile}, if any. Otherwise,
   * they are written to STDOUT.
   *
   * <p>If {@link #analyse(File)} yielded no results ({@link Optional.empty}), nothing is written to
   * {@link #outputFile} and STDOUT.
   */
  @Override
  public void run() {

    Stream<Optional<List<AnalysisReport>>> results =
        inputFiles.stream()
            .map(f -> analyse(f).map(l -> l.stream().map(r -> r.model(f.getName())).toList()));

    if (results.anyMatch(Optional::isPresent)) {
      List<AnalysisReport> reports =
          results.flatMap(Optional::stream).flatMap(List::stream).toList();

      if (outputFile.isPresent()) {
        try {
          writeReports(outputFile.get(), reports);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println(reports);
      }
    }
  }

  /**
   * This method is executed for each query result. Override it with a specific analysis.
   *
   * <p>Let your implementation return {@link Optional.empty} to prevent any output. This is
   * helpful, if you want to handle the output in a special way.
   *
   * @param queryResultFile The query result ZIP file to be analysed.
   * @return An {@link Optional} that may contain analysis results.
   */
  protected abstract Optional<List<AnalysisReport>> analyse(File queryResultFile);

  /**
   * Parse analysis configuration from the provided YAML file ({@link #configFile}) to the specified
   * type.
   *
   * @param <T> Java class the YAML will be parsed to (e.g., {@link AnalysisSpec}).
   * @return Optional that may contain an object of type {@code T}.
   * @throws IOException If the YAML file could not be read.
   */
  protected <T> Optional<T> loadConfiguration(Class<T> cls) throws IOException {
    if (configFile.isEmpty()) {
      log.trace("No configuration was provided.");
      return Optional.empty();
    }

    File file = configFile.get();
    if (!file.canRead())
      throw new IOException(String.format("Cannot access configuration at '%s'!", file));

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return Optional.of(mapper.readValue(file, cls));
  }

  /**
   * Load metadata from a {@code metadata.csv} file in a query result ZIP file.
   *
   * @param queryResultFile The query result file for which the metadata are requested.
   * @return List of {@link Phenotype}.
   */
  protected List<Phenotype> loadMetadata(File queryResultFile) {
    return getMetadataStream(queryResultFile).toList();
  }

  /**
   * Load metadata from a {@code metadata.csv} file in a query result ZIP file to a map with
   * phenotype ids as keys.
   *
   * @param queryResultFile The query result file for which the metadata are requested.
   * @return List of {@link Phenotype}.
   */
  protected Map<String, Phenotype> loadMetadataToMap(File queryResultFile) {
    return getMetadataStream(queryResultFile)
        .collect(Collectors.toMap(Phenotype::getId, Function.identity()));
  }

  private Stream<Phenotype> getMetadataStream(File queryResultFile) {
    return loadCsv(queryResultFile, "metadata.csv").stream()
        .map(
            r ->
                new Phenotype(EntityType.fromValue(r.get("type")))
                    .id(r.get("phenotype"))
                    .dataType(DataType.fromValue(r.get("datatype")))
                    .unit(r.get("unit")));
  }

  /**
   * Load subject data from a {@code data_subjects.csv} file in a query result ZIP file.
   *
   * @param queryResultFile The query result file for which the subject data are requested.
   * @return List of String maps.
   */
  protected List<Map<String, String>> loadSubjectData(File queryResultFile) {
    return loadCsv(queryResultFile, "data_subjects.csv");
  }

  /**
   * Load subject data from a {@code data_phenotypes.csv} file in a query result ZIP file.
   *
   * @param queryResultFile The query result file for which the phenotype data are requested.
   * @return List of String maps.
   */
  protected List<Map<String, String>> loadPhenotypeData(File queryResultFile) {
    return loadCsv(queryResultFile, "data_phenotypes.csv");
  }

  /**
   * Load subject data from a {@code data_phenotypes.csv} file in a query result ZIP file.
   *
   * @param queryResultFile The query result file for which the phenotype data are requested.
   * @return Multimap with subject ids als keys and phenotype records as values.
   */
  protected Multimap<String, PhenotypeRecord> loadPhenotypeDataOfSubjects(File queryResultFile) {
    Multimap<String, PhenotypeRecord> data = ArrayListMultimap.create();
    loadPhenotypeData(queryResultFile)
        .forEach(r -> data.put(r.get("subject"), new PhenotypeRecord(r)));
    return data;
  }

  protected void writeReports(@NotNull File csvFile, List<AnalysisReport> reports)
      throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
    try (Writer writer = new FileWriter(csvFile)) {
      StatefulBeanToCsv<AnalysisReport> csvWriter =
          new StatefulBeanToCsvBuilder<AnalysisReport>(writer).build();
      csvWriter.write(reports);
    }
  }

  private List<Map<String, String>> loadCsv(File queryResultFile, @NotBlank String entryName)
      throws ArrayIndexOutOfBoundsException {
    List<Map<String, String>> records = new ArrayList<>();

    try (ZipFile zipFile = new ZipFile(queryResultFile)) {
      ZipEntry entry = zipFile.getEntry(entryName);
      InputStream stream = zipFile.getInputStream(entry);
      CSVReaderHeaderAwareBuilder builder =
          new CSVReaderHeaderAwareBuilder(new InputStreamReader(stream, StandardCharsets.UTF_8))
              .withCSVParser(new CSVParserBuilder().withSeparator(';').build());
      try (CSVReaderHeaderAware reader = builder.build()) {
        Map<String, String> values;
        while ((values = reader.readMap()) != null) {
          records.add(values);
        }
      } catch (CsvValidationException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return records;
  }
}
