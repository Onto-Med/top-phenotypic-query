package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.top_phenotypic_query.command.QueryCommand;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class QueryCommandTest {

  @Test
  public void callTest() {
    QueryCommand command = new QueryCommand();
    assertEquals(2, command.call()); // no configuration
    int exitCode = new CommandLine(command).execute("-p", "phenotype1");
    assertEquals(2, exitCode); // missing required parameters
    exitCode = new CommandLine(command).execute("/these/files", "/do/not/exist");
    assertEquals(1, exitCode);
  }
}
