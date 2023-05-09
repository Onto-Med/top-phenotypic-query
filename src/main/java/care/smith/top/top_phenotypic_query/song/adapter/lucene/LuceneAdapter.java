package care.smith.top.top_phenotypic_query.song.adapter.lucene;

import care.smith.top.model.Query;
import care.smith.top.top_phenotypic_query.song.adapter.Documents;
import care.smith.top.top_phenotypic_query.song.adapter.TextAdapter;
import care.smith.top.top_phenotypic_query.song.adapter.TextAdapterConfig;
import care.smith.top.top_phenotypic_query.util.Expressions;

public class LuceneAdapter extends TextAdapter {

  public LuceneAdapter(TextAdapterConfig config) {
    super(config);
  }

  public LuceneAdapter(String configFile) {
    super(configFile);
  }

  @Override
  public Documents execute(Query query) {
    String q = Expressions.getStringValue(LuceneSong.get().generate(String.valueOf(query.getId())));
    // execute query
    // return resulting documents
    return null;
  }
}
