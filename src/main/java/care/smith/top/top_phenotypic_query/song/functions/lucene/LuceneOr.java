package care.smith.top.top_phenotypic_query.song.functions.lucene;

import java.util.List;
import java.util.stream.Collectors;

import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.song.SONG;
import care.smith.top.top_phenotypic_query.song.functions.Or;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class LuceneOr extends Or {

  private static LuceneOr INSTANCE = new LuceneOr();

  private LuceneOr() {}

  public static LuceneOr get() {
    return INSTANCE;
  }

  @Override
  public Expression generate(List<Expression> args, SONG song) {
    args = song.generate(args);
    if (args.isEmpty()) return Exp.of("");
    if (args.size() == 1) return args.get(0);
    String query =
        args.stream().map(a -> Expressions.getStringValue(a)).collect(Collectors.joining(" OR "));
    return Exp.of("(" + query + ")");
  }
}
