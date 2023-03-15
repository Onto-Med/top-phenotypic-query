package care.smith.top.top_phenotypic_query.h2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {

  private String name;
  private Field pk;
  private Field[] fields;
  private Field[] insertFields;
  private List<String> values = new ArrayList<>();

  public Table(String name, Field pk, Field... fields) {
    this.name = name;
    this.pk = pk;
    this.fields = fields;
  }

  public String getName() {
    return name;
  }

  public Field getPk() {
    return pk;
  }

  public Table insertFields(Field... fields) {
    this.insertFields = fields;
    return this;
  }

  public String getCreateStatement() {
    String stmt = "CREATE TABLE " + name + " (" + System.lineSeparator();
    stmt +=
        Stream.of(fields)
            .map(Field::toString)
            .collect(Collectors.joining("," + System.lineSeparator()));
    stmt += "," + System.lineSeparator() + "PRIMARY KEY (" + pk.getName() + ")";
    stmt += "," + System.lineSeparator() + ")";
    return stmt;
  }

  public Table values(Value... data) {
    values.add(Stream.of(data).map(Value::toString).collect(Collectors.joining(", ")));
    return this;
  }

  public String getInsertStatement() {
    String stmt;
    if (insertFields.length == 0) stmt = "INSERT INTO " + name + " VALUES";
    else
      stmt =
          "INSERT INTO "
              + name
              + " ("
              + Stream.of(insertFields).map(Field::getName).collect(Collectors.joining(", "))
              + ") VALUES";

    return stmt + System.lineSeparator() + String.join("," + System.lineSeparator(), values);
  }
}
