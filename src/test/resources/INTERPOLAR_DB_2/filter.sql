INSERT INTO db2dataprocessor_out.v_patient_all (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0001', '2001-01-01', 'male');

INSERT INTO db2dataprocessor_out.v_encounter_all (enc_id, enc_patient_ref, enc_diagnosis_condition_ref, enc_class_code, enc_period_start,
                              enc_period_end)
VALUES ('HOSP-0001-E-0', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0001-E-1', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0001-E-2', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0001-E-3', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0001-E-4', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0001-E-5', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-02-01');

INSERT INTO db2dataprocessor_out.v_medication_all (med_id, med_code_system, med_code_code)
VALUES ('Medication-1', 'http://fhir.de/CodeSystem/bfarm/atc', 'B01AE07');

INSERT INTO db2dataprocessor_out.v_medicationadministration_all (medadm_id, medadm_main_encounter_calculated_ref, medadm_patient_ref,
                                             medadm_effectivedatetime, medadm_medicationreference_ref)
VALUES ('HOSP-0001-E-1-MA-1', 'Encounter/HOSP-0001-E-1', 'Patient/HOSP-0001', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0001-E-2-MA-1', 'Encounter/HOSP-0001-E-2', 'Patient/HOSP-0001', '2020-01-02', 'Medication/Medication-1'),
       ('HOSP-0001-E-3-MA-1', 'Encounter/HOSP-0001-E-3', 'Patient/HOSP-0001', null, 'Medication/Medication-1'),
       ('HOSP-0001-E-5-MA-1', 'Encounter/HOSP-0001-E-5', 'Patient/HOSP-0001', '2020-01-02', 'Medication/Medication-1');

INSERT INTO db2dataprocessor_out.v_observation_all (obs_id, obs_main_encounter_calculated_ref, obs_patient_ref, obs_code_system, obs_code_code,
                                obs_effectivedatetime, obs_valuequantity_value)
VALUES ('HOSP-0001-E-1-OL-1', 'Encounter/HOSP-0001-E-1', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0',
        '2020-01-01', 10),
       ('HOSP-0001-E-2-OL-1', 'Encounter/HOSP-0001-E-2', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0',
        '2020-01-01', 31),
       ('HOSP-0001-E-3-OL-1', 'Encounter/HOSP-0001-E-3', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0',
        '2020-01-01', 10),
       ('HOSP-0001-E-4-OL-1', 'Encounter/HOSP-0001-E-4', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0',
        '2020-01-01', 10),
       ('HOSP-0001-E-5-OL-1', 'Encounter/HOSP-0001-E-5', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0',
        '2020-01-01', 10);