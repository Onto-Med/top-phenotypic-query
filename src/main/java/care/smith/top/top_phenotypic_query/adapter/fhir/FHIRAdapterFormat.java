package care.smith.top.top_phenotypic_query.adapter.fhir;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.simple_onto_api.util.DateUtil;
import care.smith.top.top_phenotypic_query.adapter.DataAdapterFormat;

public class FHIRAdapterFormat implements DataAdapterFormat {

  private static FHIRAdapterFormat instance = null;

  private FHIRAdapterFormat() {}

  public static FHIRAdapterFormat get() {
    if (instance == null) instance = new FHIRAdapterFormat();
    return instance;
  }

  @Override
  public String formatNumber(BigDecimal num) {
    return num.toPlainString();
  }

  @Override
  public String formatDateTime(LocalDateTime date) {
    return DateUtil.format(date);
  }

  @Override
  public String formatBoolean(Boolean bool) {
    return bool.toString();
  }

  @Override
  public String formatString(String str) {
    return str;
  }

  @Override
  public String formatList(Stream<String> values) {
    return values.collect(Collectors.joining(","));
  }

  @Override
  public String formatOperator(RestrictionOperator oper) {
    if (oper == RestrictionOperator.GREATER_THAN) return "=gt";
    if (oper == RestrictionOperator.GREATER_THAN_OR_EQUAL_TO) return "=ge";
    if (oper == RestrictionOperator.LESS_THAN) return "=lt";
    return "=le";
  }
}
