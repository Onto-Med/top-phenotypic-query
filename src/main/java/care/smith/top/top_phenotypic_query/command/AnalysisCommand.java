package care.smith.top.top_phenotypic_query.command;

import care.smith.top.top_phenotypic_query.analysis.CountPhenotypes;
import care.smith.top.top_phenotypic_query.analysis.ListPhenotypeClasses;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParameterException;

@Command(
    name = "analysis",
    description = "Choose and execute analyses on TOP phenotypic query results.",
    mixinStandardHelpOptions = true,
    subcommands = {CountPhenotypes.class, ListPhenotypeClasses.class})
public class AnalysisCommand implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    throw new ParameterException(
        new CommandLine(this),
        "Please specify an analysis name as subcommand. You can list available subcommands with the"
            + " --help option.");
  }
}
