INSERT INTO db2dataprocessor_out.v_patient_last_version (pat_id, pat_birthdate, pat_gender)
VALUES ('1', '1990-01-01', 'male');

INSERT INTO db2dataprocessor_out.v_encounter_last_version (
  enc_id, enc_patient_ref, enc_class_code, enc_period_start,
  enc_period_end, enc_type_code, enc_age_at_admission
)
VALUES
  ('1',  'Patient/1', 'IMP', '2023-01-01', '2023-01-10', 'einrichtungskontakt', 33),
  ('2',  'Patient/1', 'IMP', '2023-02-01', '2023-02-10', 'einrichtungskontakt', 33),
  ('3',  'Patient/1', 'IMP', '2023-03-01', '2023-03-10', 'einrichtungskontakt', 33),
  ('4',  'Patient/1', 'IMP', '2023-04-01', '2023-04-10', 'einrichtungskontakt', 33),
  ('5',  'Patient/1', 'IMP', '2023-05-01', '2023-05-10', 'einrichtungskontakt', 33),
  ('6',  'Patient/1', 'IMP', '2023-06-01', '2023-06-10', 'einrichtungskontakt', 33),
  ('7',  'Patient/1', 'IMP', '2023-07-01', '2023-07-10', 'einrichtungskontakt', 33),
  ('8',  'Patient/1', 'IMP', '2023-08-01', '2023-08-10', 'einrichtungskontakt', 33),
  ('9',  'Patient/1', 'IMP', '2023-09-01', '2023-09-10', 'einrichtungskontakt', 33),
  ('10', 'Patient/1', 'IMP', '2023-10-01', '2023-10-10', 'einrichtungskontakt', 33),
  ('11', 'Patient/1', 'IMP', '2023-11-01', '2023-11-10', 'einrichtungskontakt', 33),
  ('12', 'Patient/1', 'IMP', '2023-12-01', '2023-12-10', 'einrichtungskontakt', 33),
  ('13', 'Patient/1', 'IMP', '2023-12-15', '2023-12-20', 'einrichtungskontakt', 17);

