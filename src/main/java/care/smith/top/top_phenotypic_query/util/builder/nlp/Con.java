package care.smith.top.top_phenotypic_query.util.builder.nlp;

import care.smith.top.model.ConceptQuery;
import care.smith.top.model.Entity;
import care.smith.top.top_phenotypic_query.song.adapter.TextAdapter;
import care.smith.top.top_phenotypic_query.song.adapter.TextAdapterConfig;
import care.smith.top.top_phenotypic_query.song.adapter.TextFinder;

public class Con {

    private TextAdapter adapter;
    private TextAdapterConfig config;
    private ConceptQuery query = new ConceptQuery();
    private Entity[] entities;

    public Con(TextAdapter adapter, TextAdapterConfig config, Entity[] entities) {
        this.adapter = adapter;
        this.config = config;
        this.entities = entities;
    }

    public static TextAdapter getAdapter(String configFilePath) throws InstantiationException {
        return TextAdapter.getInstance(getConfig(configFilePath));
    }

    public static TextAdapterConfig getConfig(String configFilePath) {
        return TextAdapterConfig.getInstance(getPath(configFilePath));
    }

    private static String getPath(String configFilePath) {
        return Thread.currentThread().getContextClassLoader().getResource(configFilePath).getPath();
    }

    public TextAdapter getAdapter() {
        return adapter;
    }

    public TextAdapterConfig getConfig() {
        return config;
    }

    public ConceptQuery getQuery() {
        return query;
    }

    public TextFinder getFinder() {
        return new TextFinder(query, entities, adapter);
    }
}
