package care.smith.top.top_phenotypic_query.song.functions.lucene;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import care.smith.top.model.Expression;
import care.smith.top.model.Value;
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

    Set<String> terms = new LinkedHashSet<>();
    for (Expression a : args) for (Value v : a.getValues()) terms.add(song.getTerm(v, a));

    if (terms.isEmpty()) return new Expression();
    return Exp.of("NOT " + terms.stream().collect(Collectors.joining(" AND NOT ")));
  }
}
