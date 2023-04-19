package care.smith.top.top_phenotypic_query.tests.nlp;

import care.smith.top.top_phenotypic_query.song.adapter.Documents;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LuceneAdapterTest {
    static final String ELASTIC_URL = "localhost";
    static final String ELASTIC_PORT = "9200";
    static final String[] ELASTIC_INDEX = new String[]{"test_documents"};

    ElasticsearchClient esClient;

    @BeforeEach
    void setUp() {
        // The ES index "test_documents" should be populated with the following three documents:
        //      {"id": "01", "name": "test01", "text": "What do we have here? A test document. With an entity. Nice."},
        //      {"id": "02", "name": "test02", "text": "Another document is here. It has two entities."},
        //      {"id": "03", "name": "test03", "text": "And a third document; but this one features nothing."}
        RestClient restClient = RestClient.builder(
                new HttpHost(ELASTIC_URL, Integer.parseInt(ELASTIC_PORT))).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        esClient = new ElasticsearchClient(transport);
        assertNotNull(esClient);

        try {
            SearchResponse<Documents> search = esClient.search(
                    s -> s
                            .index(Arrays.stream(ELASTIC_INDEX).findFirst().get())
                            .query(q -> q
                                    .matchAll(v -> v
                                            .queryName("matchAll"))),
                    Documents.class);
            assertEquals(3, search.hits().hits().size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAdapterConnection() {
        URL configFile =
                Thread.currentThread().getContextClassLoader().getResource("config/Elastic_Adapter_Test.yml");
        assertNotNull(configFile);
    }
}