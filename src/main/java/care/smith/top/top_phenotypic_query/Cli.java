package care.smith.top.top_phenotypic_query;

import module java.base;

import care.smith.top.top_phenotypic_query.command.AnalysisCommand;
import care.smith.top.top_phenotypic_query.command.QueryCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParameterException;

@Command(
    name = "top-phenotypic-query",
    mixinStandardHelpOptions = true,
    description = "Perform phenotypic queries and analyses on data sources.",
    synopsisSubcommandLabel = "COMMAND",
    subcommands = {QueryCommand.class, AnalysisCommand.class})
public class Cli implements Callable<Integer> {

  void main(String... args) {
    int exitCode = new CommandLine(new Cli()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() {
    throw new ParameterException(new CommandLine(this), "Missing required subcommand.");
  }
}
