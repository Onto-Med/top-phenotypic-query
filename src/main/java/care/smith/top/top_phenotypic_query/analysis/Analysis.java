package care.smith.top.top_phenotypic_query.analysis;

import java.io.File;
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
      paramLabel = "<input ZIP>",
      description = "TOP query result as ZIP file to be analysed.")
  protected File inputFile;
}
