package care.smith.top.top_phenotypic_query.lab_units;

import module java.base;
import module java.logging;

/**
 * Filters laboratory observations, harmonizes their units to match reference ranges, and evaluates
 * relative cutoff thresholds (ULN/LLN). Mirrors filterObservations() and the cutoff evaluation
 * block in R.
 */
public class LabCutoffEvaluator {

  private static final Logger LOGGER = Logger.getLogger(LabCutoffEvaluator.class.getName());
  private final LabUnitConverter unitConverter;

  public LabCutoffEvaluator(LabUnitConverter unitConverter) {
    this.unitConverter = unitConverter;
  }

  /** Evaluates a list of observation rows against a relative cutoff rule (ULN or LLN). */
  public CutoffEvaluationResult evaluateCutoff(
      List<LabObservation> observations, CutoffRule cutoff) {

    if (observations == null
        || observations.isEmpty()
        || cutoff == null
        || cutoff.reference() == null
        || cutoff.multiplier() == null
        || cutoff.operator() == null) {
      return new CutoffEvaluationResult(List.of(), observations != null ? observations : List.of());
    }

    List<LabObservation> invalidObs = new ArrayList<>();
    List<LabObservation> validObs = new ArrayList<>();

    // --- 1. Validate numeric value, unit validity, and presence of reference limit ---
    for (LabObservation obs : observations) {
      if (obs == null) {
        continue;
      }
      BigDecimal numericValue = parseNumericValue(obs.rawValue());
      BigDecimal referenceLimit = getReferenceValue(obs, cutoff.reference());

      if (numericValue == null
          || !unitConverter.isValidUnit(obs.unit())
          || referenceLimit == null) {
        invalidObs.add(obs);
      } else {
        validObs.add(obs);
      }
    }

    if (validObs.isEmpty()) {
      logInvalidObservationsWarning(invalidObs);
      return new CutoffEvaluationResult(List.of(), invalidObs);
    }

    // --- 2. Filter observations, resolve reference range conflicts, and convert units ---
    List<ConvertedObservation> filteredAndConverted =
        filterObservations(validObs, cutoff.reference(), invalidObs);

    if (filteredAndConverted.isEmpty()) {
      logInvalidObservationsWarning(invalidObs);
      return new CutoffEvaluationResult(List.of(), invalidObs);
    }

    // --- 3. Calculate threshold and compare converted values ---
    List<MatchedObservation> evaluatedResults = new ArrayList<>();
    for (ConvertedObservation item : filteredAndConverted) {
      BigDecimal referenceLimit = getReferenceValue(item.observation, cutoff.reference());
      BigDecimal threshold = referenceLimit.multiply(cutoff.multiplier(), MathContext.DECIMAL128);
      boolean isMatch = compareValues(item.convertedValue, threshold, cutoff.operator());

      evaluatedResults.add(
          new MatchedObservation(
              item.observation,
              item.convertedValue,
              item.convertedUnit,
              threshold.stripTrailingZeros(),
              isMatch));
    }

    logInvalidObservationsWarning(invalidObs);
    return new CutoffEvaluationResult(evaluatedResults, invalidObs);
  }

