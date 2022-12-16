package care.smith.top.top_phenotypic_query.converter;

import java.io.File;
import java.io.InputStream;

import care.smith.top.model.Entity;

public interface PhenotypeImporter {

  public Entity[] read(InputStream stream);

  public Entity[] read(File file);
}
