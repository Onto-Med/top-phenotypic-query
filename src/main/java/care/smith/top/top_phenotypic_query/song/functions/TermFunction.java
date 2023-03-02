package care.smith.top.top_phenotypic_query.song.functions;

import care.smith.top.model.ExpressionFunction.NotationEnum;

public abstract class TermFunction extends TextFunction {

  protected TermFunction(String id, String title, NotationEnum notation) {
    super(id, title, notation);
  }
}