  /**
   * Mirrors filterObservations() in R: - Groups multiple reference range candidates belonging to
   * the same observation - Prioritizes referenceRangeType == "normal", falling back to null - Picks
   * the first candidate whose reference unit is convertible from the observation unit - Converts
   * observation values into the reference unit and logs unit changes
   */
  private List<ConvertedObservation> filterObservations(
      List<LabObservation> validObs,
      CutoffRule.ReferenceType referenceType,
      List<LabObservation> invalidObs) {

    // Group rows belonging to the same observation (mirroring split(obs, by = non_ref_cols))
    Map<ObservationGroupKey, List<LabObservation>> grouped =
        validObs.stream()
            .collect(
                Collectors.groupingBy(
                    obs ->
                        new ObservationGroupKey(
                            obs.observationId(), obs.loincCode(), obs.rawValue(), obs.unit()),
                    LinkedHashMap::new,
                    Collectors.toList()));

    List<LabObservation> selectedCandidates = new ArrayList<>();

    for (List<LabObservation> groupRows : grouped.values()) {
      // Prioritize "normal" reference_range_type, fallback to null
      List<LabObservation> preferred =
          groupRows.stream().filter(r -> "normal".equals(r.referenceRangeType())).toList();

      if (preferred.isEmpty()) {
        preferred =
            groupRows.stream()
                .filter(r -> r.referenceRangeType() == null || r.referenceRangeType().isBlank())
                .toList();
      }

      if (preferred.isEmpty()) {
        invalidObs.addAll(groupRows);
        continue;
      }

      String unitFrom = groupRows.get(0).unit();
      Optional<LabObservation> firstConvertible =
          preferred.stream()
              .filter(candidate ->
                  isConvertibleUnit(unitFrom, getReferenceUnit(candidate, referenceType)))
              .findFirst();

      if (firstConvertible.isPresent()) {
        LabObservation chosen = firstConvertible.get();
        selectedCandidates.add(chosen);
        // Add unselected duplicate candidates to invalidObs (matching R behavior)
        boolean skippedChosen = false;
        for (LabObservation row : groupRows) {
          if (!skippedChosen && row == chosen) {
            skippedChosen = true;
            continue;
          }
          invalidObs.add(row);
        }
      } else {
        invalidObs.addAll(groupRows);
      }
    }

    // Convert values to reference units
    List<ConvertedObservation> convertedList = new ArrayList<>();
    List<String> conversionLogDetails = new ArrayList<>();

    for (LabObservation obs : selectedCandidates) {
      BigDecimal originalVal = parseNumericValue(obs.rawValue());
      String unitFrom = obs.unit();
      String unitTarget = getReferenceUnit(obs, referenceType);

      if (unitTarget == null || unitTarget.isBlank()) {
        convertedList.add(new ConvertedObservation(obs, originalVal, unitFrom));
        continue;
      }

      BigDecimal convertedVal =
          unitConverter.convertLabUnits(
              originalVal,
              unitFrom,
              unitTarget,
              null,
              null,
              true,
              "for LOINC code " + obs.loincCode());

      if (convertedVal == null) {
        invalidObs.add(obs);
      } else {
        convertedList.add(new ConvertedObservation(obs, convertedVal, unitTarget));
        if (convertedVal.compareTo(originalVal) != 0) {
          conversionLogDetails.add(
              String.format(
                  "  code=%s, value=%s %s changed to %s %s",
                  obs.loincCode(),
                  originalVal.toPlainString(),
                  unitFrom,
                  convertedVal.toPlainString(),
                  unitTarget));
        }
      }
    }

    if (!conversionLogDetails.isEmpty()) {
      LOGGER.warning(
          "For the following observations, the unit had to be converted to match the reference range:\n"
              + String.join("\n", conversionLogDetails));
    }

    return convertedList;
  }

  // --- Helper Methods ---

  private boolean isConvertibleUnit(String unitFrom, String unitTo) {
    if (unitTo == null || unitTo.isBlank()) {
      return true;
    }
    return unitConverter.convertLabUnits(BigDecimal.ONE, unitFrom, unitTo) != null;
  }

  private BigDecimal getReferenceValue(LabObservation obs, CutoffRule.ReferenceType type) {
    return switch (type) {
      case ULN -> obs.referenceRangeHighValue();
      case LLN -> obs.referenceRangeLowValue();
    };
  }

  private String getReferenceUnit(LabObservation obs, CutoffRule.ReferenceType type) {
    return switch (type) {
      case ULN -> obs.referenceRangeHighCode();
      case LLN -> obs.referenceRangeLowCode();
    };
  }

  private BigDecimal parseNumericValue(String rawValue) {
    if (rawValue == null || rawValue.isBlank()) {
      return null;
    }
    try {
      return new BigDecimal(rawValue.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private boolean compareValues(
      BigDecimal value, BigDecimal threshold, CutoffRule.Operator operator) {
    if (value == null || threshold == null || operator == null) {
      return false;
    }
    int cmp = value.compareTo(threshold);
    return switch (operator) {
      case GREATER_THAN -> cmp > 0;
      case GREATER_THAN_OR_EQUAL -> cmp >= 0;
      case LESS_THAN -> cmp < 0;
      case LESS_THAN_OR_EQUAL -> cmp <= 0;
    };
  }

  private void logInvalidObservationsWarning(List<LabObservation> invalidObs) {
    if (!invalidObs.isEmpty()) {
      LOGGER.warning(
          "Found " + invalidObs.size() + " invalid or non-convertible observation row(s).");
    }
  }

  private record ObservationGroupKey(
      String observationId, String loincCode, String rawValue, String unit) {}

  private record ConvertedObservation(
      LabObservation observation, BigDecimal convertedValue, String convertedUnit) {}
}
