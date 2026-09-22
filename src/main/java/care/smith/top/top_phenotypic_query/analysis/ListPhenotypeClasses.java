package care.smith.top.top_phenotypic_query.analysis;

import module java.base;

import com.opencsv.CSVWriter;
import picocli.CommandLine.Command;

@Command(
    name = "list-phenotype-classes",
    description = "List metadata of the phenotype classes in the query results.",
    mixinStandardHelpOptions = true)
public class ListPhenotypeClasses extends Analysis {

  @Override
  public Optional<List<AnalysisReport>> analyse(File queryResultFile) {
    if (outputFile.isPresent()) {
      try (CSVWriter writer = new CSVWriter(new FileWriter(outputFile.get()))) {
        loadMetadata(queryResultFile).stream()
            .map(
                phe ->
                    new String[] {
                      phe.getId(),
                      Objects.requireNonNullElse(phe.getEntityType(), "").toString(),
                      Objects.requireNonNullElse(phe.getDataType(), "").toString(),
                      phe.getUnit()
                    })
            .forEach(line -> writer.writeNext(line));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println(loadMetadata(queryResultFile));
    }
    return Optional.empty();
  }
}
