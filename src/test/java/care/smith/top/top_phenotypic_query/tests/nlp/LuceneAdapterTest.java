package care.smith.top.top_phenotypic_query.tests.nlp;

import care.smith.top.model.Category;
import care.smith.top.top_phenotypic_query.song.adapter.Document;
import care.smith.top.top_phenotypic_query.song.adapter.TextAdapter;
import care.smith.top.top_phenotypic_query.song.adapter.lucene.LuceneAdapter;
import care.smith.top.top_phenotypic_query.song.adapter.lucene.LuceneSong;
import care.smith.top.top_phenotypic_query.song.functions.And;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.nlp.Cat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LuceneAdapterTest extends AbstractElasticTest {
    Category documentEntity =
            new Cat("document")
                    .titleEn("document")
                    .get();
    Category entityEntity =
            new Cat("entity")
                    .titleEn("entity")
                    .synonymEn("entities")
                    .get();

    @BeforeAll
    static void setUp() {
        setUpESIndex();
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
    void testExecute1() throws InstantiationException {
        Entities concepts = Entities.of(documentEntity, entityEntity);
        String queryString =
                Expressions.getStringValue(LuceneSong.get().concepts(concepts).lang("en").generate(
                        And.of(documentEntity, entityEntity)
                ));
        int correctDocumentCount = 2; // "test01": has "document" & "entity"; "test02": "document" & "entities"
                                      // not "test03": has "document" but neither "entity" nor "entities" ((see setUp))

        URL configFile =
                Thread.currentThread().getContextClassLoader().getResource("config/Elastic_Adapter_Test.yml");
        assertNotNull(configFile);

        TextAdapter adapter = LuceneAdapter.getInstance(configFile.getPath());
        assertNotNull(adapter);

        List<Document> documents = adapter.execute(queryString);
        assertEquals(correctDocumentCount, documents.size());
        assertEquals(
                new HashSet<>(Arrays.asList("test01", "test02")),
                documents.stream().map(Document::getName).collect(Collectors.toSet())
        );

        // Test if "field" specification correctly excludes documents when searching on "name" field
        adapter.getConfig().setField(new String[]{"name"});
        documents = adapter.execute(queryString);
        assertNotEquals(correctDocumentCount, documents.size());
    }
}