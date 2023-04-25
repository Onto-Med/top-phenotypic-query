package care.smith.top.top_phenotypic_query.tests.nlp;

import care.smith.top.model.Category;
import care.smith.top.top_phenotypic_query.song.adapter.TextFinder;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.builder.nlp.Cat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
    void execute() {
        TextFinder tf = new Con()
    }
}