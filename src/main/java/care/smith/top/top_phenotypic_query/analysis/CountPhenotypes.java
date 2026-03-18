package care.smith.top.top_phenotypic_query.analysis;

import java.util.List;
import java.util.Map;
import picocli.CommandLine.Command;

@Command(
    name = "count-phenotypes",
    description = "Get the number of phenotypes in all query results.",
    mixinStandardHelpOptions = true)
public class CountPhenotypes extends Analysis {

  @Override
  public void run() {
    List<Map<String, String>> phenotypes = loadPhenotypeData();
    System.out.println(phenotypes.size());
  }
}
