package care.smith.top.top_phenotypic_query.tests.nlp;

import care.smith.top.model.Category;
import care.smith.top.top_phenotypic_query.song.adapter.Document;
import care.smith.top.top_phenotypic_query.song.adapter.TextAdapter;
import care.smith.top.top_phenotypic_query.song.adapter.lucene.LuceneAdapter;
import care.smith.top.top_phenotypic_query.song.adapter.lucene.LuceneSong;
import care.smith.top.top_phenotypic_query.song.functions.And;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Cat;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LuceneAdapterTest {
    static final String ELASTIC_URL = "localhost";
    static final String ELASTIC_PORT = "9200";
    static final String[] ELASTIC_INDEX = new String[]{"test_documents"};

    ElasticsearchClient esClient;

    @BeforeEach
    void setUp() {
        // The ES index "test_documents" should be populated with the following three documents:
        //      "_id": "01", _source: {"name": "test01", "text": "What do we have here? A test document. With an entity. Nice."},
        //      "_id": "02", _source: {"name": "test02", "text": "Another document is here. It has two entities."},
        //      "_id": "03", _source: {"name": "test03", "text": "And a third document; but this one features nothing."}
        RestClient restClient = RestClient.builder(
                new HttpHost(ELASTIC_URL, Integer.parseInt(ELASTIC_PORT))).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        esClient = new ElasticsearchClient(transport);
        assertNotNull(esClient);

        try {
            SearchResponse<Document> search = esClient.search(
                    s -> s
                            .index(Arrays.asList(ELASTIC_INDEX))
                            .query(q -> q
                                    .matchAll(v -> v
                                            .queryName("matchAll"))),
                    Document.class);
            assertEquals(3, search.hits().hits().size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAdapterConnection() throws InstantiationException {
        URL configFile =
                Thread.currentThread().getContextClassLoader().getResource("config/Elastic_Adapter_Test.yml");
        assertNotNull(configFile);

        TextAdapter adapter = LuceneAdapter.getInstance(configFile.getPath());
        assertNotNull(adapter);
    }

    @Test
    void testExecute() throws InstantiationException {
        Category documentEntity = new Cat("document").titleEn("document").get();
        Category entityEntity = new Cat("entity").titleEn("entity").synonymEn("entities").get();

        Entities concepts = Entities.of(documentEntity, entityEntity);
        String queryString =
                Expressions.getStringValue(LuceneSong.get().concepts(concepts).lang("en")
                        .generate(And.of(documentEntity, entityEntity)));

        URL configFile =
                Thread.currentThread().getContextClassLoader().getResource("config/Elastic_Adapter_Test.yml");
        assertNotNull(configFile);

        TextAdapter adapter = LuceneAdapter.getInstance(configFile.getPath());
        assertNotNull(adapter);

        List<Document> documents = adapter.execute(queryString);
        assertEquals(2, documents.size());
        assertEquals(
                new HashSet<>(Arrays.asList("test01", "test02")),
                documents.stream().map(Document::getName).collect(Collectors.toSet())
        );
    }
}