package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.util.DateUtil;
import care.smith.top.top_phenotypic_query.util.Values;
import java.util.List;

public class PhenotypesCSVDataRecord extends CSVRecord {

  private static final long serialVersionUID = 1L;

  public static List<String> FIELDS =
      List.of(
          "subject",
          "phenotype",
          "title",
          "date_time",
          "start_date_time",
          "end_date_time",
          "number_value",
          "string_value",
          "date_time_value",
          "boolean_value");

  public PhenotypesCSVDataRecord(String sbj, String phe, String title, Value val) {
    addEntry(sbj);
    addEntry(phe);
    addEntry(title);
    addValueTimestamp(val);
    addNumberValue(val);
    addStringValue(val);
    addDateTimeValue(val);
    addBooleanValue(val);
  }

  private void addValueTimestamp(Value val) {
    if (val == null || val.getDateTime() == null) add("");
    else add(DateUtil.format(val.getDateTime()));

    if (val == null || val.getStartDateTime() == null) add("");
    else add(DateUtil.format(val.getStartDateTime()));

    if (val == null || val.getEndDateTime() == null) add("");
    else add(DateUtil.format(val.getEndDateTime()));
  }

  private void addNumberValue(Value val) {
    if (val == null || !Values.hasNumberType(val)) add("");
    else add(Values.getNumberValue(val).toPlainString());
  }

  private void addStringValue(Value val) {
    if (val == null || !Values.hasStringType(val)) add("");
    else add(Values.getStringValue(val));
  }

  private void addDateTimeValue(Value val) {
    if (val == null || !Values.hasDateTimeType(val)) add("");
    else add(DateUtil.format(Values.getDateTimeValue(val)));
  }

  private void addBooleanValue(Value val) {
    if (val == null || !Values.hasBooleanType(val)) add("");
    else add(Values.getBooleanValue(val).toString());
  }
}
