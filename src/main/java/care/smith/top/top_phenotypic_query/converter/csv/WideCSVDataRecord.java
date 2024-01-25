package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.Values;
import java.util.ArrayList;
import java.util.List;

public class WideCSVDataRecord extends CSVRecordCompositeEntries {

  private static final long serialVersionUID = 1L;

  public WideCSVDataRecord(
      SubjectPhenotypes values, List<WideCSVHead> header, String entryPartsDelimiter) {
    super(entryPartsDelimiter);
    addEntry(values.getSubjectId());
    for (WideCSVHead head : header) addValues(values, head);
  }

  private void addValues(SubjectPhenotypes values, WideCSVHead head) {
    PhenotypeValues pheVals = values.getValues(head.getId());
    if (pheVals == null) {
      addEmpty(head);
      return;
    }

    List<String> strVals = new ArrayList<>();
    for (List<Value> vals : pheVals.values()) {
      for (Value val : vals) {
        if (val != null) {
          String v = Values.toStringWithoutFields(val);
          if (v != null) strVals.add(Values.toStringWithoutFields(val));
        }
      }
    }

    if (strVals.isEmpty()) addEmpty(head);
    else addEntry(strVals);
  }

  private void addEmpty(WideCSVHead head) {
    if (head.isBoolean()) addEntry("false");
    else addEntry();
  }
}
