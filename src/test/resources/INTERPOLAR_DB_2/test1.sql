INSERT INTO db2dataprocessor_out.patient (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0001', '1940-01-01', 'female');

INSERT INTO db2dataprocessor_out.encounter (enc_id, enc_patient_ref, enc_diagnosis_condition_ref, enc_class_code, enc_period_start,
                              enc_period_end)
VALUES ('HOSP-0001-E-11', 'Patient/HOSP-0001', '', 'IMP', '2016-01-01', '2016-02-01'),
       ('HOSP-0001-E-22', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0001-E-33', 'Patient/HOSP-0001', '', 'IMP', '2018-01-01', '2018-02-01'),
       ('HOSP-0001-E-44', 'Patient/HOSP-0001', '', 'IMP', '2019-01-01', '2019-02-01'),
       ('HOSP-0001-E-55', 'Patient/HOSP-0001', '', 'AMB', '2020-01-01', '2020-02-01');

INSERT INTO db2dataprocessor_out.observation (obs_id, obs_encounter_ref, obs_patient_ref, obs_code_system, obs_code_code,
                                obs_effectivedatetime, obs_valuequantity_value, obs_valuequantity_code)
VALUES ('HOSP-0001-E-11-OL-1', 'Encounter/HOSP-0001-E-11', 'Patient/HOSP-0001', 'http://loinc.org', '1234-5',
        '2016-01-16T12:43:00', 160, 'mg/dL'),
       ('HOSP-0001-E-11-OL-1', 'Encounter/HOSP-0001-E-11', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0',
        '2016-01-16T12:43:00', 160, 'mg/dL'),
       ('HOSP-0001-E-22-OL-1', 'Encounter/HOSP-0001-E-22', 'Patient/HOSP-0001', 'http://loinc.org', '1234-5',
        '2017-01-17T06:35:00', 170, 'mg/dL'),
       ('HOSP-0001-E-22-OL-1', 'Encounter/HOSP-0001-E-22', 'Patient/HOSP-0001', 'http://loinc.org', '678-9',
        '2017-01-17T06:35:00', 170, 'mg/dL'),
       ('HOSP-0001-E-33-OL-1', 'Encounter/HOSP-0001-E-33', 'Patient/HOSP-0001', 'http://loinc.org', '678-9',
        '2018-01-18T12:43:00', 180, 'mg/dL'),
       ('HOSP-0001-E-33-OL-1', 'Encounter/HOSP-0001-E-33', 'Patient/HOSP-0001', 'http://loinc.org', '38483-4',
        '2018-01-18T12:43:00', 180, 'mg/dL'),
       ('HOSP-0001-E-44-OL-1', 'Encounter/HOSP-0001-E-44', 'Patient/HOSP-0001', 'http://loinc.org', '1234-5',
        '2019-01-19T06:35:00', 190, 'mg/dL'),
       ('HOSP-0001-E-44-OL-1', 'Encounter/HOSP-0001-E-44', 'Patient/HOSP-0001', 'http://loinc.org', '678-9',
        '2019-01-19T06:35:00', 190, 'mg/dL'),
       ('HOSP-0001-E-55-OL-1', 'Encounter/HOSP-0001-E-55', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0',
        '2020-01-20T12:43:00', 200, 'mg/dL'),
       ('HOSP-0001-E-55-OL-1', 'Encounter/HOSP-0001-E-55', 'Patient/HOSP-0001', 'http://loinc.org', '1234-5',
        '2020-01-20T12:43:00', 200, 'mg/dL');