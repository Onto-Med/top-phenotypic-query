package care.smith.top.top_phenotypic_query.song.functions.lucene;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.song.SONG;
import care.smith.top.top_phenotypic_query.song.functions.Not;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class LuceneNot extends Not {

  private static LuceneNot INSTANCE = new LuceneNot();

  private LuceneNot() {}

  public static LuceneNot get() {
    return INSTANCE;
  }

  @Override
  public Expression generate(List<Expression> args, SONG song) {
    args = song.generate(args);
    if (args.isEmpty()) return new Expression();
    String query = song.getQuery(args.get(0));
    return Exp.of("NOT " + query).type(SONG.EXPRESSION_TYPE_QUERY);
  }
}
