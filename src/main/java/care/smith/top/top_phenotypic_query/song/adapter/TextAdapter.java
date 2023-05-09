package care.smith.top.top_phenotypic_query.song.adapter;

import java.lang.reflect.InvocationTargetException;

import care.smith.top.model.Query;
import care.smith.top.top_phenotypic_query.data_adapter.config.DataAdapterConfig;

public abstract class TextAdapter {

  protected TextAdapterConfig config;

  protected TextAdapter(TextAdapterConfig config) {
    this.config = config;
  }

  protected TextAdapter(String configFile) {
    this(TextAdapterConfig.getInstance(configFile));
  }

  public static TextAdapter getInstance(TextAdapterConfig config) throws InstantiationException {
    TextAdapter adapter;
    try {
      Class<?> adapterClass = Class.forName(config.getAdapter());
      adapter =
          (TextAdapter) adapterClass.getConstructor(DataAdapterConfig.class).newInstance(config);
    } catch (ClassNotFoundException
        | InvocationTargetException
        | IllegalAccessException
        | NoSuchMethodException
        | ClassCastException e) {
      e.printStackTrace();
      throw new InstantiationException("Could not instantiate adapter for provided configuration.");
    }
    return adapter;
  }

  public static TextAdapter getInstance(String configFile) throws InstantiationException {
    return getInstance(TextAdapterConfig.getInstance(configFile));
  }

  public TextAdapterConfig getConfig() {
    return config;
  }

  // generate and execute query
  public abstract Documents execute(Query query);
}
