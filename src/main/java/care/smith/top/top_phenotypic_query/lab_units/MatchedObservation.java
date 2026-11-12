package care.smith.top.top_phenotypic_query.lab_units;

import java.math.BigDecimal;

/** Holds a matched observation together with its normalized value and calculated threshold. */
public record MatchedObservation(
    LabObservation observation,
    BigDecimal convertedValue,
    String convertedUnit,
    BigDecimal threshold,
    boolean matched) {}
