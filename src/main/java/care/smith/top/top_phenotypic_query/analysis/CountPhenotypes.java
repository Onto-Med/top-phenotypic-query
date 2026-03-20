package care.smith.top.top_phenotypic_query.analysis;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import picocli.CommandLine.Command;

@Command(
    name = "count-phenotypes",
    description = "Get the number of phenotypes in all query results.",
    mixinStandardHelpOptions = true)
public class CountPhenotypes extends Analysis {

  @Override
  public Optional<List<AnalysisReport>> analyse(File queryResultFile) {
    List<Map<String, String>> phenotypes = loadPhenotypeData(queryResultFile);
    System.out.println(phenotypes.size());
    return Optional.empty();
  }
}
