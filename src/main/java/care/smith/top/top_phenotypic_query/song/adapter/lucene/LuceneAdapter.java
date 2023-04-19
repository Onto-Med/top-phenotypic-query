package care.smith.top.top_phenotypic_query.song.adapter.lucene;

import care.smith.top.model.ConceptQuery;
import care.smith.top.top_phenotypic_query.song.adapter.Documents;
import care.smith.top.top_phenotypic_query.song.adapter.TextAdapter;
import care.smith.top.top_phenotypic_query.song.adapter.TextAdapterConfig;
import care.smith.top.top_phenotypic_query.util.Expressions;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

//ToDo: rename this with ElasticsearchAdapter to be more precise
public class LuceneAdapter extends TextAdapter {

    private ElasticsearchClient esClient;

    public LuceneAdapter(TextAdapterConfig config) {
        super(config);
        initConnection();
    }

    public LuceneAdapter(String configFile) {
        super(configFile);
        initConnection();
    }

    private void initConnection() {
        RestClient restClient = RestClient.builder(
                new HttpHost(
                        config.getConnectionAttribute("url"),
                        Integer.parseInt(config.getConnectionAttribute("port"))
                )).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        this.esClient = new ElasticsearchClient(transport);
    }

    @Override
    public Documents execute(ConceptQuery query) {
        String queryString = Expressions.getStringValue(LuceneSong.get().generate(query.getEntityId()));
        // execute query
        try {
            SearchResponse<Documents> search = esClient.search(
                    s -> s
                            .index(config.getConnectionAttribute("index"))
                            .query(q -> q
                                    .queryString(qs -> qs
                                            .query(queryString))),
                    Documents.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // return resulting documents
        return null;
    }
}