INSERT INTO db2dataprocessor_out.v_observation_last_version (
  obs_id, obs_encounter_calculated_ref, obs_patient_ref, analysis_loinc_code,
  obs_effectivedatetime, analysis_value, analysis_unit, analysis_value_status,
  obs_referencerange_low_value, obs_referencerange_low_code,
  obs_referencerange_high_value, obs_referencerange_high_code,
  obs_referencerange_type_code, obs_valuequantity_value, obs_valuequantity_code
)
VALUES
  -- Encounter 1: 'treatment' (1-5) AND 'normal' (10-20) -> prefer 'normal'
  ('1', 'Encounter/1', 'Patient/1', '123', '2023-01-02', 15.0, 'mg/dL', 'already_reference_unit', 1.0, 'mg/dL', 5.0, 'mg/dL', 'treatment', 123, 'abc'),
  ('1', 'Encounter/1', 'Patient/1', '123', '2023-01-02', 15.0, 'mg/dL', 'already_reference_unit', 10.0, 'mg/dL', 20.0, 'mg/dL', 'normal', 123, 'abc'),

  -- Encounter 2: Two reference ranges WITHOUT a reference type (NULL) -> select the first one (5–15)
  ('2', 'Encounter/2', 'Patient/1', '123', '2023-02-02', 12.0, 'mg/dL', 'converted', 8.0, 'mg/dL', 18.0, 'mg/dL', NULL, NULL, NULL),
  ('2', 'Encounter/2', 'Patient/1', '123', '2023-02-02', 12.0, 'mg/dL', 'converted', 5.0, 'mg/dL', 15.0, 'mg/dL', NULL, NULL, NULL),

  -- Encounter 3: Two reference ranges WITHOUT a reference type as an empty string (“”) -> treat as NULL; select the first one (6–16)
  ('3', 'Encounter/3', 'Patient/1', '123', '2023-03-02', 13.0, 'mg/dL', 'already_reference_unit', 9.0, 'mg/dL', 19.0, 'mg/dL', '', NULL, NULL),
  ('3', 'Encounter/3', 'Patient/1', '123', '2023-03-02', 13.0, 'mg/dL', 'already_reference_unit', 6.0, 'mg/dL', 16.0, 'mg/dL', '', NULL, NULL),

  -- Encounter 4: Reference range WITHOUT type (NULL, 4–14) AND with type “normal” (11–21) -> prefer 'normal'
  ('4', 'Encounter/4', 'Patient/1', '123', '2023-04-02', 16.0, 'mg/dL', 'already_reference_unit', 4.0, 'mg/dL', 14.0, 'mg/dL', NULL, NULL, NULL),
  ('4', 'Encounter/4', 'Patient/1', '123', '2023-04-02', 16.0, 'mg/dL', 'already_reference_unit', 11.0, 'mg/dL', 21.0, 'mg/dL', 'normal', NULL, NULL),

  -- Encounter 5: ONLY an inappropriate reference type (“treatment”) -> Observation is retained, reference range is NULL
  ('5', 'Encounter/5', 'Patient/1', '123', '2023-05-02', 25.0, 'mg/dL', 'already_reference_unit', 2.0, 'mg/dL', 6.0, 'mg/dL', 'treatment', NULL, NULL),

  -- Encounter 6: With obs_id, completely WITHOUT a reference range (all reference columns set to NULL)
  ('6', 'Encounter/6', 'Patient/1', '123', '2023-06-02', 30.0, 'mg/dL', 'already_reference_unit', NULL, NULL, NULL, NULL, NULL, NULL, NULL),

  -- Encounter 7: With obs_id, completely WITHOUT a reference range (empty strings in code columns)
  ('7', 'Encounter/7', 'Patient/1', '123', '2023-07-02', 31.0, 'mg/dL', 'converted', NULL, '', NULL, '', '', NULL, NULL),

  -- Encounter 8: WITHOUT obs_id (NULL) AND completely WITHOUT a reference range (all NULL)
  (NULL, 'Encounter/8', 'Patient/1', '123', '2023-08-02', 32.0, 'mg/dL', 'already_reference_unit', NULL, NULL, NULL, NULL, NULL, NULL, NULL),

  -- Encounter 9: With an empty string for obs_id (“”) AND completely WITHOUT a reference range (all NULL)
  ('', 'Encounter/9', 'Patient/1', '123', '2023-09-02', 33.0, 'mg/dL', 'already_reference_unit', NULL, NULL, NULL, NULL, NULL, NULL, NULL),

  -- Encounter 10: Single-sided reference range WITHOUT type (NULL) – upper limit only (high = 50.0)
  ('10', 'Encounter/10', 'Patient/1', '123', '2023-10-02', 35.0, 'mg/dL', 'already_reference_unit', NULL, NULL, 50.0, 'mg/dL', NULL, NULL, NULL),

  -- Encounter 11: Observation WITHOUT obs_id (NULL) AND WITHOUT reference type (NULL), but with a reference range
  (NULL, 'Encounter/11', 'Patient/1', '123', '2023-11-02', 7.0, 'mg/dL', 'already_reference_unit', 3.0, 'mg/dL', 9.0, 'mg/dL', NULL, NULL, NULL),

  -- Encounter 12: Invalid status (“not_converted”) -> must NOT be found
  ('12', 'Encounter/12', 'Patient/1', '123', '2023-12-02', 99.0, 'mg/dL', 'not_converted', NULL, NULL, NULL, NULL, NULL, NULL, NULL),

  -- Encounter 13: Underage (17 years old) -> must NOT be found
  ('13', 'Encounter/13', 'Patient/1', '123', '2023-12-16', 14.0, 'mg/dL', 'already_reference_unit', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
