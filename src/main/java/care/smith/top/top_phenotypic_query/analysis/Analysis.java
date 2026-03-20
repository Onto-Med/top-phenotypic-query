package care.smith.top.top_phenotypic_query.analysis;

import care.smith.top.model.DataType;
import care.smith.top.model.EntityType;
import care.smith.top.model.Phenotype;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public abstract class Analysis implements Runnable {

  private Logger log = LoggerFactory.getLogger(Analysis.class);

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
   * Iterates over all provided query results and calls {@link #analyse(List, List, List)} for each
   * of them.
   *
   * <p>The resulting reports are written to the provided {@link #outputFile}, if any. Otherwise,
   * they are written to STDOUT.
   */
  @Override
  public void run() {
    ListIterator<File> iterator = inputFiles.listIterator();

    while (iterator.hasNext()) {
      int index = iterator.nextIndex();
      File file = iterator.next();
      Optional<List<AnalysisReport>> reports =
          analyse(index).map(o -> o.stream().map(r -> r.model(file.getName())).toList());

      if (reports.isPresent()) {
        if (outputFile.isPresent()) {
          try {
            writeReports(outputFile.get(), reports.get());
          } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
            e.printStackTrace();
          }
        } else {
          System.out.println(reports);
        }
      }
    }
  }

  /**
   * This method is executed for each query result. Override it with a specific analysis.
   *
   * @param index The index of the query result ZIP file to be analysed.
   * @return An {@link Optional} that may contain analysis results.
   */
  protected abstract Optional<List<AnalysisReport>> analyse(int index);

  /**
   * Parse analysis configuration from the provided YAML file ({@link #configFile}) to the specified
   * type.
   *
   * @param <T> Java class the YAML will be parsed to (e.g., {@link AnalysisSpec}).
   * @return Optional that may contain a map, where keys are algorithm IDs and values are of type
   *     {@code T}.
   * @throws IOException If the YAML file could not be read.
   */
  protected <T> Optional<Map<String, List<T>>> loadConfiguration() throws IOException {
    if (configFile.isEmpty()) {
      log.trace("No configuration was provided.");
      return Optional.empty();
    }

    File file = configFile.get();
    if (!file.canRead())
      throw new IOException(String.format("Cannot access configuration at '%s'!", file));

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return Optional.of(mapper.readValue(file, new TypeReference<Map<String, List<T>>>() {}));
  }

  /**
   * Load metadata from a {@code metadata.csv} file in a query result ZIP file, denoted by {@code
   * index}.
   *
   * @param index The index of the query result for which the metadata are requested.
   * @return List of {@link Phenotype}.
   */
  protected List<Phenotype> loadMetadata(int index) {
    return loadCsv(index, "metadata.csv").stream()
        .map(
            r ->
                new Phenotype(EntityType.fromValue(r.get("type")))
                    .id(r.get("phenotype"))
                    .dataType(DataType.fromValue(r.get("datatype")))
                    .unit(r.get("unit")))
        .toList();
  }

  /**
   * Load subject data from a {@code data_subjects.csv} file in a query result ZIP file, denoted by
   * {@code index}.
   *
   * @param index The index of the query result for which the subject data are requested.
   * @return List of String maps.
   */
  protected List<Map<String, String>> loadSubjectData(int index) {
    return loadCsv(index, "data_subjects.csv");
  }

  /**
   * Load subject data from a {@code data_phenotypes.csv} file in a query result ZIP file, denoted
   * by {@code index}.
   *
   * @param index The index of the query result for which the phenotype data are requested.
   * @return List of String maps.
   */
  protected List<Map<String, String>> loadPhenotypeData(int index) {
    return loadCsv(index, "data_phenotypes.csv");
  }

  protected void writeReports(@NotNull File csvFile, List<AnalysisReport> reports)
      throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
    try (Writer writer = new FileWriter(csvFile)) {
      StatefulBeanToCsv<AnalysisReport> csvWriter =
          new StatefulBeanToCsvBuilder<AnalysisReport>(writer).build();
      csvWriter.write(reports);
    }
  }

  private List<Map<String, String>> loadCsv(int index, @NotBlank String entryName)
      throws ArrayIndexOutOfBoundsException {
    List<Map<String, String>> records = new ArrayList<>();

    File f = inputFiles.get(index);
    try (ZipFile zipFile = new ZipFile(f)) {
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
