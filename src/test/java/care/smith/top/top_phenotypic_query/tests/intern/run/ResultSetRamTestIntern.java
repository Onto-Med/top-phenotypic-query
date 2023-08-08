package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import care.smith.top.model.Entity;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.top_phenotypic_query.adapter.DataAdapter;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.search.PhenotypeFinder;

public class ResultSetRamTestIntern {

	private static final String CONFIG = "rs/mii_fhir_testserver.yml";

	private static final URL QUERY_CONFIG = ResultSetRamTestIntern.class.getClassLoader().getResource("rs/top_bmi_query.json");
	static {assertNotNull(QUERY_CONFIG);}

	private static final URL PHENOTYPE_MODEL = ResultSetRamTestIntern.class.getClassLoader().getResource("rs/bmi_example.json");
	static {assertNotNull(PHENOTYPE_MODEL);}

	private final ObjectMapper MAPPER =
			new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);

	@Test
	public void resultSetTest() throws InstantiationException, SQLException, StreamReadException, DatabindException, IOException {

		DataAdapter adapter = DataAdapter.getInstanceFromResource(CONFIG);
		PhenotypeQuery query = MAPPER.readValue(QUERY_CONFIG, PhenotypeQuery.class);
		Entity[] entities = MAPPER.readValue(PHENOTYPE_MODEL, Entity[].class);

		PhenotypeFinder finder = new PhenotypeFinder(query, entities, adapter);
		ResultSet rs = finder.execute();

		adapter.close();

	}

}
