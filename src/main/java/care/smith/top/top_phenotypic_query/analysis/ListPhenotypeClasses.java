package care.smith.top.top_phenotypic_query.analysis;

import java.io.File;
import java.util.List;
import java.util.Optional;
import picocli.CommandLine.Command;

@Command(
    name = "list-phenotype-classes",
    description = "List metadata of the phenotype classes in the query results.",
    mixinStandardHelpOptions = true)
public class ListPhenotypeClasses extends Analysis {

  @Override
  public Optional<List<AnalysisReport>> analyse(File queryResultFile) {
    System.out.println(loadMetadata(queryResultFile));
    return Optional.empty();
  }
}
