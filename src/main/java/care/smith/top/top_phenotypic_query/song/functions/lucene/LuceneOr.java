package care.smith.top.top_phenotypic_query.song.functions.lucene;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.song.SONG;
import care.smith.top.top_phenotypic_query.song.functions.Or;

public class LuceneOr extends Or {

  private static LuceneOr INSTANCE = new LuceneOr();

  private LuceneOr() {}

  public static LuceneOr get() {
    return INSTANCE;
  }

  @Override
  public Expression generate(List<Expression> args, SONG song) {

    return null;
  }
}
