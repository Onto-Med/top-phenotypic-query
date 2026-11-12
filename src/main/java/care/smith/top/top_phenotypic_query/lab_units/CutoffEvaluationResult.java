package care.smith.top.top_phenotypic_query.lab_units;

import java.util.List;

/**
 * Final evaluation output containing both valid evaluated rows and rejected invalid observations.
 */
public record CutoffEvaluationResult(
    List<MatchedObservation> evaluatedObservations, List<LabObservation> invalidObservations) {}
