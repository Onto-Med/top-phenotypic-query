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
  obs_referencerange_type_code
)
VALUES
  -- Encounter 1: 'treatment' (1-5) UND 'normal' (10-20) -> 'normal' bevorzugen
  ('1', 'Encounter/1', 'Patient/1', '123', '2023-01-02', 15.0, 'mg/dL', 'already_reference_unit', 1.0, 'mg/dL', 5.0, 'mg/dL', 'treatment'),
  ('1', 'Encounter/1', 'Patient/1', '123', '2023-01-02', 15.0, 'mg/dL', 'already_reference_unit', 10.0, 'mg/dL', 20.0, 'mg/dL', 'normal'),

  -- Encounter 2: Zwei Referenzbereiche OHNE Referenztyp (NULL) -> ersten (5-15) wählen
  ('2', 'Encounter/2', 'Patient/1', '123', '2023-02-02', 12.0, 'mg/dL', 'converted', 8.0, 'mg/dL', 18.0, 'mg/dL', NULL),
  ('2', 'Encounter/2', 'Patient/1', '123', '2023-02-02', 12.0, 'mg/dL', 'converted', 5.0, 'mg/dL', 15.0, 'mg/dL', NULL),

  -- Encounter 3: Zwei Referenzbereiche OHNE Referenztyp als Leerstring ('') -> wie NULL behandeln, ersten (6-16) wählen
  ('3', 'Encounter/3', 'Patient/1', '123', '2023-03-02', 13.0, 'mg/dL', 'already_reference_unit', 9.0, 'mg/dL', 19.0, 'mg/dL', ''),
  ('3', 'Encounter/3', 'Patient/1', '123', '2023-03-02', 13.0, 'mg/dL', 'already_reference_unit', 6.0, 'mg/dL', 16.0, 'mg/dL', ''),

  -- Encounter 4: Referenzbereich OHNE Typ (NULL, 4-14) UND mit Typ 'normal' (11-21) -> 'normal' bevorzugen
  ('4', 'Encounter/4', 'Patient/1', '123', '2023-04-02', 16.0, 'mg/dL', 'already_reference_unit', 4.0, 'mg/dL', 14.0, 'mg/dL', NULL),
  ('4', 'Encounter/4', 'Patient/1', '123', '2023-04-02', 16.0, 'mg/dL', 'already_reference_unit', 11.0, 'mg/dL', 21.0, 'mg/dL', 'normal'),

  -- Encounter 5: NUR unpassender Referenztyp ('treatment') -> Observation bleibt erhalten, Referenzbereich NULL
  ('5', 'Encounter/5', 'Patient/1', '123', '2023-05-02', 25.0, 'mg/dL', 'already_reference_unit', 2.0, 'mg/dL', 6.0, 'mg/dL', 'treatment'),

  -- Encounter 6: Mit obs_id, komplett OHNE Referenzbereich (alle Referenzspalten NULL)
  ('6', 'Encounter/6', 'Patient/1', '123', '2023-06-02', 30.0, 'mg/dL', 'already_reference_unit', NULL, NULL, NULL, NULL, NULL),

  -- Encounter 7: Mit obs_id, komplett OHNE Referenzbereich (Leerstrings in Code-Spalten)
  ('7', 'Encounter/7', 'Patient/1', '123', '2023-07-02', 31.0, 'mg/dL', 'converted', NULL, '', NULL, '', ''),

  -- Encounter 8: OHNE obs_id (NULL) UND komplett OHNE Referenzbereich (alles NULL)
  (NULL, 'Encounter/8', 'Patient/1', '123', '2023-08-02', 32.0, 'mg/dL', 'already_reference_unit', NULL, NULL, NULL, NULL, NULL),

  -- Encounter 9: Mit Leerstring obs_id ('') UND komplett OHNE Referenzbereich (alles NULL)
  ('', 'Encounter/9', 'Patient/1', '123', '2023-09-02', 33.0, 'mg/dL', 'already_reference_unit', NULL, NULL, NULL, NULL, NULL),

  -- Encounter 10: Einseitiger Referenzbereich OHNE Typ (NULL) – nur oberer Grenzwert (high = 50.0)
  ('10', 'Encounter/10', 'Patient/1', '123', '2023-10-02', 35.0, 'mg/dL', 'already_reference_unit', NULL, NULL, 50.0, 'mg/dL', NULL),

  -- Encounter 11: Observation OHNE obs_id (NULL) UND OHNE Referenztyp (NULL), aber mit Referenzbereich
  (NULL, 'Encounter/11', 'Patient/1', '123', '2023-11-02', 7.0, 'mg/dL', 'already_reference_unit', 3.0, 'mg/dL', 9.0, 'mg/dL', NULL),

  -- Encounter 12: Ungültiger Status ('not_converted') -> darf NICHT gefunden werden
  ('12', 'Encounter/12', 'Patient/1', '123', '2023-12-02', 99.0, 'mg/dL', 'not_converted', NULL, NULL, NULL, NULL, NULL),

  -- Encounter 13: Minderjährig (17 Jahre) -> darf NICHT gefunden werden
  ('13', 'Encounter/13', 'Patient/1', '123', '2023-12-16', 14.0, 'mg/dL', 'already_reference_unit', NULL, NULL, NULL, NULL, NULL);
