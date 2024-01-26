package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Values;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVSubjectsDataRecord extends CSVRecordCompositeEntries {

  private static final long serialVersionUID = 1L;

  protected CSVSubjectsDataRecord(
      SubjectPhenotypes values, List<CSVSubjectsHead> header, String entryPartsDelimiter) {
    super(entryPartsDelimiter);
    addEntry(values.getSubjectId());
    for (CSVSubjectsHead head : header) addValues(values, head);
  }

  private void addValues(SubjectPhenotypes values, CSVSubjectsHead head) {
    PhenotypeValues pheVals = values.getValues(head.getId());
    if (pheVals == null) {
      addEmpty(head);
      return;
    }

    List<String> strVals = new ArrayList<>();
    List<String> strDates = new ArrayList<>();
    for (List<Value> vals : pheVals.values()) {
      for (Value val : vals) {
        if (val != null) {
          String v = Values.toStringWithoutDateTime(val);
          if (v != null) {
            strVals.add(v);
            if (head.hasDateColumn()) {
              LocalDateTime date = Values.getDateTime(val);
              String strDate = (date == null) ? "null" : DateUtil.formatDate(date);
              strDates.add(strDate);
            }
          }
        }
      }
    }

    if (strVals.isEmpty()) addEmpty(head);
    else {
      addEntry(strVals);
      if (head.hasDateColumn()) addEntry(strDates);
    }
  }

  private void addEmpty(CSVSubjectsHead head) {
    if (head.isBoolean()) addEntry("false");
    else addEntry();
    if (head.hasDateColumn()) addEntry();
  }
}
