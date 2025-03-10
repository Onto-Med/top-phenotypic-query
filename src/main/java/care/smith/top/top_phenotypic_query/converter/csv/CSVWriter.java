package care.smith.top.top_phenotypic_query.converter.csv;

import com.google.common.base.Strings;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class CSVWriter {

  private OutputStreamWriter writer;
  private String entriesDelimiter;

  protected CSVWriter(OutputStream out, String entriesDelimiter, Charset charset) {
    if ("|".equals(entriesDelimiter.trim()))
      throw new IllegalArgumentException("The pipe symbol '|' must not be used as delimiter!");
    this.entriesDelimiter = entriesDelimiter;
    this.writer = new OutputStreamWriter(out, charset);
  }

  protected void write(List<String> record) {
    try {
      writer.write(
          record.stream().map(this::escapeCsvField).collect(Collectors.joining(entriesDelimiter))
              + System.lineSeparator());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void flush() {
    try {
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected String escapeCsvField(String field) {
    String escapedData = Strings.nullToEmpty(field).replaceAll("\\R", " ");
    if (escapedData.contains(entriesDelimiter)
        || escapedData.contains("\"")
        || escapedData.contains("'")) {
      escapedData = escapedData.replace("\"", "\"\"");
      escapedData = "\"" + escapedData + "\"";
    }
    return escapedData;
  }
}
