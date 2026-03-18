package care.smith.top.top_phenotypic_query.analysis;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public abstract class Analysis implements Runnable {
  @Option(
      names = {"-c", "--config"},
      paramLabel = "<config YAML>",
      description = "Specify analysis parameters via a YAML configuration file.")
  protected File configFile;

  @Option(
      names = {"-o", "--output"},
      paramLabel = "<output ZIP>",
      description =
          "Location where resulting out file will be stored. "
              + "The file must have a .zip extension.")
  protected File outputFile;

  @Parameters(
      index = "0",
      arity = "1..*",
      paramLabel = "<input ZIP>",
      description = "TOP query results as ZIP files to be analysed.")
  protected List<File> inputFiles;

  protected List<Map<String, String>> loadMetadata() {
    return loadCsv("metadata.csv");
  }

  protected List<Map<String, String>> loadSubjectData() {
    return loadCsv("data_subjects.csv");
  }

  protected List<Map<String, String>> loadPhenotypeData() {
    return loadCsv("data_phenotypes.csv");
  }

  private List<Map<String, String>> loadCsv(@NotBlank String entryName) {
    List<Map<String, String>> records = new ArrayList<>();

    for (File f : inputFiles) {
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
    }

    return records;
  }
}
