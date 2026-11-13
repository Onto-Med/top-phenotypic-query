package care.smith.top.top_phenotypic_query.lab_units;

import java.math.BigDecimal;

/** Defines a relative cutoff rule (e.g., value > 3 * ULN). */
public record CutoffRule(
    ReferenceType reference, // ULN or LLN
    BigDecimal multiplier, // e.g., 3.0
    Operator operator // >, >=, <, <=
    ) {
  public enum ReferenceType {
    ULN, // Upper Limit of Normal (referenceRangeHighValue)
    LLN // Lower Limit of Normal (referenceRangeLowValue)
  }

  public enum Operator {
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<=");

    private final String symbol;

    Operator(String symbol) {
      this.symbol = symbol;
    }

    public static Operator fromSymbol(String symbol) {
      if (symbol == null) {
        return null;
      }
      for (Operator op : values()) {
        if (op.symbol.equals(symbol.trim())) {
          return op;
        }
      }
      return null;
    }
  }
}
