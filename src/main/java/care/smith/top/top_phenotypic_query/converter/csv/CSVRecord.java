package care.smith.top.top_phenotypic_query.converter.csv;

import java.util.ArrayList;

public class CSVRecord extends ArrayList<String> {

  private static final long serialVersionUID = 1L;

  protected void addEntry(String entry) {
    if (entry == null) add("");
    else add(entry);
  }

  protected void addEntry() {
    add("");
  }
}
