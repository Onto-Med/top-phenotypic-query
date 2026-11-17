INSERT INTO db2dataprocessor_out.v_patient_last_version (pat_id, pat_birthdate, pat_gender)
VALUES ('1', '2001-01-01', 'male');

INSERT INTO db2dataprocessor_out.v_encounter_last_version (enc_id, enc_patient_ref, enc_class_code, enc_period_start,
                              enc_period_end, enc_type_code, enc_age_at_admission)
VALUES ('1', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 18),
       ('2', 'Patient/1', 'IMP', '2017-01-01', '2017-02-01', 'einrichtungskontakt', 17);


INSERT INTO db2dataprocessor_out.v_observation_last_version (obs_id, obs_encounter_calculated_ref, obs_patient_ref, analysis_loinc_code,
                                obs_effectivedatetime, analysis_value)
VALUES ('1', 'Encounter/1', 'Patient/1', '123', null, 31),
       ('2', 'Encounter/2', 'Patient/1', '123', null, 31);

INSERT INTO db2dataprocessor_out.v_condition_last_version (con_id, con_encounter_calculated_ref, con_patient_ref, con_code_system, con_code_code, con_onsetperiod_start, con_onsetperiod_end, con_onsetdatetime, con_recordeddate)
VALUES ('1', 'Encounter/1', 'Patient/1', 'con', '123', null, null, null, null),
       ('2', 'Encounter/2', 'Patient/1', 'con', '123', null, null, null, null);
       
INSERT INTO db2dataprocessor_out.v_procedure_last_version (proc_id, proc_encounter_calculated_ref, proc_patient_ref, proc_code_system, proc_code_code, proc_performedperiod_start, proc_performedperiod_end, proc_performeddatetime)
VALUES ('1', 'Encounter/1', 'Patient/1', 'pro', '123', null, null, null),
       ('2', 'Encounter/2', 'Patient/1', 'pro', '123', null, null, null);

INSERT INTO db2dataprocessor_out.v_medicationadministration_last_version (medadm_id, medadm_encounter_calculated_ref, medadm_patient_ref,
                                             medadm_effectivedatetime, medadm_effectiveperiod_start, medadm_effectiveperiod_end,
                                                             medadm_medication_system, medadm_medication_code)
VALUES ('1', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ma', '123'),
       ('2', 'Encounter/2', 'Patient/1', '2020-01-01', null, null, 'ma', '123');

INSERT INTO db2dataprocessor_out.v_medicationrequest_last_version (medreq_id, medreq_encounter_calculated_ref, medreq_patient_ref,
                                      medreq_authoredon, medreq_medication_system, medreq_medication_code)
VALUES ('1', 'Encounter/1', 'Patient/1', '2020-01-01', 'mr', '123'),
       ('2', 'Encounter/2', 'Patient/1', '2020-01-01', 'mr', '123');


INSERT INTO db2dataprocessor_out.v_medicationstatement_last_version (medstat_id, medstat_encounter_calculated_ref, medstat_patient_ref,
            medstat_effectivedatetime, medstat_effectiveperiod_start, medstat_effectiveperiod_end, medstat_medication_system, medstat_medication_code)
VALUES ('1', 'Encounter/1', 'Patient/1', '2020-01-01', null, null, 'ms', '123'),
       ('2', 'Encounter/2', 'Patient/1', '2020-01-01', null, null, 'ms', '123');
