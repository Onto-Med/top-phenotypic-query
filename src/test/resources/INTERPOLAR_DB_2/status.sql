INSERT INTO db2dataprocessor_out.v_patient_last_version (pat_id, pat_birthdate, pat_gender)
VALUES ('1', '2001-01-01', 'male');

-- Encounter 1-4: valid statuses ('finished', 'in-progress', 'triaged', 'onleave')
-- Encounter 5: NULL status (must remain included)
-- Encounters 6-9: excluded statuses ('planned', 'cancelled', 'entered-in-error', 'unknown')
INSERT INTO db2dataprocessor_out.v_encounter_last_version (enc_id, enc_patient_ref, enc_class_code, enc_period_start,
                                                         enc_period_end, enc_type_code, enc_age_at_admission, enc_status)
VALUES ('1', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, 'finished'),
       ('2', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, 'in-progress'),
       ('3', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, 'triaged'),
       ('4', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, 'onleave'),
       ('5', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, null),
       ('6', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, 'planned'),
       ('7', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, 'cancelled'),
       ('8', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, 'entered-in-error'),
       ('9', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18, 'unknown');

-- Observation (with obs_id):
-- 1-5: valid statuses ('final', 'preliminary', 'amended', 'corrected', null)
-- 6-9: excluded statuses ('on-hold', 'registered', 'cancelled', 'entered-in-error')
-- 10: valid status, but excluded encounter ('Encounter/6')
-- Observation (without obs_id - NULL or empty string):
-- 36-37: valid statuses ('final', null) without obs_id
-- 96-97: excluded statuses ('cancelled') or excluded encounter ('Encounter/6') without obs_id
INSERT INTO db2dataprocessor_out.v_observation_last_version (obs_id, obs_encounter_calculated_ref, obs_patient_ref,
                                                           analysis_loinc_code, obs_effectivedatetime, analysis_value,
                                                           analysis_value_status, obs_status)
VALUES ('1', 'Encounter/1', 'Patient/1', '123', null, 31, 'already_reference_unit', 'final'),
       ('2', 'Encounter/1', 'Patient/1', '123', null, 32, 'already_reference_unit', 'preliminary'),
       ('3', 'Encounter/1', 'Patient/1', '123', null, 33, 'already_reference_unit', 'amended'),
       ('4', 'Encounter/1', 'Patient/1', '123', null, 34, 'already_reference_unit', 'corrected'),
       ('5', 'Encounter/1', 'Patient/1', '123', null, 35, 'already_reference_unit', null),
       (null, 'Encounter/1', 'Patient/1', '123', null, 36, 'already_reference_unit', 'final'),
       ('', 'Encounter/1', 'Patient/1', '123', null, 37, 'already_reference_unit', null),
       ('6', 'Encounter/1', 'Patient/1', '123', null, 91, 'already_reference_unit', 'on-hold'),
       ('7', 'Encounter/1', 'Patient/1', '123', null, 92, 'already_reference_unit', 'registered'),
       ('8', 'Encounter/1', 'Patient/1', '123', null, 93, 'already_reference_unit', 'cancelled'),
       ('9', 'Encounter/1', 'Patient/1', '123', null, 94, 'already_reference_unit', 'entered-in-error'),
       ('10', 'Encounter/6', 'Patient/1', '123', null, 95, 'already_reference_unit', 'final'),
       (null, 'Encounter/1', 'Patient/1', '123', null, 96, 'already_reference_unit', 'cancelled'),
       ('', 'Encounter/6', 'Patient/1', '123', null, 97, 'already_reference_unit', 'final');

-- Condition:
-- 1-5: valid verification statuses ('confirmed', 'unconfirmed', 'provisional', 'differential', null)
-- 6-7: excluded verification statuses ('refuted', 'entered-in-error')
-- 8: valid status, but excluded encounter ('Encounter/6')
INSERT INTO db2dataprocessor_out.v_condition_last_version (con_id, con_encounter_calculated_ref, con_patient_ref,
                                                         con_code_system, con_code_code, con_onsetperiod_start,
                                                         con_onsetperiod_end, con_onsetdatetime, con_recordeddate,
                                                         con_verificationstatus_code)
VALUES ('1', 'Encounter/1', 'Patient/1', 'con', '1', null, null, null, null, 'confirmed'),
       ('2', 'Encounter/1', 'Patient/1', 'con', '2', null, null, null, null, 'unconfirmed'),
       ('3', 'Encounter/1', 'Patient/1', 'con', '3', null, null, null, null, 'provisional'),
       ('4', 'Encounter/1', 'Patient/1', 'con', '4', null, null, null, null, 'differential'),
       ('5', 'Encounter/1', 'Patient/1', 'con', '5', null, null, null, null, null),
       ('6', 'Encounter/1', 'Patient/1', 'con', '6', null, null, null, null, 'refuted'),
       ('7', 'Encounter/1', 'Patient/1', 'con', '7', null, null, null, null, 'entered-in-error'),
       ('8', 'Encounter/6', 'Patient/1', 'con', '1', null, null, null, null, 'confirmed');

