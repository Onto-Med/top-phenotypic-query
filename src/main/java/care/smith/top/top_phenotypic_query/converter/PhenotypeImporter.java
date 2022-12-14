package care.smith.top.top_phenotypic_query.converter;

import java.io.File;
import java.io.InputStream;

import care.smith.top.top_phenotypic_query.util.PhenotypeList;

public interface PhenotypeImporter {

  public PhenotypeList read(InputStream stream);

  public PhenotypeList read(File file);
}
