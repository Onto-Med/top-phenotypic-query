package care.smith.top.top_phenotypic_query.analysis;

import picocli.CommandLine.Command;

@Command(
    name = "list-phenotype-classes",
    description = "List metadata of the phenotype classes in the query results.",
    mixinStandardHelpOptions = true)
public class ListPhenotypeClasses extends Analysis {

  @Override
  public void run() {
    System.out.println(loadMetadata());
  }
}
