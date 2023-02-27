package care.smith.top.top_phenotypic_query.song.functions.lucene;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.song.SONG;
import care.smith.top.top_phenotypic_query.song.functions.Not;

public class LuceneNot extends Not {

  private static LuceneNot INSTANCE = new LuceneNot();

  private LuceneNot() {}

  public static LuceneNot get() {
    return INSTANCE;
  }

  @Override
  public Expression generate(List<Expression> args, SONG song) {

    return null;
  }
}
