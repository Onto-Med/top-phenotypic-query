package care.smith.top.top_phenotypic_query.song.functions;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import care.smith.top.model.Category;
import care.smith.top.model.Entity;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.ExpressionFunction.NotationEnum;
import care.smith.top.top_phenotypic_query.song.SONG;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class SubTree extends TextFunction {

  public static final String ID = "SubTree";
  private static final NotationEnum NOTATION = NotationEnum.PREFIX;

  public static final ExpressionFunction FUNCTION =
      new ExpressionFunction()
          .id(ID)
          .title(ID)
          .notation(NOTATION)
          .minArgumentNumber(1)
          .maxArgumentNumber(1);

  private static SubTree INSTANCE = new SubTree();

  private SubTree() {
    super(ID, ID, NOTATION);
  }

  public static SubTree get() {
    return INSTANCE;
  }

  public static Expression of(Expression arg) {
    return Exp.function(ID, arg);
  }

  public static Expression of(Entity arg) {
    return of(Exp.of(arg));
  }

  public static Expression of(String phenotypeId) {
    return of(Exp.ofEntity(phenotypeId));
  }

  @Override
  public Expression generate(List<Expression> args, SONG song) {
    if (args.isEmpty()) return Exp.of("");
    Expression arg = args.get(0);
    if (arg.getEntityId() == null) return Exp.of("");

    Category concept = (Category) song.getConcept(arg.getEntityId());
    Set<String> labels = Entities.getLabels(concept, song.getLang(), true);

    Expression res = null;
    if (labels.isEmpty()) res = Exp.of("");
    else {
      List<Expression> expLabels = labels.stream().map(t -> Exp.of(t)).collect(Collectors.toList());
      res = song.getFunction(Or.ID).generate(expLabels, song);
    }

    return res;
  }
}
