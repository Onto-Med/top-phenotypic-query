package care.smith.top.top_phenotypic_query.analysis;

import java.util.stream.Collectors;
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
                + "input files: %s\n"
                + "output file: %s\n"
                + "config file: %s",
            inputFiles.stream().map(i -> i.getAbsolutePath()).collect(Collectors.joining(";")),
            outputFile != null ? outputFile.getAbsolutePath() : null,
            configFile != null ? configFile.getAbsolutePath() : null));
  }
}