-- Procedure:
-- 1-5: valid statuses ('completed', 'in-progress', 'not-done', 'stopped', null)
-- 6: excluded status ('entered-in-error')
-- 7: valid status, but excluded encounter ('Encounter/6')
INSERT INTO db2dataprocessor_out.v_procedure_last_version (proc_id, proc_encounter_calculated_ref, proc_patient_ref,
                                                         proc_code_system, proc_code_code, proc_performedperiod_start,
                                                         proc_performedperiod_end, proc_performeddatetime, proc_status)
VALUES ('1', 'Encounter/1', 'Patient/1', 'pro', '1', null, null, null, 'completed'),
       ('2', 'Encounter/1', 'Patient/1', 'pro', '2', null, null, null, 'in-progress'),
       ('3', 'Encounter/1', 'Patient/1', 'pro', '3', null, null, null, 'not-done'),
       ('4', 'Encounter/1', 'Patient/1', 'pro', '4', null, null, null, 'stopped'),
       ('5', 'Encounter/1', 'Patient/1', 'pro', '5', null, null, null, null),
       ('6', 'Encounter/1', 'Patient/1', 'pro', '6', null, null, null, 'entered-in-error'),
       ('7', 'Encounter/6', 'Patient/1', 'pro', '1', null, null, null, 'completed');

-- MedicationAdministration:
-- 1-5: valid statuses ('completed', 'in-progress', 'on-hold', 'stopped', null)
-- 6-7: excluded statuses ('not-done', 'entered-in-error')
-- 8: valid status, but excluded encounter ('Encounter/6')
INSERT INTO db2dataprocessor_out.v_medicationadministration_last_version (medadm_id, medadm_encounter_calculated_ref,
                                                                        medadm_patient_ref, medadm_effectivedatetime,
                                                                        medadm_effectiveperiod_start,
                                                                        medadm_effectiveperiod_end,
                                                                        medadm_medication_system,
                                                                        medadm_medication_code, medadm_status)
VALUES ('1', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ma', '1', 'completed'),
       ('2', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ma', '2', 'in-progress'),
       ('3', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ma', '3', 'on-hold'),
       ('4', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ma', '4', 'stopped'),
       ('5', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ma', '5', null),
       ('6', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ma', '6', 'not-done'),
       ('7', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ma', '7', 'entered-in-error'),
       ('8', 'Encounter/6', 'Patient/1', '2020-01-01', null, null, 'ma', '1', 'completed');

-- MedicationStatement:
-- 1-5: valid statuses ('active', 'completed', 'intended', 'stopped', null)
-- 6: excluded status ('entered-in-error')
-- 7: valid status, but excluded encounter ('Encounter/6')
INSERT INTO db2dataprocessor_out.v_medicationstatement_last_version (medstat_id, medstat_encounter_calculated_ref,
                                                                   medstat_patient_ref, medstat_effectivedatetime,
                                                                   medstat_effectiveperiod_start,
                                                                   medstat_effectiveperiod_end,
                                                                   medstat_medication_system, medstat_medication_code,
                                                                   medstat_status)
VALUES ('1', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ms', '1', 'active'),
       ('2', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ms', '2', 'completed'),
       ('3', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ms', '3', 'intended'),
       ('4', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ms', '4', 'stopped'),
       ('5', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ms', '5', null),
       ('6', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ms', '6', 'entered-in-error'),
       ('7', 'Encounter/6', 'Patient/1', '2020-01-01', null, null, 'ms', '1', 'active');

-- MedicationRequest:
-- 1-5: valid statuses ('active', 'completed', 'draft', 'unknown', null)
-- 6-9: excluded statuses ('on-hold', 'cancelled', 'entered-in-error', 'stopped')
-- 10: valid status, but excluded encounter ('Encounter/6')
INSERT INTO db2dataprocessor_out.v_medicationrequest_last_version (medreq_id, medreq_encounter_calculated_ref,
                                                                 medreq_patient_ref, medreq_authoredon,
                                                                 medreq_medication_system, medreq_medication_code,
                                                                 medreq_status)
VALUES ('1', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '1', 'active'),
       ('2', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '2', 'completed'),
       ('3', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '3', 'draft'),
       ('4', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '4', 'unknown'),
       ('5', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '5', null),
       ('6', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '6', 'on-hold'),
       ('7', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '7', 'cancelled'),
       ('8', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '8', 'entered-in-error'),
       ('9', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '9', 'stopped'),
       ('10', 'Encounter/6', 'Patient/1', '2020-01-01', 'mr', '1', 'active');