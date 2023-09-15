package care.smith.top.top_phenotypic_query.tests.intern.run;

import care.smith.top.model.*;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResultSetRamTestIntern {

  private static final String CONFIG = "rs/config.yml";

  private static final URL QUERY_CONFIG =
      ResultSetRamTestIntern.class.getClassLoader().getResource("rs/query.json");
  private static final URL PHENOTYPE_MODEL =
      ResultSetRamTestIntern.class.getClassLoader().getResource("rs/model.json");

  // static {assertNotNull(QUERY_CONFIG);}

  // static {assertNotNull(PHENOTYPE_MODEL);}

  private final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  /*
  	@Test
  	public void resultSetTest() throws InstantiationException, SQLException, StreamReadException, DatabindException, IOException, InterruptedException {

  		DataAdapter adapter = DataAdapter.getInstanceFromResource(CONFIG);
  		PhenotypeQuery query = MAPPER.readValue(QUERY_CONFIG, PhenotypeQuery.class);
  		Entity[] entities = MAPPER.readValue(PHENOTYPE_MODEL, Entity[].class);

  		PhenotypeFinder finder = new PhenotypeFinder(query, entities, adapter);
  		final int COUNT = 5;
  		var rss = new ResultSet[COUNT];
  		System.out.print("stress testing");
  		for(int i=0;i<COUNT;i++)
  		{
  			boolean success = false;
  			do
  			{
  				try
  				{
  					rss[i] = finder.execute();
  					success = true;
  				}
  				catch (UnclassifiedServerFailureException e) {
  					System.err.println("Server failure, repeating after wait. "+e.getMessage());
  					Thread.sleep(1000);
  				}

  			} while (!success);
  			System.out.print('.');
  		}
  		System.out.println();
  		for(int i=0;i<COUNT;i++) Cli.writeResultSetToZip(rss[0], entities, new File("/tmp/rs"+i+".zip"), true);
  		adapter.close();
  		System.out.println(rss[0].size()+ " entries returned");
  	}
  */
  @Test
  void testVeryLargeResultSet() throws InterruptedException {
    Phenotype height = buildPhenotype("height", "cm");
    Phenotype weight = buildPhenotype("weight", "kg");

    /*
     * Currently olny one instance of {@link DateTimeRestriction} is used for all result set values.
     * If each value has it's one instance, memory use increases a lot. I don't really know how instances are handled by
     * top-phenotypic-query.
     */
    DateTimeRestriction dtr = buildDateTimeRestriction();
    /*
     * Each of the {@code subjectCount} generated subjects in the result set will have {@code valueCount} values of
     * height and {@code valueCount} values of weight.
     */
    int subjectCount = 100000, valueCount = 100;

    ResultSet rs = new ResultSet();
    for (int i = 0; i < subjectCount; i++) {
      for (int j = 0; j < valueCount; j++) {
        rs.addValue(String.valueOf(i), height, dtr, buildValue(j));
        rs.addValue(String.valueOf(i), weight, dtr, buildValue(j));
      }
    }
    System.gc();
    System.out.println("Sleeping 5");
    Thread.sleep(5000);
    System.out.println(
        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000_000
            + " MB used");
    Assertions.assertEquals(subjectCount, rs.size());
    Assertions.assertEquals(valueCount, rs.getValues("0", height.getId()).getValues(dtr).size());
    Assertions.assertEquals(valueCount, rs.getValues("0", weight.getId()).getValues(dtr).size());
  }

  DateTimeRestriction buildDateTimeRestriction() {
    return new DateTimeRestriction()
        .minOperator(RestrictionOperator.GREATER_THAN)
        .addValuesItem(LocalDateTime.of(2022, 1, 1, 0, 0));
  }

  Phenotype buildPhenotype(String id, String unit) {
    return (Phenotype)
        new Phenotype()
            .dataType(DataType.NUMBER)
            .unit(unit)
            .id(id)
            .entityType(EntityType.SINGLE_PHENOTYPE);
  }

  Value buildValue(int day) {
    return new NumberValue()
        .value(BigDecimal.valueOf(100.0))
        .dataType(DataType.NUMBER)
        .dateTime(LocalDateTime.of(2022, 1, 1, 0, 0).plusDays(day));
  }
}
