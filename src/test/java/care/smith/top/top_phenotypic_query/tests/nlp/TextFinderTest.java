package care.smith.top.top_phenotypic_query.tests.nlp;

import care.smith.top.model.Category;
import care.smith.top.top_phenotypic_query.song.adapter.Document;
import care.smith.top.top_phenotypic_query.song.adapter.TextFinder;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.builder.nlp.Cat;
import care.smith.top.top_phenotypic_query.util.builder.nlp.Con;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class TextFinderTest extends AbstractElasticTest{
    Category phrase1 =
            new Cat("phrase1")
                    .titleEn("\"a document\"")
                    .get();
    Entities entities = Entities.of(phrase1);

    @BeforeAll
    static void setUp() {
        setUpESIndex();
    }
    @Test
    void getEntities() {
    }

    @Test
    void execute() throws InstantiationException {
        initAdaper();
        TextFinder tf = new Con(adapter, adapter.getConfig(), entities.getCategories().toArray(new Category[0])).getFinder();
//        List<Document> documents = tf.execute();
    }
}