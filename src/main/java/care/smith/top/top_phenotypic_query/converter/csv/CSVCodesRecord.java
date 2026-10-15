package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.util.Entities;
import java.util.List;

public class CSVCodesRecord extends CSVRecordCompositeEntries {

  private static final long serialVersionUID = 1L;

  protected static List<String> FIELDS =
      List.of("algorithm", "parameter", "type", "unit", "codesystem", "code");

  protected CSVCodesRecord(
      Phenotype alg, Phenotype par, String codesystem, String code, String entryPartsDelimiter) {
    super(entryPartsDelimiter);
    add(Entities.getDefaultTitle(alg));
    add(Entities.getDefaultTitle(par));
    addItemType(par);
    addEntry(par.getUnit());
    addEntry(codesystem);
    addEntry(code);
  }

  //  protected CSVCodesRecord(Phenotype alg, Phenotype par, String entryPartsDelimiter) {
  //    super(entryPartsDelimiter);
  //    add(Entities.getDefaultTitle(alg));
  //    add(Entities.getDefaultTitle(par));
  //    addItemType(par);
  //    addEntry(par.getUnit());
  //    addEntry(Phenotypes.getCodeUris(par));
  //  }

  private void addItemType(Phenotype phe) {
    if (phe.getItemType() == null) add("");
    else add(phe.getItemType().getValue());
  }
}
