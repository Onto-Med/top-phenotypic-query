package care.smith.top.top_phenotypic_query.song.adapter;

import care.smith.top.model.Entity;
import care.smith.top.top_phenotypic_query.util.Entities;

public class TextFinder {

  private TextQuery query;
  private Entities entities;
  private TextAdapter adapter;

  public TextFinder(TextQuery query, Entity[] entities, TextAdapter adapter) {
    this.query = query;
    this.adapter = adapter;
    this.entities = Entities.of(entities);
  }

  public Entities getEntities() {
    return entities;
  }

  public Documents execute() {
    return adapter.execute(query);
  }
}
