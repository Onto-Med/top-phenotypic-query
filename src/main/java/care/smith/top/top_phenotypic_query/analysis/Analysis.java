package care.smith.top.top_phenotypic_query.analysis;

import java.io.File;
import java.util.List;

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
      arity = "1..*",
      paramLabel = "<input ZIP>",
      description = "TOP query results as ZIP files to be analysed.")
  protected List<File> inputFiles;
}
