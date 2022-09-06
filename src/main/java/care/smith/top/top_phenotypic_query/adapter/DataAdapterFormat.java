package care.smith.top.top_phenotypic_query.adapter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import care.smith.top.backend.model.RestrictionOperator;

public interface DataAdapterFormat {
  public String formatNumber(BigDecimal num);

  public String formatDateTime(OffsetDateTime date);

  public String formatBoolean(Boolean bool);

  public String formatString(String str);

  public String formatList(Stream<String> values);

  public String formatOperator(RestrictionOperator oper);
}
