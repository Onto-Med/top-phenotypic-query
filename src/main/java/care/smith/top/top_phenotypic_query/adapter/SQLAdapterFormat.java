package care.smith.top.top_phenotypic_query.adapter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.simple_onto_api.util.DateUtil;

public class SQLAdapterFormat implements DataAdapterFormat {

  private static SQLAdapterFormat instance = null;

  private SQLAdapterFormat() {}

  public static SQLAdapterFormat get() {
    if (instance == null) instance = new SQLAdapterFormat();
    return instance;
  }

  @Override
  public String formatNumber(BigDecimal num) {
    return num.toPlainString();
  }

  @Override
  public String formatDateTime(OffsetDateTime date) {
    return "'" + DateUtil.format(DateUtil.convert(date)) + "'::date";
  }

  @Override
  public String formatBoolean(Boolean bool) {
    return bool.toString();
  }

  @Override
  public String formatString(String str) {
    return "'" + str + "'";
  }

  @Override
  public String formatList(Stream<String> values) {
    return values.collect(Collectors.joining(", "));
  }

  @Override
  public String formatOperator(RestrictionOperator oper) {
    return oper.getValue();
  }
}
