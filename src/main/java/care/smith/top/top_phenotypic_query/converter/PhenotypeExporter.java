package care.smith.top.top_phenotypic_query.converter;

import java.io.File;
import java.io.OutputStream;

import care.smith.top.model.Entity;
import care.smith.top.model.Repository;

public interface PhenotypeExporter {

  public void write(Entity[] entities, Repository repo, String uri, OutputStream stream);

  public void write(Entity[] entities, Repository repo, String uri, File file);

  public String getFileExtension();
}
