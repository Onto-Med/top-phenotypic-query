package care.smith.top.top_phenotypic_query.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;

public class CSV {

  public static void write(ResultSet rs, OutputStream out, String delimiter, Charset cs) {
    OutputStreamWriter osw = new OutputStreamWriter(out, cs);
    write(
        delimiter,
        osw,
        "subject",
        "phenotype",
        "timestamp",
        "number_value",
        "text_value",
        "date_time_value",
        "boolean_value");
    for (SubjectPhenotypes sbjPhes : rs.values()) {
      for (PhenotypeValues pheVals : sbjPhes.values()) {
        for (List<Value> vals : pheVals.values()) {
          for (Value val : vals)
            write(sbjPhes.getSubjectId(), pheVals.getPhenotypeName(), val, delimiter, osw);
        }
      }
    }
    try {
      osw.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void write(String sbj, String phe, Value val, String del, OutputStreamWriter osw) {
    String num = "";
    String txt = "";
    String dt = "";
    String boo = "";
    String timestamp = (val.getDateTime() == null) ? "" : DateUtil.format(val.getDateTime());
    if (Values.hasNumberType(val)) num = Values.getNumberValue(val).toPlainString();
    else if (Values.hasStringType(val)) txt = Values.getStringValue(val);
    else if (Values.hasDateTimeType(val)) dt = DateUtil.format(Values.getDateTimeValue(val));
    else if (Values.hasBooleanType(val)) boo = Values.getBooleanValue(val).toString();
    write(del, osw, sbj, phe, timestamp, num, txt, dt, boo);
  }

  private static void write(String del, OutputStreamWriter osw, String... line) {
    try {
      osw.write(String.join(del, line) + System.lineSeparator());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
