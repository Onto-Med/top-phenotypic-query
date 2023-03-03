package care.smith.top.top_phenotypic_query.song.functions.lucene;

import care.smith.top.top_phenotypic_query.song.SONG;
import care.smith.top.top_phenotypic_query.util.Entities;

public class Lucene extends SONG {

  private static Lucene INSTANCE = new Lucene();

  public static Lucene get() {
    return INSTANCE;
  }

  private Lucene() {
    super(LuceneAnd.get(), LuceneOr.get(), LuceneNot.get(), LuceneDist.get());
  }

  public Lucene lang(String lang) {
    setLang(lang);
    return this;
  }

  public Lucene concepts(Entities concepts) {
    setConcepts(concepts);
    return this;
  }
}
