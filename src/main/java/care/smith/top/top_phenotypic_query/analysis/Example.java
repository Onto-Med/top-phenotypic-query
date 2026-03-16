package care.smith.top.top_phenotypic_query.analysis;

import picocli.CommandLine.Command;

@Command(
    name = "example",
    description = "Description of this example analysis.",
    mixinStandardHelpOptions = true)
public class Example extends Analysis {

  @Override
  public void run() {
    System.out.println(
        String.format(
            "I have been called with the following parameters:\n"
                + "input file: %s\n"
                + "output file: %s\n"
                + "config file: %s",
            inputFile.getAbsolutePath(),
            outputFile != null ? outputFile.getAbsolutePath() : null,
            configFile != null ? configFile.getAbsolutePath() : null));
  }
}
