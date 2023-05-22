package care.smith.top.top_phenotypic_query.tests.nlp;

import care.smith.top.model.Concept;
import care.smith.top.top_phenotypic_query.song.adapter.Document;
import care.smith.top.top_phenotypic_query.song.adapter.TextFinder;
import care.smith.top.top_phenotypic_query.song.functions.And;
import care.smith.top.top_phenotypic_query.song.functions.Dist;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.nlp.Cat;
import care.smith.top.top_phenotypic_query.util.builder.nlp.CQue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class TextFinderTest extends AbstractElasticTest{
    final String PARENT_CAT_ID = "phrase_search_cat";
    Concept phrase1 =
            new Cat("phrase1")
                    .titleEn("\"a document\"")
                    .get();
    Concept phrase2 =
            new Cat("phrase2")
                    .titleEn("entity")
                    .get();
    Concept parentCat =
            new Cat(PARENT_CAT_ID)
                    .titleEn("phrase_search_cat")
                    .expression(And.of(Dist.of(phrase1, 1), Exp.of(phrase2)))
                    .get();
    Entities entities = Entities.of(parentCat, phrase1, phrase2);

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
        TextFinder tf = new CQue(
                adapter,
                adapter.getConfig(),
                entities.getConcepts().toArray(new Concept[0]),
                PARENT_CAT_ID
        ).getFinder();
        List<Document> documents = tf.execute();
    }
}