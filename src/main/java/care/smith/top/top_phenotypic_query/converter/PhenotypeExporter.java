package care.smith.top.top_phenotypic_query.converter;

import java.io.File;
import java.io.OutputStream;

import care.smith.top.top_phenotypic_query.util.PhenotypeList;

public interface PhenotypeExporter {

  public void write(PhenotypeList phenotypes, OutputStream stream);

  public void write(PhenotypeList phenotypes, File file);
}
