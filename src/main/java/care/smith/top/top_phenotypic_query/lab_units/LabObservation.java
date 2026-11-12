package care.smith.top.top_phenotypic_query.lab_units;

import java.math.BigDecimal;

/**
 * Represents a single laboratory observation row (equivalent to a row in observation_resources).
 */
public record LabObservation(
    String observationId,
    String loincCode,
    String rawValue, // String so non-numeric values can be caught and logged
    String unit,
    String referenceRangeType, // e.g., "normal", "therapeutic", or null
    BigDecimal referenceRangeLowValue,
    String referenceRangeLowCode,
    BigDecimal referenceRangeHighValue,
    String referenceRangeHighCode) {}
