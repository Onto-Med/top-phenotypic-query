package care.smith.top.top_phenotypic_query.song.operator.lucene;

import java.util.List;

import care.smith.top.model.Expression;
import care.smith.top.top_phenotypic_query.song.SONG;

public class Or extends care.smith.top.top_phenotypic_query.song.operator.And {

  private static Or INSTANCE = new Or();

  private Or() {}

  public static Or get() {
    return INSTANCE;
  }

  @Override
  public Expression generate(List<Expression> args, SONG song) {

    return null;
  }
}
