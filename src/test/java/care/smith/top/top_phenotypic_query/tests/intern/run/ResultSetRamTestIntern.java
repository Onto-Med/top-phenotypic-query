package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ca.uhn.fhir.rest.server.exceptions.UnclassifiedServerFailureException;
import care.smith.top.model.Entity;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.top_phenotypic_query.Cli;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;

public class ResultSetRamTestIntern {

	private static final String CONFIG = "rs/config.yml";

	private static final URL QUERY_CONFIG = ResultSetRamTestIntern.class.getClassLoader().getResource("rs/query.json");
	static {assertNotNull(QUERY_CONFIG);}

	private static final URL PHENOTYPE_MODEL = ResultSetRamTestIntern.class.getClassLoader().getResource("rs/model.json");
	static {assertNotNull(PHENOTYPE_MODEL);}

	private final ObjectMapper MAPPER =
			new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);

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

}
